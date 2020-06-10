package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.FilePartInfoDTO;
import com.zysl.cloud.aws.api.dto.PartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.api.enums.FileDirEnum;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiCompleteRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiStartRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiUploadRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.FilePartInfoBO;
import com.zysl.cloud.aws.domain.bo.MultipartUploadBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.service.utils.ObjectFormatUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
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
	@Autowired
	private WebConfig webConfig;
	
	
	@Override
	public void copy(SysFileRequest source, SysFileRequest target, Boolean isOverWrite) {
		log.info("copy-source:{},target:{}",source,target);
		//不覆盖
		if(isOverWrite == null || !isOverWrite){
			if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
				Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(target));
				if(obj != null){
					log.info("-copy.target.is.exist:{}",target);
					return;
				}
			}
		}
		
		copyFile(source,target);
	}
	
	@Override
	public void move(SysFileRequest source, SysFileRequest target) {
		log.info("move-source:{},target:{}",source,target);
		if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(target));
			if(obj != null  ){
				log.info("-move.target.is.exist:{}",target);
				throw new AppLogicException(ErrCodeEnum.MOVE_TARGET_EXIST.getCode());
			}

		}
		if(FileSysTypeEnum.S3.getCode().equals(source.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(source));
			if(obj == null){
				log.info("-move.source.is.not.exist:{}",source);
				throw new AppLogicException(ErrCodeEnum.MOVE_SOURCE_NOT_EXIST.getCode());
			}
			S3ObjectBO rst = (S3ObjectBO)obj;
			if(rst.getContentLength() > webConfig.getCopyMaxFileSize() * 1024 * 1024L){
				log.warn("move-source.size.must.lower.then:{}M",webConfig.getCopyMaxFileSize());
				throw new AppLogicException(ErrCodeEnum.COPY_SOURCE_SIZE_TOO_LONG.getCode());
			}
		}
		
		
		
		copyFile(source,target);
		//删除源数据
		delete(source);
	}
	
	@Override
	public void delete(SysFileRequest request) {
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			s3ObjectBO.setDeleteStore(DeleteStoreEnum.NOCOVER.getCode());
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
				dto.setType(request.getType());
				dto.setServerNo(request.getServerNo());
				dto.setFileName(s3ObjectBO.getFileName());
				dto.setIsFile(FileDirEnum.FILE.getCode());
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
	
	@Override
	public String multiUploadStart(SysFileMultiStartRequest request){
		log.info("multiUploadStart-source:{}",request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			
			//已存在分片上传对象
			FilePartInfoDTO dto = multiUploadInfo(request);
			if(dto != null){
				log.warn("multiUploadStart.exist:{}",request);
				throw new AppLogicException(ErrCodeEnum.MULTI_UPLOAD_START_FILE_EXIST.getCode());
			}
			
			return  s3FileService.createMultipartUpload(s3ObjectBO);
		}
		
		return null;
	}
	
	
	@Override
	public void multiUploadAbort(SysFileMultiRequest request){
		log.info("multiUploadAbort-source:{}",request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			s3ObjectBO.setUploadId(request.getUploadId());
			
			s3FileService.abortMultipartUpload(s3ObjectBO);
		}
	}
	
	@Override
	public String multiUploadBodys(SysFileMultiUploadRequest request,byte[] bodys){
		log.info("multiUploadUpload-source:{}",request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			s3ObjectBO.setBodys(bodys);
			s3ObjectBO.setUploadId(request.getUploadId());
			s3ObjectBO.setPartNumber(request.getPartNumber());
			
			S3ObjectBO rst = (S3ObjectBO)s3FileService.uploadPart(s3ObjectBO);
			return StringUtils.isBlank(rst.getETag()) ? null : rst.getETag().replaceAll("\"","");
		}
		return null;
	}
	
	@Override
	public void multiUploadComplete(SysFileMultiCompleteRequest request){
		log.info("multiUploadComplete-source:{}",request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			s3ObjectBO.setUploadId(request.getUploadId());
			s3ObjectBO.setETagList(BeanCopyUtil.copyList(request.getETagList(),MultipartUploadBO.class));
			
			s3FileService.completeMultipartUpload(s3ObjectBO);
		}
	}
	
	@Override
	public FilePartInfoDTO multiUploadInfo(SysFileMultiStartRequest request){
		log.info("multiUploadInfoList-source:{}",request);
		FilePartInfoDTO dto = null;
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			
			String uploadId = s3FileService.getMultiUploadId(s3ObjectBO);
			
			if(StringUtils.isBlank(uploadId)){
				return null;
			}
			dto = new FilePartInfoDTO();
			dto.setUploadId(uploadId);
			s3ObjectBO.setUploadId(uploadId);
			
			List<FilePartInfoBO> list  = s3FileService.listParts(s3ObjectBO);
			
			list.forEach(bo->bo.setETag(bo.getETag().replaceAll("\"","")));
			
			dto.setETagList(BeanCopyUtil.copyList(list, PartInfoDTO.class));
		}
		
		return dto;
	}
	
	private void copyFile(SysFileRequest source, SysFileRequest target){
		/*byte[] bodys = null;
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
		}*/
		
		
		if(FileSysTypeEnum.S3.getCode().equals(source.getType())){
			if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
				S3ObjectBO sourceBO = ObjectFormatUtils.createS3ObjectBO(source);
				S3ObjectBO targetBO = ObjectFormatUtils.createS3ObjectBO(target);
				s3FileService.copy(sourceBO,targetBO);
			}
		}
	}
	
}
