package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.common.AppLogicException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SysFileManagerImpl implements ISysFileManager {
	
	@Autowired
	private IS3FileService s3FileService;
	
	
	@Override
	public void copy(SysFileRequest source, SysFileRequest target, Boolean isOverWrite) {
		log.info("copy-source:{},target:{}",source,target);
		if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(target));
			if(obj != null){
				if(isOverWrite != null && !isOverWrite){
					log.info("-copy.target.is.exist:{}",target);
					return;
				}
			}
		}
		
		moveFile(source,target);
	}
	
	@Override
	public void move(SysFileRequest source, SysFileRequest target) {
		log.info("move-source:{},target:{}",source,target);
		if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(target));
			if(obj != null){
				log.info("-move.target.is.exist:{}",target);
				throw new AppLogicException(ErrCodeEnum.MOVE_TARGET_EXIST.getCode());
			}
		}
		moveFile(source,target);
		//删除源数据
		delete(source);
	}
	
	@Override
	public void delete(SysFileRequest request) {
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			
			s3FileService.delete(s3ObjectBO);
		}
	}
	
	@Override
	public SysFileDTO info(SysFileRequest request){
		SysFileDTO dto  = null;
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			
			Object obj = s3FileService.getBaseInfo(s3ObjectBO);
			if(obj != null){
				S3ObjectBO rst = (S3ObjectBO)obj;
				dto = new SysFileDTO();
				dto.setContentMd5(rst.getContentMD5());
				dto.setLastModified(rst.getLastModified());
				dto.setSize(rst.getContentLength());
				dto.setVersionId(rst.getVersionId());
				dto.setPath(rst.getBucketName() + ":/" + rst.getPath());
				return dto;
			}
			
		}
		return null;
	}
	
	
	@Override
	public void upload(SysFileRequest request,byte[] bodys,Boolean isOverWrite){
		log.info("upload-source:{}",request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(request));
			if(obj != null && isOverWrite != null && !isOverWrite){
				log.info("-upload.source.is.exist:{}",request);
				throw new AppLogicException(ErrCodeEnum.MOVE_TARGET_EXIST.getCode());
			}
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			s3ObjectBO.setBodys(bodys);
			s3FileService.create(s3ObjectBO);
		}
		
	}
	
	@Override
	public byte[] getFileBodys(SysFileRequest request,String range){
		log.info("getFileBodys-source:{}",request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			s3ObjectBO.setRange(range);
			S3ObjectBO rst = (S3ObjectBO)s3FileService.getInfoAndBody(s3ObjectBO);
			
			return rst == null ? null : rst.getBodys();
		}
		return null;
	}
	
	
	@Override
	public List<SysFileDTO> listVersions(SysFileListRequest request){
		log.info("listVersions-source:{}",request);
		List<SysFileDTO> rstList = new ArrayList<>();
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			
			List<S3ObjectBO> list = (List<S3ObjectBO>)s3FileService.getVersions(s3ObjectBO);
			for(S3ObjectBO bo : list){
				rstList.add(ObjectFormatUtils.s3ObjectBOToSysFileDTO(bo));
			}
			
			return rstList;
		}
		
		return null;
	}
	
	private void moveFile(SysFileRequest source, SysFileRequest target){
		byte[] bodys = null;
		//源数据读取
		if(FileSysTypeEnum.S3.getCode().equals(source.getType())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(source);
			S3ObjectBO rst = (S3ObjectBO)s3FileService.getInfoAndBody(s3ObjectBO);
			bodys = rst.getBodys();
		}
		//复制到目标地址
		if(bodys == null){
			return;
		}
		if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(target);
			s3ObjectBO.setBodys(bodys);
			s3FileService.create(s3ObjectBO);
		}
	}
	
}
