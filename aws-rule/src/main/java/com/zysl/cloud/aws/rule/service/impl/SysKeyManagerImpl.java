package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.PartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.dto.SysKeyFileDTO;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteListRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyMultiUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.LogConfig;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.FilePartInfoBO;
import com.zysl.cloud.aws.domain.bo.MultipartUploadBO;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.MyPage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.services.s3.S3Client;
import sun.misc.BASE64Decoder;

@Slf4j
@Service
public class SysKeyManagerImpl implements ISysKeyManager {
	
	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private IS3KeyService s3KeyService;
	@Autowired
	private WebConfig webConfig;
	@Autowired
	private BizConfig bizConfig;
	@Autowired
	private LogConfig logConfig;
	
	@Override
	public void create(SysKeyCreateRequest request){
		log.info(logConfig.getLogTemplate(),"create-param",request.getPath(),request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getScheme())){
			SysKeyUploadRequest sysKeyUploadRequest = BeanCopyUtil.copy(request,SysKeyUploadRequest.class);
			sysKeyUploadRequest.setIsCover(Boolean.TRUE);
			
			byte[] bodys = null;
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				if(StringUtils.isNotEmpty(request.getData())){
					bodys = decoder.decodeBuffer(request.getData());
				}
			} catch (IOException e) {
				log.error(logConfig.getLogTemplate(),"createKey",request.getPath(),"base64.to.bytes.ioException");
			}
			
