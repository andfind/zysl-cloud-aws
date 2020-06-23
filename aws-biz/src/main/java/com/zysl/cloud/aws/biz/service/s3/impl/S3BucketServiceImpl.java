package com.zysl.cloud.aws.biz.service.s3.impl;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.enums.BucketVerStatusEnum;
import com.zysl.cloud.aws.api.req.BucketFileRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.MyPage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@Slf4j
public class S3BucketServiceImpl implements IS3BucketService {

	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private IS3FileService fileService;
	@Autowired
	private IS3KeyService s3KeyService;

	@Override
	public List<Bucket> getBucketList(S3Client s3){
		log.info("=getBucketList=");
		ListBucketsRequest request = ListBucketsRequest.builder().build();
		ListBucketsResponse response = s3FactoryService.callS3Method(request,s3,S3Method.LIST_BUCKETS);

		return response.buckets();
	}

	@Override
	public List<String> getS3Buckets(String serviceNo){
		log.info("=getS3Buckets={}",serviceNo);
		List<String> list = new ArrayList<>();
		Map<String, String> map = s3FactoryService.getBucketServerNoMap();
		if(map != null && !map.isEmpty()){
			for(String key:map.keySet()){
				if (StringUtils.isBlank(serviceNo) || map.get(key).equals(serviceNo)) {
				  list.add(key);
				}
			}
		}

		log.info("---buskets:{}", list.size());
		return list;
	}

	@Override
	public Boolean createBucket(String bucketName, String serviceNo) {
		log.info("---createBucket:---bucketName:{},serviceName:{}",bucketName, serviceNo);
		S3Client s3 = s3FactoryService.getS3ClientByServerNo(serviceNo);

		//判断bucket是否存在
		if(s3FactoryService.isExistBucket(bucketName)){
			log.info("---createBucket.is.exist--bucketName:{},serviceName:{}",bucketName, serviceNo);
			throw new AppLogicException(ErrCodeEnum.S3_CREATE_BUCKET_EXIST.getCode());
		}

		CreateBucketRequest s3r = CreateBucketRequest.builder().bucket(bucketName).build();
		CreateBucketResponse response = s3FactoryService.callS3Method(s3r,s3,S3Method.CREATE_BUCKETS);

		log.info("--createBucket.success--fileName:{}", response.location());
		s3FactoryService.addBucket(bucketName,serviceNo);

		//启用版本控制
		SetFileVersionRequest request = new SetFileVersionRequest();
		request.setBucketName(bucketName);
		request.setStatus(BucketVerStatusEnum.ENABLED.getCode());
		setBucketVersion(request);

		return Boolean.TRUE;
	}


	@Override
	public Boolean setBucketVersion(SetFileVersionRequest request) {
		log.info("setBucketVersion-param:",JSON.toJSONString(request));
		S3Client s3 = s3FactoryService.getS3ClientByBucket(request.getBucketName());
		//启动文件夹的版本控制,,//BucketVersioningStatus.ENABLED
		PutBucketVersioningRequest s3r = PutBucketVersioningRequest.builder()
												.bucket(request.getBucketName())
												.versioningConfiguration(VersioningConfiguration.builder()
												.status(request.getStatus())
												.build())
												.build();
		s3FactoryService.callS3Method(s3r,s3,S3Method.PUT_BUCKET_VERSIONING);
		log.info("修改版本控制成功:{}", request.getBucketName());

		return Boolean.TRUE;

	}

	@Override
	public Boolean putBucketTag(S3ObjectBO t) {
 		log.info("s3bucket.putBucketTag.param:{}", t);
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//先查询原来bucket的标签信息
		List<TagBO> oldTagList = this.getBucketTag(t.getBucketName());

        //新设置标签入参
        List<TagBO> newTageList = t.getTagList();

        List<TagBO> tagBOList = fileService.mergeTags(oldTagList, newTageList);

        List<Tag> tagSet = new ArrayList<>();
        if(!CollectionUtils.isEmpty(tagBOList)) {
            tagBOList.forEach(obj -> {
                tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
            });
            Tagging tagging = Tagging.builder().tagSet(tagSet).build();

            PutBucketTaggingRequest request = PutBucketTaggingRequest.builder()
                    .bucket(t.getBucketName())
                    .tagging(tagging)
                    .build();
            //设置bucket标签
            PutBucketTaggingResponse response = s3FactoryService.callS3Method(request, s3, S3Method.PUT_BUCKET_TAGGING);
        }

        return Boolean.TRUE;
	}

	@Override
	public List<TagBO> getBucketTag(String bucketName) {
        log.info("s3bucket.getBucketTag.param:{}", bucketName);
        //获取s3初始化对象
        S3Client s3 = s3FactoryService.getS3ClientByBucket(bucketName);

        GetBucketTaggingRequest request = GetBucketTaggingRequest.builder().bucket(bucketName).build();
        GetBucketTaggingResponse response = s3FactoryService.callS3Method(request, s3, S3Method.GET_BUCKET_TAGGING,Boolean.FALSE);

        if(response != null && !CollectionUtils.isEmpty(response.tagSet())){
			List<TagBO> tagList = Lists.newArrayList();
			response.tagSet().forEach(tag -> {
				TagBO tagBO = new TagBO();
				tagBO.setKey(tag.key());
				tagBO.setValue(tag.value());
				tagList.add(tagBO);
			});
			return tagList;
		}
     return null;
	}
	
	@Override
	public void getBucketInfo(String bucketName){
		log.info("ES_LOG getBucketInfo-param {}",bucketName);
		//获取s3初始化对象
//		S3Client s3 = s3FactoryService.getS3ClientByBucket(bucketName);
//
//		HeadBucketRequest request = HeadBucketRequest.builder()
//										.bucket(bucketName)
//										.build();
//		HeadBucketResponse response = s3FactoryService.callS3Method(request, s3, S3Method.HEAD_BUCKET);
//		log.info("ES_LOG getBucketInfo-rsp {}",response);
		
		//遍历文件，统计数量及大小
		//已技术验证，查询只有元信息：	response.responseMetadata()
	}
	
	@Override
	public void delete(S3Client s3Client,String bucketName){
		log.info("ES_LOG delete-param {}",bucketName);
		//删除bucket
		DeleteBucketRequest request = DeleteBucketRequest.builder().bucket(bucketName).build();
		s3FactoryService.callS3Method(request, s3Client, S3Method.DELETE_BUCKET);
		
		log.info("ES_LOG delete-rsp {}",bucketName);
	}
	
}
