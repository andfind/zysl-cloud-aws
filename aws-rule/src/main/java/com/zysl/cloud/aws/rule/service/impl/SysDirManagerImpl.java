package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.enums.FileDirEnum;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysDirListRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.domain.bo.ObjectInfoBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.rule.service.ISysDirManager;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.MyPage;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class SysDirManagerImpl implements ISysDirManager {
	
	@Autowired
	private IS3FolderService s3FolderService;
	@Autowired
	private IS3FileService s3FileService;
	@Autowired
	private ISysFileManager sysFileManager;
	
	@Override
	public void mkdir(SysDirRequest request){
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = new S3ObjectBO();
			ObjectFormatUtils.setBucketAndPath(s3ObjectBO,request.getPath());
			s3FolderService.create(s3ObjectBO);
		}
		
	}
	
	
	@Override
	public List<SysFileDTO> list(SysDirListRequest request, MyPage myPage){
		log.info("list-param:{}",request);
		List<SysFileDTO> list = new ArrayList<>();
		
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = new S3ObjectBO();
			ObjectFormatUtils.setBucketAndPath(s3ObjectBO,request.getPath());
			S3ObjectBO rstBo = (S3ObjectBO)s3FolderService.list(s3ObjectBO,myPage);
			if(CollectionUtils.isEmpty(rstBo.getFileList())){
				for(ObjectInfoBO bo:rstBo.getFileList()){
					SysFileDTO dto = new SysFileDTO();
					dto.setIsFile(FileDirEnum.FILE.getCode());
					dto.setLastModified(DateUtils.from(bo.getUploadTime()));
					dto.setContentMd5(bo.getContentMd5());
					dto.setSize(bo.getFileSize());
					ObjectFormatUtils.setPathAndFileName(dto,bo.getBucket(),bo.getKey());
				}
			}
			if(CollectionUtils.isEmpty(rstBo.getFolderList())){
				for(ObjectInfoBO bo:rstBo.getFolderList()){
					SysFileDTO dto = new SysFileDTO();
					dto.setIsFile(FileDirEnum.DIR.getCode());
					dto.setLastModified(DateUtils.from(bo.getUploadTime()));
					ObjectFormatUtils.setPathAndFileName(dto,bo.getBucket(),bo.getKey());
				}
			}
			
		}
		return list;
	}
	
	
	@Override
	public void copy(SysDirRequest source,SysDirRequest target,Boolean isOverWrite){
		log.info("copy-source:{},target:{}",source,target);
		List<SysFileDTO> list = null;
		if(FileSysTypeEnum.S3.getCode().equals(source.getType())){
			SysDirListRequest request = BeanCopyUtil.copy(source,SysDirListRequest.class);
			MyPage myPage = new MyPage(1,999999999);
			list = list(request,myPage);
			if(list == null){
				throw new AppLogicException(ErrCodeEnum.COPY_SOURCE_NOT_EXIST.getCode());
			}
		}
		SysDirRequest dirRequest = null;
		SysFileRequest sourceFile = null;
		SysFileRequest targetFile = null;
		for(SysFileDTO dto:list){
			if(FileDirEnum.DIR.getCode().intValue() == dto.getIsFile()){
				dirRequest = BeanCopyUtil.copy(target,SysDirRequest.class);
				dirRequest.setPath(target.getPath() + dto.getPath().replace(source.getPath(),""));
				mkdir(dirRequest);
			}else{
				sourceFile = BeanCopyUtil.copy(dto,SysFileRequest.class);
				targetFile = BeanCopyUtil.copy(target,SysFileRequest.class);
				dirRequest.setPath(target.getPath() + dto.getPath().replace(source.getPath(),""));
				
				sysFileManager.copy(sourceFile,targetFile,isOverWrite);
			}
		}
	
	}
	
	@Override
	public void delete(SysDirRequest request){
		log.info("delete-request:{}",request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = new S3ObjectBO();
			ObjectFormatUtils.setBucketAndPath(s3ObjectBO,request.getPath());
			s3FolderService.delete(s3ObjectBO);
		}
	}
	
	@Override
	public void move(SysDirRequest source,SysDirRequest target){
		log.info("move-source:{},target:{}",source,target);
		//判断是否目标地址是否存在,存在则不覆盖
		if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
			S3ObjectBO s3ObjectBO = new S3ObjectBO();
			ObjectFormatUtils.setBucketAndPath(s3ObjectBO,target.getPath());
			Object obj = s3FileService.getBaseInfo(s3ObjectBO);
			if(obj != null){
				throw new AppLogicException(ErrCodeEnum.COPY_TARGET_EXIST.getCode());
			}
		}
		copy(source,target,Boolean.FALSE);
		delete(source);
	}
	
	
}
