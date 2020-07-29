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
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.FilePartInfoBO;
import com.zysl.cloud.aws.domain.bo.MultipartUploadBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.LogHelper;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class SysFileManagerImpl implements ISysFileManager {
	
	@Autowired
	private IS3FileService s3FileService;
	@Autowired
	private WebConfig webConfig;
	
	
	@Override
	public void copy(SysFileRequest source, SysFileRequest target, Boolean isOverWrite) {
		LogHelper.info(getClass(),"copyFile.param",source.getEsLogMsg(),source + "->" + target);
		//不覆盖
		if(isOverWrite == null || !isOverWrite){
			if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
				Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(target));
				if(obj != null){
					LogHelper.info(getClass(),"copyFile.rst",source.getEsLogMsg(),"copy.target.is.exist");
					return;
				}
			}
		}
		
		copyFile(source,target);
	}
	
	@Override
	public void move(SysFileRequest source, SysFileRequest target) {
		LogHelper.info(getClass(),"moveFile.param",source.getEsLogMsg(),source + "->" + target);
		if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(target));
			if(obj != null  ){
				LogHelper.warn(getClass(),"moveFile.rst",source.getEsLogMsg(),"move.target.is.exist");
				throw new AppLogicException(ErrCodeEnum.MOVE_TARGET_EXIST.getCode());
			}

		}
		if(FileSysTypeEnum.S3.getCode().equals(source.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(source));
			if(obj == null){
				LogHelper.warn(getClass(),"moveFile.rst",source.getEsLogMsg(),"move.source.is.not.exist");
				throw new AppLogicException(ErrCodeEnum.MOVE_SOURCE_NOT_EXIST.getCode());
			}
			S3ObjectBO rst = (S3ObjectBO)obj;
			if(rst.getContentLength() > webConfig.getCopyMaxFileSize() * 1024 * 1024L){
				LogHelper.warn(getClass(),"moveFile.rst",source.getEsLogMsg(),"source.size.must.lower.then " + webConfig.getCopyMaxFileSize());
				throw new AppLogicException(ErrCodeEnum.COPY_SOURCE_SIZE_TOO_LONG.getCode());
			}
		}
		
		
		
		copyFile(source,target);
		//删除源数据
		delete(source);
	}
	
	@Override
	public void delete(SysFileRequest request) {
		LogHelper.warn(getClass(),"deleteFile.param",request.getEsLogMsg(),request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			s3ObjectBO.setDeleteStore(DeleteStoreEnum.NOCOVER.getCode());
			s3FileService.delete(s3ObjectBO);
		}
	}
	
	@Override
	public SysFileDTO info(SysFileRequest request){
		LogHelper.info(getClass(),"infoFile.param",request.getEsLogMsg(),request);
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
				
				String verNo = S3Utils.getTagValue(rst.getTagList(), S3TagKeyEnum.VERSION_NUMBER.getCode());
				if(StringUtils.isNotEmpty(verNo)){
					dto.setVersionNo(Integer.parseInt(verNo));
				}
				LogHelper.info(getClass(),"infoFile.rst",request.getEsLogMsg(),dto);
				return dto;
			}
			
		}
		return null;
	}
	
	
	@Override
	public void upload(SysFileRequest request,byte[] bodys,Boolean isOverWrite){
		LogHelper.info(getClass(),"uploadFile.rst",request.getEsLogMsg(),request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			Object obj = s3FileService.getBaseInfo(ObjectFormatUtils.createS3ObjectBO(request));
			if(obj != null && isOverWrite != null && !isOverWrite){
				LogHelper.info(getClass(),"uploadFile.rst",request.getEsLogMsg(),"upload.source.is.exist");
				return;
			}
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			s3ObjectBO.setBodys(bodys);
			
			List<TagBO> tagList = new ArrayList<>();
			TagBO tagBO = new TagBO(S3TagKeyEnum.VERSION_NUMBER.getCode(),createVersionNo(s3ObjectBO));
			tagList.add(tagBO);
			s3ObjectBO.setTagList(tagList);
			
			s3FileService.create(s3ObjectBO);
		}
		
	}
	
	private String createVersionNo(S3ObjectBO s3ObjectBO){
		LogHelper.info(getClass(),"createVersionNo.param",s3ObjectBO.bucketKey(),s3ObjectBO);
		int verNoInt = 1;
		Object obj = s3FileService.getDetailInfo(s3ObjectBO);
		if (obj != null ) {
			S3ObjectBO bo = (S3ObjectBO)obj;
			if(!CollectionUtils.isEmpty(bo.getTagList())){
				try{
					String verNo = S3Utils.getTagValue(bo.getTagList(), S3TagKeyEnum.VERSION_NUMBER.getCode());
					if(StringUtils.isNotEmpty(verNo)){
						verNoInt =  Integer.parseInt(verNo) + 1;
					}
				}catch (NumberFormatException e){
					LogHelper.warn(getClass(),"createVersionNo",s3ObjectBO.bucketKey(),"NumberFormatException");
				}
			}
		}
		
		return String.valueOf(verNoInt);
	}
	
	@Override
	public byte[] getFileBodys(SysFileRequest request,String range){
		LogHelper.info(getClass(),"getFileBodys.param",request.getEsLogMsg(),request);
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
		LogHelper.info(getClass(),"listVersions.param",request.getEsLogMsg(),request);
		List<SysFileDTO> rstList = new ArrayList<>();
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			
			List<S3ObjectBO> list = (List<S3ObjectBO>)s3FileService.getVersions(s3ObjectBO);
			for(S3ObjectBO bo : list){
				SysFileDTO dto = ObjectFormatUtils.s3ObjectBOToSysFileDTO(bo);
				List<TagBO> tagList = s3FileService.getTags(bo);
				String verNo = S3Utils.getTagValue(tagList,S3TagKeyEnum.VERSION_NUMBER.getCode());
				if(StringUtils.isNotEmpty(verNo)){
					dto.setVersionNo(Integer.parseInt(verNo));
				}
				rstList.add(dto);
			}
			
			LogHelper.info(getClass(),"listVersions.rst",request.getEsLogMsg(),rstList);
			return rstList;
		}
		
		return null;
	}
	
	@Override
	public String multiUploadStart(SysFileMultiStartRequest request){
		LogHelper.info(getClass(),"multiUploadStart.param",request.getEsLogMsg(),request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			
			//已存在分片上传对象
			FilePartInfoDTO dto = multiUploadInfo(request);
			if(dto != null){
				LogHelper.info(getClass(),"multiUploadStart.param",request.getEsLogMsg(),"multiUploadStart.exist");
				throw new AppLogicException(ErrCodeEnum.MULTI_UPLOAD_START_FILE_EXIST.getCode());
			}
			
			return  s3FileService.createMultipartUpload(s3ObjectBO);
		}
		
		return null;
	}
	
	
	@Override
	public void multiUploadAbort(SysFileMultiRequest request){
		LogHelper.info(getClass(),"multiUploadAbort.param",request.getEsLogMsg(),request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			s3ObjectBO.setUploadId(request.getUploadId());
			
			s3FileService.abortMultipartUpload(s3ObjectBO);
		}
	}
	
	@Override
	public String multiUploadBodys(SysFileMultiUploadRequest request,byte[] bodys){
		LogHelper.info(getClass(),"multiUploadUpload.param",request.getEsLogMsg(),request);
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
		LogHelper.info(getClass(),"multiUploadComplete.param",request.getEsLogMsg(),request);
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
		LogHelper.info(getClass(),"multiUploadInfoList.param",request.getEsLogMsg(),request);
		FilePartInfoDTO dto = null;
		if(FileSysTypeEnum.S3.getCode().equals(request.getType())){
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(fileRequest);
			
			String uploadId = s3FileService.getMultiUploadId(s3ObjectBO);
			
			if(StringUtils.isBlank(uploadId)){
				LogHelper.info(getClass(),"multiUploadInfoList.rst",request.getEsLogMsg(),"uploadId is null");
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

	/**
	 * 复制文件--旧方法
	 * @description
	 * @author miaomingming
	 * @date 17:33 2020/6/11
	 * @param source
	 * @param target
	 * @return void
	 **/
  private void copyFile_old(SysFileRequest source, SysFileRequest target) {
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
	
  	/**
  	 * 复制文件
  	 * @description
  	 * @author miaomingming
  	 * @date 17:34 2020/6/11
  	 * @param source
  	 * @param target
  	 * @return void
  	 **/
	private void copyFile(SysFileRequest source, SysFileRequest target){
		if(FileSysTypeEnum.S3.getCode().equals(source.getType())){
			if(FileSysTypeEnum.S3.getCode().equals(target.getType())){
				S3ObjectBO sourceBO = ObjectFormatUtils.createS3ObjectBO(source);
				S3ObjectBO targetBO = ObjectFormatUtils.createS3ObjectBO(target);
				s3FileService.copy(sourceBO,targetBO);
			}
		}
	}
	
}
