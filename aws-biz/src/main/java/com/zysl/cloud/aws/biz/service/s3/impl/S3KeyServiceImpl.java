package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.biz.utils.DataAuthUtils;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Slf4j
@Service("s3KeyService")
public class S3KeyServiceImpl implements IS3KeyService<S3KeyBO> {
	
	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private BizConfig bizConfig;
	@Autowired
	private DataAuthUtils dataAuthUtils;
	@Autowired
	private IS3BucketService s3BucketService;
	
	@Override
	public S3KeyBO create(S3KeyBO t) {
		log.info("ESLOG create-param {} ", t);
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucket(),Boolean.TRUE);
		
		PutObjectRequest.Builder request = PutObjectRequest.builder()
												.bucket(t.getBucket())
												.key(t.getKey())
												.contentEncoding(t.getContentEncoding())
												.expires(t.getExpires() == null ? null : t.getExpires().toInstant());
		//获取目标文件标签内容
		Tagging tagging = S3Utils.creatTagging(t.getTagList());
		if(null != tagging){
			request.tagging(tagging);
		}
		
		s3FactoryService.callS3MethodWithBody(request.build(), RequestBody.fromBytes(t.getBodys()),s3Client, S3Method.PUT_OBJECT);
		log.info("ESLOG create-success {}", t);
		
		return t;
	}
	
	@Override
	public void delete(S3KeyBO t) {
		log.info("ESLOG delete-param {}", t);
		
//		//获取s3初始化对象
//		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
//
//		DeleteObjectsRequest deleteObjectsRequest = null;
//		Delete delete = null;
//		//逻辑删除或者指定版本号
//		if(StringUtils.isNotBlank(t.getVersionId()) || DeleteStoreEnum.NOCOVER.getCode().equals(t.getDeleteStore())){
//			ObjectIdentifier.Builder objectIdentifier = ObjectIdentifier.builder()
//				.key(StringUtils.join(t.getPath() ,t.getFileName()));
//			if(StringUtils.isNotBlank(t.getVersionId())){
//				objectIdentifier.versionId(t.getVersionId());
//			}
//			;
//			List<ObjectIdentifier> objects = new ArrayList<>();
//			objects.add(objectIdentifier.build());
//			delete = Delete.builder().objects(objects).build();
//
//		}else if(DeleteStoreEnum.COVER.getCode().equals(t.getDeleteStore())){
//			//删除整个文件信息, 先查询文件的版本信息
//			List<S3ObjectBO> objectList = getVersions(t);
//
//			List<ObjectIdentifier> objects = Lists.newArrayList();
//			//查询文件的版本信息
//			if(!CollectionUtils.isEmpty(objectList)){
//				objectList.forEach(obj -> {
//					ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
//						.key(obj.getFileName())
//						.versionId(obj.getVersionId()).build();
//					objects.add(objectIdentifier);
//				});
//
//				//删除列表
//				delete = Delete.builder().objects(objects).build();
//			}
//
//		}
//		//逻辑删除
//		if(delete != null){
//			deleteObjectsRequest = DeleteObjectsRequest.builder()
//				.bucket(t.getBucketName())
//				.delete(delete)
//				.build();
//			//文件删除
//			DeleteObjectsResponse response = s3FactoryService.callS3Method(deleteObjectsRequest, s3, S3Method.DELETE_OBJECTS);
//			log.debug("--delete文件删除返回；{}--", response);
//		}
		
		log.info("ESLOG delete-success {}", t);
	}
	
	@Override
	public void rename(S3KeyBO src, S3KeyBO dest) {
	
	}
	
	@Override
	public S3KeyBO copy(S3KeyBO src, S3KeyBO dest) {
		return null;
	}
	
	@Override
	public void move(S3KeyBO src, S3KeyBO dest) {
	
	}
	
	@Override
	public void modify(S3KeyBO t) {
	
	}
	
	@Override
	public S3KeyBO getBaseInfo(S3KeyBO t) {
		log.info("ESLOG getBaseInfo.param {}", t);
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucket());
		
		HeadObjectRequest.Builder request = HeadObjectRequest.builder()
												.bucket(t.getBucket())
												.key(t.getKey())
												.versionId(t.getVersionId());
		
		HeadObjectResponse response = s3FactoryService.callS3Method(request.build(), s3Client, S3Method.HEAD_OBJECT, Boolean.FALSE);
		log.info("ESLOG getBaseInfo.resp {}", response);
		
		if(response != null){
			t.setVersionId(response.versionId());
			t.setContentLength(response.contentLength());
			t.setExpires(DateUtils.from(response.expires()));
			t.setLastModified(DateUtils.from(response.lastModified()));
			t.setContentEncoding(response.contentEncoding());
			t.setContentType(response.contentType());
			log.info("ESLOG getBaseInfo.rst {}", t);
			return t;
		}
		
		return null;
	}
	
	@Override
	public S3KeyBO getDetailInfo(S3KeyBO t) {
		return null;
	}
	
	@Override
	public List<TagBO> getTagList(S3KeyBO t){
		log.info("ESLOG getTagList.param {}", t);
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucket());
		//查询文件的标签信息
		GetObjectTaggingRequest.Builder request = GetObjectTaggingRequest.builder()
													.bucket(t.getBucket())
													.key(t.getKey())
													.versionId(t.getVersionId());
		GetObjectTaggingResponse response = s3FactoryService.callS3Method(request.build(), s3, S3Method.GET_OBJECT_TAGGING, false);
		log.info("ESLOG getTagList.resp {}", response);
		
		
		List<TagBO> tagList = Lists.newArrayList();
		if(null != response){
			List<Tag> list = response.tagSet();
			if(!CollectionUtils.isEmpty(list)){
				list.forEach(obj -> {
					TagBO tag = new TagBO();
					tag.setKey(obj.key());
					tag.setValue(obj.value());
					tagList.add(tag);
				});
			}
		}
		log.info("ESLOG getTagList.rst({}) {}", t.getKey(),JSON.toJSONString(tagList));
		return tagList;
	}
	
	@Override
	public S3KeyBO getInfoAndBody(S3KeyBO t) {
		return null;
	}
	
	@Override
	public List<S3KeyBO> getVersions(S3KeyBO t) {
		return null;
	}
	
	@Override
	public S3KeyBO rename(S3KeyBO t) {
		return null;
	}
}
