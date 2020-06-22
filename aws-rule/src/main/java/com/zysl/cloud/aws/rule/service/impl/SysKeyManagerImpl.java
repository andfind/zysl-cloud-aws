package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.MyPage;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	
	@Override
	public void create(SysKeyCreateRequest request){
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
				log.error("ES_LOG_EXCEPTION {} base64.to.bytes.ioException", request.getPath());
			}
			
			//没有上传base64 或者 上传后转化成功
			if(StringUtils.isEmpty(request.getData()) || bodys != null){
				upload(sysKeyUploadRequest,bodys);
			}
		}
	}
	
	@Override
	public void upload(SysKeyUploadRequest request,byte[] bodys){
		if(FileSysTypeEnum.S3.getCode().equals(request.getScheme())){
			S3KeyBO keyBO = BeanCopyUtil.copy(request,S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			//不覆盖
			if(request.getIsCover() == null || !request.getIsCover()){
				//判断是否存在，存在则返回
				if(s3KeyService.getBaseInfo(s3,keyBO) != null){
					throw new AppLogicException(ErrCodeEnum.S3_BUCKET_OBJECT_NOT_EXIST.getCode());
				}
			}
			keyBO.setBodys(bodys);
			//设置版本号
			List<TagBO> tagList = new ArrayList<>();
			tagList.add(new TagBO(S3TagKeyEnum.VERSION_NUMBER.getCode(),createVersionNo(keyBO)));
			tagList.add(new TagBO(S3TagKeyEnum.FILE_NAME.getCode(),request.getFileName()));
			keyBO.setTagList(tagList);
			s3KeyService.create(s3,keyBO);
		}
	}
	
	@Override
	public SysKeyDTO info(SysKeyRequest request){
		SysKeyDTO dto = null;
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
		  	S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			
			S3KeyBO s3KeyBO = (S3KeyBO)s3KeyService.getBaseInfo(s3,keyBO);
			if(s3KeyBO == null){
				return null;
			}
			dto = BeanCopyUtil.copy(s3KeyBO, SysKeyDTO.class);
			dto.setSize(s3KeyBO.getContentLength());
			dto.setPath(request.getPath());
			//版本号
			List<TagBO> tagBOList = s3KeyService.getTagList(s3,keyBO);
			String verNo = S3Utils.getTagValue(tagBOList, S3TagKeyEnum.VERSION_NUMBER.getCode());
			if(StringUtils.isNotEmpty(verNo)){
				dto.setVersionNo(Integer.parseInt(verNo));
			}
		}
		
		return dto;
	}
	
	@Override
	public List<SysKeyDTO> keyList(SysKeyRequest request){
		return  null;
	}
	
	@Override
	public 	List<SysKeyDTO> versionList(SysKeyRequest request){
		return null;
	}
	
	@Override
	public void delete(SysKeyDeleteRequest request){
		if(FileSysTypeEnum.S3.getCode().equals(request.getScheme())){
			S3KeyBO keyBO = BeanCopyUtil.copy(request,S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			//物理删除，需要删除所有版本
			if(request.getIsPhy() != null && request.getIsPhy()){
				//版本列表查询-->删除所有版本
				List<S3KeyBO> s3KeyBOList = s3KeyService.getVersions(s3,keyBO);
				//删除当前对象
				s3KeyService.deleteList(s3,keyBO.getBucket(),s3KeyBOList);
			}else{
				MyPage myPage = new MyPage(1,999999);
				List<S3KeyBO> s3KeyBOList = s3KeyService.list(s3,keyBO,myPage);
				s3KeyService.deleteList(s3,keyBO.getBucket(),s3KeyBOList);
			}
		}
	}
	
	@Override
	public byte[] getBody(SysKeyRequest request,String range){
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
		    S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
		    keyBO.setBucket(request.getHost());
		    keyBO.setRange(range);
	
     	    S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			if(s3KeyService.getInfoAndBody(s3,keyBO) != null){
				Date date1 = keyBO.getLastModified();
				Date date2 = DateUtils.createDate(bizConfig.DOWNLOAD_TIME);
				log.info("ES_LOG {} bytes.length:{},date1:{},date2:{}", keyBO.getKey(),keyBO.getBodys().length,date1,date2);
				
				//有一批数据写入时是直接base64的所以要解码
				if(DateUtils.doCompareDate(date1, date2) < 0 && keyBO.getBodys() != null){
					//进行解码
					try {
						BASE64Decoder decoder = new BASE64Decoder();
						return decoder.decodeBuffer(new String(keyBO.getBodys()));
					} catch (IOException e) {
						log.error("ES_LOG IOException msg:{}",keyBO.getKey(), ExceptionUtil.getMessage(e));
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
		if (FileSysTypeEnum.S3.getCode().equals(request.getScheme())) {
			S3KeyBO keyBO = BeanCopyUtil.copy(request, S3KeyBO.class);
			keyBO.setBucket(request.getHost());
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			return s3KeyService.getTagList(s3,keyBO);
		}
		
		return null;
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
			log.warn("ES_LOG {} {}",keyBO, "createVersionNo:"+ ExceptionUtil.getMessage(e));
		}
		
		return String.valueOf(verNoInt);
	}
}
