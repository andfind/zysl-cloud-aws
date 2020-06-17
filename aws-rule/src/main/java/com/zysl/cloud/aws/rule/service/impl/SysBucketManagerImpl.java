package com.zysl.cloud.aws.rule.service.impl;

import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.rule.service.ISysBucketManager;
import com.zysl.cloud.utils.StringUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;

@Slf4j
@Service
public class SysBucketManagerImpl implements ISysBucketManager {
	
	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private IS3KeyService s3KeyService;
	@Autowired
	private IS3BucketService s3BucketService;
	
	
	@Override
	public void delete(String bucketName) {
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(bucketName);
		//清空文件
		S3KeyBO s3KeyBO = new S3KeyBO(bucketName);
		//版本列表查询
		List<S3KeyBO> s3KeyBOList = s3KeyService.getVersions(s3,s3KeyBO);
		//删除所有版本
		s3KeyService.deleteList(s3,bucketName,s3KeyBOList);
		
		//删除bucket
		s3BucketService.delete(s3,bucketName);
		
		//更新缓存
		String serverNo = s3FactoryService.getServerNo(bucketName);
		if(StringUtils.isNotEmpty(serverNo)){
			s3FactoryService.updateBucket(serverNo);
		}
		
	}
}
