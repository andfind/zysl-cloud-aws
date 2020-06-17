package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysKeyRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
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
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SysKeyManagerImpl implements ISysKeyManager {
	
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
			
			s3KeyService.create(keyBO);
		}
	}
	
	@Override
	public SysKeyDTO info(SysKeyRequest request){
		return null;
	}
	
	
	private String createVersionNo(S3KeyBO keyBO){
		int verNoInt = 1;
		//查询信息
		List<TagBO> tagBOList = s3KeyService.getTagList(keyBO);
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
