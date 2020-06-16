package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysKeyRequest;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.utils.BizUtil;
import java.net.URI;
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
		URI uri = BizUtil.formatPathURI(request.getPath());
		if(FileSysTypeEnum.S3.getCode().equals(uri.getScheme())){
			S3KeyBO keyBO = createS3KeyBO(uri);
			keyBO.setBodys(bodys);
			//查询信息
			s3KeyService.getDetailInfo(keyBO);
			//设置版本号
			
			s3KeyService.create(keyBO);
		}
	}
	
	@Override
	public SysKeyDTO info(SysKeyRequest request){
		return null;
	}
	
	private S3KeyBO createS3KeyBO(URI uri){
		S3KeyBO keyBO = new S3KeyBO();
		keyBO.setVersionId(uri.getFragment());
		String path = uri.getPath();
		//去掉首个/
		path = path.substring(1);
		keyBO.setBucket(path.substring(0,path.indexOf("/")));
		keyBO.setKey(path.substring(keyBO.getBucket().length()+1));
		
		return  keyBO;
	}
}
