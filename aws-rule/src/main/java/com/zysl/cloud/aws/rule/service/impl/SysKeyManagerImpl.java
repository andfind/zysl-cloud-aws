package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysKeyRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.rule.service.utils.ObjectFormatUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.MyPage;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Service
public class SysKeyManagerImpl implements ISysKeyManager {
	
	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private IS3KeyService s3KeyService;
	@Autowired
	private WebConfig webConfig;
	
	@Override
	public void create(SysKeyRequest request, byte[] bodys, Boolean isOverWrite){
		PathUriBO pathUriBO = ObjectFormatUtils.formatS3PathURI(request.getPath());
		if(FileSysTypeEnum.S3.getCode().equals(pathUriBO.getScheme())){
			S3KeyBO keyBO = BeanCopyUtil.copy(pathUriBO,S3KeyBO.class);
			keyBO.setBodys(bodys);
			//设置版本号
			List<TagBO> tagList = new ArrayList<>();
			tagList.add(new TagBO(BizConstants.S3_TAG_KEY_VERSION_NO,createVersionNo(keyBO)));
			keyBO.setTagList(tagList);
			
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			s3KeyService.create(s3,keyBO);
		}
	}
	
	@Override
	public SysKeyDTO info(SysKeyRequest request){
		return null;
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
	public void delete(SysKeyRequest request,Boolean isPhy){
		PathUriBO pathUriBO = ObjectFormatUtils.formatS3PathURI(request.getPath());
		if(FileSysTypeEnum.S3.getCode().equals(pathUriBO.getScheme())){
			S3KeyBO keyBO = BeanCopyUtil.copy(pathUriBO,S3KeyBO.class);
			S3Client s3 = s3FactoryService.getS3ClientByBucket(keyBO.getBucket());
			//物理删除，需要删除所有版本
			if(isPhy != null && isPhy){
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
		String verNo = S3Utils.getTagValue(tagBOList, BizConstants.S3_TAG_KEY_VERSION_NO);
		
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