			//没有上传base64 或者 上传后转化成功
			if(StringUtils.isEmpty(request.getData()) || bodys != null){
				upload(sysKeyUploadRequest,bodys);
			}
		}
	}
	
	@Override
	public void upload(SysKeyUploadRequest request,byte[] bodys){
		log.info(logConfig.getLogTemplate(),"upload-param",request.getPath(),request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getScheme())){
			S3KeyBO keyBO = BeanCopyUtil.copy(request,S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			//不覆盖
			if(request.getIsCover() == null || !request.getIsCover()){
				//判断是否存在，存在则返回
				if(s3KeyService.getBaseInfo(s3,keyBO) != null){
					throw new AppLogicException(ErrCodeEnum.S3_BUCKET_OBJECT_EXIST.getCode());
				}
			}
			keyBO.setBodys(bodys);
			//设置版本号
			List<TagBO> tagList = new ArrayList<>();
			tagList.add(new TagBO(S3TagKeyEnum.VERSION_NUMBER.getCode(),createVersionNo(keyBO)));
			if(StringUtils.isNotEmpty(request.getFileName())){
				tagList.add(new TagBO(S3TagKeyEnum.FILE_NAME.getCode(),request.getFileName()));
			}
			keyBO.setTagList(tagList);
			s3KeyService.create(s3,keyBO);
		}
	}
	
	@Override
	public SysKeyDTO info(SysKeyRequest request){
		log.info(logConfig.getLogTemplate(),"info-param",request.getPath(),request);
		SysKeyDTO dto = null;
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
		  	S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			
			S3KeyBO s3KeyBO = (S3KeyBO)s3KeyService.getBaseInfo(s3,keyBO);
			if(s3KeyBO == null){
				return null;
			}
			
			dto = ObjectFormatUtils.s3KeyBO2SysKeyDTO(s3KeyBO,s3KeyService.getTagList(s3,keyBO));
		}
		
		return dto;
	}
	
	@Override
	public List<SysKeyFileDTO> infoList(SysKeyRequest request, MyPage myPage){
		log.info(logConfig.getLogTemplate(),"infoList-param",request.getPath(),request);
		List<SysKeyFileDTO> list = new ArrayList<>();
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
			S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			
			List<S3KeyBO> s3KeyBOList = s3KeyService.list(s3,keyBO,myPage);
			
			if(!CollectionUtils.isEmpty(s3KeyBOList)){
				s3KeyBOList.forEach(bo->{
					bo.setBucket(keyBO.getBucket());
					list.add(ObjectFormatUtils.s3KeyBO2SysKeyFileDTO(bo,s3KeyService.getTagList(s3,bo)));
				});
			}
		}
		return  list;
	}
	
	
	@Override
	public 	List<SysKeyDTO> versionList(SysKeyRequest request, MyPage myPage){
		log.info(logConfig.getLogTemplate(),"versionList-param",request.getPath(),request);
		List<SysKeyDTO> list = new ArrayList<>();
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
			S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			
			List<S3KeyBO> s3KeyBOList = s3KeyService.getVersions(s3,keyBO,myPage);
			
			if(!CollectionUtils.isEmpty(s3KeyBOList)){
				s3KeyBOList.forEach(bo->{
					bo.setBucket(keyBO.getBucket());
					list.add(ObjectFormatUtils.s3KeyBO2SysKeyDTO(bo,s3KeyService.getTagList(s3,bo)));
				});
			}
		}
		return  list;
	}
	
	@Override
	public void delete(SysKeyDeleteRequest request){
		log.info(logConfig.getLogTemplate(),"delete-param",request.getPath(),request);
		if(FileSysTypeEnum.S3.getCode().equals(request.getScheme())){
			S3KeyBO keyBO = BeanCopyUtil.copy(request,S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			//指定版本则物理删除当前版本
			if(StringUtils.isNotEmpty(keyBO.getVersionId())){
				s3KeyService.delete(s3,keyBO);
			}else{
				List<S3KeyBO> s3KeyBOList = new ArrayList<>();
				MyPage myPage = new MyPage(1,BizConstants.MAX_PAGE_SIE);
				boolean hasNext = Boolean.TRUE;
				
				while (hasNext){
					//物理删除，需要删除所有版本
					if(request.getIsPhy() != null && request.getIsPhy()){
						s3KeyBOList = s3KeyService.getVersions(s3,keyBO,myPage);
					}else{
						//目录或单个文件
						s3KeyBOList = s3KeyService.list(s3,keyBO,myPage);
					}
					
					//删除当前对象
					s3KeyService.deleteList(s3,keyBO.getBucket(),s3KeyBOList);
					
					if(myPage.getPageCount() <= 1){
						hasNext = Boolean.FALSE;
					}
				}
				
			}
		}
	}
	
	@Override
	public void deleteList(SysKeyDeleteListRequest request){
		if(CollectionUtils.isEmpty(request.getPathList())){
			return;
		}
		log.info(logConfig.getLogTemplate(),"deleteList-param.size",request.getPathList().size(),request);
		
		List<S3KeyBO> keys = new ArrayList<>();
		String bucket = null;
		for(SysKeyDeleteRequest deleteRequest : request.getPathList()){
			if (FileSysTypeEnum.S3.getCode().equals(deleteRequest.getScheme())) {
				if(bucket == null){
					bucket = deleteRequest.getHost();
				}
				keys.add(new S3KeyBO(deleteRequest.getKey(),deleteRequest.getVersionId(),0L));
			}
		}
		
		//不为空说明是s3
		if(bucket != null){
			S3Client s3 = s3FactoryService.getS3ClientByBucket(bucket);
			s3KeyService.deleteList(s3,bucket,keys);
		}
		
	}
	
	@Override
	public byte[] getBody(SysKeyRequest request,String range){
		log.info(logConfig.getLogTemplate(),"getBody-param",request.getPath(),request);
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
		    S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
		    keyBO.setBucket(request.getHost());
		    keyBO.setRange(range);
	
     	    S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			if(s3KeyService.getInfoAndBody(s3,keyBO) != null){
				Date date1 = keyBO.getLastModified();
				Date date2 = DateUtils.createDate(bizConfig.DOWNLOAD_TIME);
				log.info(logConfig.getLogTemplate(),"getBody",request.getPath(),String.format("lastModified:%s",date1));
				
				//有一批数据写入时是直接base64的所以要解码
				if(DateUtils.doCompareDate(date1, date2) < 0 && keyBO.getBodys() != null){
					//进行解码
					try {
						BASE64Decoder decoder = new BASE64Decoder();
						return decoder.decodeBuffer(new String(keyBO.getBodys()));
					} catch (IOException e) {
						log.error(logConfig.getLogTemplate(),"getBody",request.getPath(),"base64.to.bytes.ioException");
					}
				}else {
					return keyBO.getBodys();
				}
			}
		}
		return null;
	}
	
	@Override
	public List<TagBO> tagList(SysKeyRequest request){
		log.info(logConfig.getLogTemplate(),"tagList-param",request.getPath(),request);
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
			S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			return s3KeyService.getTagList(s3,keyBO);
		}
		
		return null;
	}
	
	
	
	@Override
	public void copy(SysKeyRequest source,SysKeyRequest target,Boolean isCover){
		log.info(logConfig.getLogTemplate(),"copy",source.getPath(),target.getPath());
		MyPage myPage = new MyPage(1,1);
		//step 1.判断数据源是否存在
		if(FileSysTypeEnum.S3.getCode().equals(source.getScheme())){
			List<SysKeyFileDTO> dtoList = this.infoList(BeanCopyUtil.copy(source,SysKeyRequest.class),myPage);
			if(CollectionUtils.isEmpty(dtoList)){
				throw new AppLogicException(ErrCodeEnum.COPY_SOURCE_NOT_EXIST.getCode());
			}
		}
		//step 2. 不能覆盖时，需要判断目标是否存在
		if(FileSysTypeEnum.S3.getCode().equals(target.getScheme())){
			if(isCover == null || !isCover){
				List<SysKeyFileDTO> dtoList = this.infoList(BeanCopyUtil.copy(target,SysKeyRequest.class),myPage);
				if(!CollectionUtils.isEmpty(dtoList)){
					throw new AppLogicException(ErrCodeEnum.COPY_TARGET_EXIST.getCode());
				}
			}
		}

		//step 3.循环复制:
		S3Client sourceClient = s3FactoryService.getS3ClientByBucket(source.getHost());
		S3Client targetClient = s3FactoryService.getS3ClientByBucket(target.getHost());
		
		copyDir(sourceClient,targetClient,
				new S3KeyBO(source.getHost(),source.getKey()),
				source.getKey(),
				new S3KeyBO(target.getHost(),target.getKey()));
		
	}
	
	@Override
	public void multiAbort(SysKeyRequest request){
		log.info(logConfig.getLogTemplate(),"multiAbort-param",request.getPath(),request);
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
			S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			
			String uploadId = s3KeyService.getMultiUploadId(s3,keyBO);
			if(StringUtils.isEmpty(uploadId)){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}
			
			
			keyBO.setUploadId(uploadId);
			s3KeyService.abortMultipartUpload(s3,keyBO);
		}
	}
	
	@Override
	public List<PartInfoDTO> multiList(SysKeyRequest request){
		log.info(logConfig.getLogTemplate(),"multiList-param",request.getPath(),request);
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
			S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			
			String uploadId = s3KeyService.getMultiUploadId(s3,keyBO);
			if(StringUtils.isEmpty(uploadId)){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}
			
			keyBO.setUploadId(uploadId);
			List<FilePartInfoBO> list = s3KeyService.listParts(s3,keyBO);
			if(CollectionUtils.isEmpty(list)){
				list.forEach(bo->bo.setETag(bo.getETag().replaceAll("\"","")));
			}
			return BeanCopyUtil.copyList(list, PartInfoDTO.class);
		}
		
		return null;
	}
	
	@Override
	public Boolean multiUpload(SysKeyMultiUploadRequest request,byte[] bodys,Long contentLength){
		log.info(logConfig.getLogTemplate(),"multiUpload-param",request.getPath(),request);
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
			S3Client s3 = s3FactoryService.getS3ClientByBucket(request.getHost());
			
			S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			//step 1.检查是否存在分片对象->不存在则创建
			String uploadId = s3KeyService.getMultiUploadId(s3,keyBO);
			if(StringUtils.isEmpty(uploadId)){
				//标签
				List<TagBO> tagList = new ArrayList<>();
				tagList.add(new TagBO(S3TagKeyEnum.VERSION_NUMBER.getCode(),createVersionNo(keyBO)));
				if(StringUtils.isNotEmpty(request.getFileName())){
					tagList.add(new TagBO(S3TagKeyEnum.FILE_NAME.getCode(),request.getFileName()));
				}
				
				keyBO.setTagList(tagList);
				uploadId = s3KeyService.createMultipartUpload(s3,keyBO);
			}else if(request.getIsCover() != null && !request.getIsCover()){
				log.info(logConfig.getLogTemplate(),"multiUpload",request.getPath(),"not.cover.but.exist");
				throw new AppLogicException(ErrCodeEnum.S3_BUCKET_OBJECT_EXIST.getCode());
			}
			keyBO.setUploadId(uploadId);
			
			//step 2.上传分片数据，将提交的文件流按片切割
			int partNumber = 1;
			int partMax = webConfig.uploadMultiPartSize * 1024 * 1024;
			if(request.getPartNumber() != null && request.getPartNumber() > 0){
				partNumber = request.getPartNumber();
			}
			if(bodys != null && bodys.length > 0){
				int start=0,end=0;
				byte[] bytes = null;
				while(start < bodys.length){
					end = start + partMax > bodys.length ? bodys.length : start + partMax;
					bytes = Arrays.copyOfRange(bodys,start,end);
					start += partMax;
					
					keyBO.setBodys(bytes);
					keyBO.setPartNumber(partNumber++);
					s3KeyService.uploadPart(s3,keyBO);
				}
			}
			
			
			//step 3.全部上传完成后，提交合成
			if(contentLength == null || contentLength <= 0){
				return Boolean.FALSE;
			}
			List<FilePartInfoBO>  list = s3KeyService.listParts(s3,keyBO);
			long count = 0;
			List<MultipartUploadBO> eTagList = new ArrayList<>();
			if(!CollectionUtils.isEmpty(list)){
				for(int i=0;i<list.size();i++){
					FilePartInfoBO bo = list.get(i);
					//非最后一片的大小 小于规定分片大小
					if(bo.getSize().longValue() < partMax && i != list.size() - 1){
						return Boolean.FALSE;
					}
					count += bo.getSize().longValue();
					
					eTagList.add(new MultipartUploadBO(bo.getPartNumber(),bo.getETag()));
				}
				
				if(count >= contentLength){
					keyBO.setETagList(eTagList);
					log.info(logConfig.getLogTemplate(),"multiUpload",request.getPath(),"completeMultipartUpload");
					s3KeyService.completeMultipartUpload(s3,keyBO);
					return Boolean.TRUE;
				}
			}
			
			
		}
		
		
		return Boolean.FALSE;
	}
	
	/**
	 * 复制目录
	 * @description
	 * @author miaomingming
	 * @date 14:11 2020/6/23
	 * @param sourceClient
	 * @param targetClient
	 * @param srcBo
	 * @param srcKey
	 * @param targetBo
	 * @return void
	 **/
	private void copyDir(S3Client sourceClient,S3Client targetClient,S3KeyBO srcBo,String srcKey, S3KeyBO targetBo){
		log.info(logConfig.getLogTemplate(),"copyDir-param",srcBo.getKey(),targetBo.getKey());
		MyPage myPage = new MyPage(1,BizConstants.MAX_PAGE_SIE);
		boolean hasNext = Boolean.TRUE;
		
		while (hasNext){
			List<S3KeyBO> list =  s3KeyService.list(sourceClient,srcBo,myPage);
			
			if(!CollectionUtils.isEmpty(list)){
				for(S3KeyBO bo:list){
					if(bo.getIsFile() == null ||  !bo.getIsFile()){
						copyDir(sourceClient,targetClient,bo,srcKey,targetBo);
					}else{
						copyFile(sourceClient,targetClient,bo,srcKey,targetBo);
					}
				}
				if(myPage.getPageCount() <= 1){
					hasNext = Boolean.FALSE;
				}
				
			}else{
				hasNext = Boolean.FALSE;
			}
			
		}
		
		
	}
	
	/**
	 * 复制文件
	 * @description
	 * @author miaomingming
	 * @date 11:43 2020/6/23
	 * @param sourceClient
	 * @param targetClient
	 * @param srcBo
	 * @param srcKey
	 * @param targetBo
	 * @return void
	 **/
	private void copyFile(S3Client sourceClient,S3Client targetClient,S3KeyBO srcBo,String srcKey, S3KeyBO targetBo){
		log.info(logConfig.getLogTemplate(),"copyFile-param",srcBo.getKey(),targetBo.getKey());
		//默认文件->目录
		String targetKey = targetBo.getKey();
		//目标为目录
		if(targetKey.endsWith(BizConstants.PATH_SEPARATOR)){
			//源为目录
			if(srcKey.endsWith(BizConstants.PATH_SEPARATOR)){
				targetKey += srcBo.getKey().replace(srcKey,"");
			}else {
				//源为文件
				targetKey += srcKey.substring(srcKey.lastIndexOf(BizConstants.PATH_SEPARATOR) + 1);
			}
		}
		S3KeyBO target = new S3KeyBO(targetBo.getBucket(),targetKey);
		
		//同一个s3服务器
		if(s3FactoryService.judgeBucket(srcBo.getBucket(),target.getBucket())){
			log.info(logConfig.getLogTemplate(),"copyFile-param",srcBo.getKey(),"same.bucket");
			s3KeyService.copy(sourceClient,srcBo,target);
		}else{
			log.info(logConfig.getLogTemplate(),"copyFile-param",srcBo.getKey(),"not.same.bucket");
			SysKeyUploadRequest sysKeyRequest = BeanCopyUtil.copy(target,SysKeyUploadRequest.class);
			byte[] bodys = this.getBody(sysKeyRequest,null);
			
			this.upload(sysKeyRequest,bodys);
		}
		//复制tagList
		List<TagBO> tagBOList = s3KeyService.getTagList(sourceClient,srcBo);
		s3KeyService.setTagList(targetClient,target,tagBOList);
	}
	
	
	
	/**
	 * 生成版本号
	 * @description
	 * @author miaomingming
	 * @date 11:44 2020/6/17
	 * @param keyBO
	 * @return java.lang.String
	 **/
	private String createVersionNo(S3KeyBO keyBO){
		int verNoInt = 1;
		S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
		//查询信息
		List<TagBO> tagBOList = s3KeyService.getTagList(s3,keyBO);
		String verNo = S3Utils.getTagValue(tagBOList, S3TagKeyEnum.VERSION_NUMBER.getCode());
		
		try{
			if(StringUtils.isNotEmpty(verNo)){
				verNoInt =  Integer.parseInt(verNo) + 1;
			}
		}catch (NumberFormatException e){
			log.info(logConfig.getLogTemplate(),"createVersionNo",keyBO.getKey(),"NumberFormatException");
		}
		
		return String.valueOf(verNoInt);
	}
}
