package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
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
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.MyPage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
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
	public S3KeyBO create(S3Client s3Client,S3KeyBO t) {
		log.info("ES_LOG create-param {} ", t);
		
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
		RequestBody requestBody = null;
		if(t.getBodys() == null){
			requestBody = RequestBody.empty();
		}else{
			requestBody = RequestBody.fromBytes(t.getBodys());
		}
		
		s3FactoryService.callS3MethodWithBody(request.build(), requestBody,s3Client, S3Method.PUT_OBJECT);
		log.info("ES_LOG create-success {}", t);
		
		return t;
	}
	
	@Override
	public void delete(S3Client s3Client,S3KeyBO t) {
		log.info("ES_LOG delete-param {}", t);
		if(StringUtils.isNotEmpty(t.getKey())){
			DeleteObjectRequest request = DeleteObjectRequest.builder()
				.bucket(t.getBucket())
				.key(t.getKey())
				.versionId(t.getVersionId())
				.build();
			
			s3FactoryService.callS3Method(request, s3Client, S3Method.DELETE_OBJECT);
		}
		
		log.info("ES_LOG delete-success {}", t);
	}
	
	@Override
	public void deleteList(S3Client s3Client, String bucket, List<S3KeyBO> s3KeyBOs) {
		log.info("ES_LOG deleteList-param.size {}", CollectionUtils.isEmpty(s3KeyBOs) ? 0 : s3KeyBOs.size());
		if(!CollectionUtils.isEmpty(s3KeyBOs)){
			int max=500;
			List<ObjectIdentifier> objects = Lists.newArrayList();
			
			//查询结果一般是返回时是目录在前，所以删除要倒序
			for(int i=s3KeyBOs.size()-1;i>=0;i--){
				S3KeyBO bo = s3KeyBOs.get(i);
				ObjectIdentifier objectIdentifier = ObjectIdentifier.builder().key(bo.getKey()).versionId(bo.getVersionId()).build();
				objects.add(objectIdentifier);
				
				if(i==0 || i%max == 0){
					Delete delete = Delete.builder().objects(objects).build();
					DeleteObjectsRequest request = DeleteObjectsRequest.builder().bucket(bucket).delete(delete).build();
					s3FactoryService.callS3Method(request, s3Client, S3Method.DELETE_OBJECTS);
					objects = Lists.newArrayList();
				}
			}
		}
		log.info("ES_LOG deleteList-success.size {}", CollectionUtils.isEmpty(s3KeyBOs) ? 0 : s3KeyBOs.size());
	}
	
	@Override
	public S3KeyBO copy(S3Client s3Client, S3KeyBO src, S3KeyBO dest) {
		return null;
	}
	
	
	@Override
	public S3KeyBO getBaseInfo(S3Client s3Client,S3KeyBO t) {
		log.info("ES_LOG getBaseInfo.param {}", t);
		
		HeadObjectRequest.Builder request = HeadObjectRequest.builder()
												.bucket(t.getBucket())
												.key(t.getKey())
												.versionId(t.getVersionId());
		
		HeadObjectResponse response = s3FactoryService.callS3Method(request.build(), s3Client, S3Method.HEAD_OBJECT, Boolean.FALSE);
		log.info("ES_LOG getBaseInfo.resp {}", response);
		
		if(response != null){
			t.setVersionId(response.versionId());
			t.setContentLength(response.contentLength());
			t.setExpires(DateUtils.from(response.expires()));
			t.setLastModified(DateUtils.from(response.lastModified()));
			t.setContentEncoding(response.contentEncoding());
			t.setContentType(response.contentType());
			log.info("ES_LOG getBaseInfo.rst {}", t);
			return t;
		}
		
		return null;
	}
	
	@Override
	public S3KeyBO getInfoAndBody(S3Client s3Client, S3KeyBO s3KeyBO) {
		return null;
	}
	
	
	@Override
	public List<TagBO> getTagList(S3Client s3Client,S3KeyBO t){
		log.info("ES_LOG getTagList.param {}", t);
		//查询文件的标签信息
		GetObjectTaggingRequest.Builder request = GetObjectTaggingRequest.builder()
													.bucket(t.getBucket())
													.key(t.getKey())
													.versionId(t.getVersionId());
		GetObjectTaggingResponse response = s3FactoryService.callS3Method(request.build(), s3Client, S3Method.GET_OBJECT_TAGGING, false);
		log.info("ES_LOG getTagList.resp {}", response);
		
		
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
		log.info("ES_LOG getTagList.rst({}) {}", t.getKey(),JSON.toJSONString(tagList));
		return tagList;
	}
	
	
	@Override
	public List<S3KeyBO> getVersions(S3Client s3Client,S3KeyBO t) {
		log.info("ES_log getVersions-param {}}",t);
		
		ListObjectVersionsRequest.Builder request = ListObjectVersionsRequest.builder().
												bucket(t.getBucket()).
												prefix(t.getKey());
		
		ListObjectVersionsResponse response = null;
		String nextMarker = null;
		List<S3KeyBO> versionList = Lists.newArrayList();
		while (response == null || response.isTruncated()){
			request.keyMarker(nextMarker);
			response = s3FactoryService.callS3Method(request.build(),s3Client, S3Method.LIST_OBJECT_VERSIONS);
			nextMarker = response.nextKeyMarker();
			//未删除文件
			if(!CollectionUtils.isEmpty(response.versions())){
				response.versions().forEach(obj -> {
					S3KeyBO s3KeyBO = new S3KeyBO(obj.key(),obj.versionId(),obj.size());
					s3KeyBO.setLastModified(Date.from(obj.lastModified()));
					s3KeyBO.setIsDeleted(Boolean.FALSE);
					versionList.add(s3KeyBO);
				});
			}
			//已删除文件
			if(!CollectionUtils.isEmpty(response.deleteMarkers())){
				response.deleteMarkers().forEach(obj -> {
					S3KeyBO s3KeyBO = new S3KeyBO(obj.key(),obj.versionId(),0L);
					s3KeyBO.setLastModified(Date.from(obj.lastModified()));
					s3KeyBO.setIsDeleted(Boolean.TRUE);
					versionList.add(s3KeyBO);
				});
			}
		}
		
		log.info("ES_log getVersions-rst.size {}}",versionList.size());
		return versionList;
	}
	
	@Override
	public List<S3KeyBO> list(S3Client s3Client, S3KeyBO s3KeyBO, MyPage myPage) {
		log.info("ES_log keyList-param {}}",s3KeyBO);
		//查询结果
		List<S3KeyBO> list = new ArrayList<>();
		//获取查询对象列表入参
		int totalRecords = 0,rspCount = 1;
		String nextMarker = null;
		ListObjectsResponse response = null;
		ListObjectsRequest.Builder request = ListObjectsRequest.builder().bucket(s3KeyBO.getBucket())
													.prefix(s3KeyBO.getKey())
													.delimiter(BizConstants.PATH_SEPARATOR);
		//查询目录下的对象信息
		while (response == null || response.isTruncated()){
			request.marker(nextMarker);
			response = s3FactoryService.callS3Method(request.build(),s3Client, S3Method.LIST_OBJECTS);
			nextMarker = response.nextMarker();
			if(!CollectionUtils.isEmpty(response.contents())){
				totalRecords += response.contents().size();
			}
			if(!CollectionUtils.isEmpty(response.commonPrefixes())){
				totalRecords += response.commonPrefixes().size();
			}
			//根据当前记录数及传入的页码+每页数据读取读取
			setS3KeyBOList(s3KeyBO.getBucket(),list,response,myPage,rspCount++);
		}
		myPage.setTotalRecords(totalRecords);
		
		return list;
	}
	
	/**
	 * 根据查询结果设置翻页数据
	 * @description
	 * @author miaomingming
	 * @date 16:08 2020/6/17
	 * @param list
	 * @param response
	 * @param myPage
	 * @param rspCount
	 * @return void
	 **/
	private void setS3KeyBOList(String bucket,List<S3KeyBO> list,ListObjectsResponse response,MyPage myPage,int rspCount){
		int myPageStart = (myPage.getPageNo()-1) * myPage.getPageSize();
		int myPageEnd =  myPage.getPageNo() * myPage.getPageSize()-1;
		int rspStartIndex = (rspCount - 1) * 1000;
		
		if(!CollectionUtils.isEmpty(response.commonPrefixes())){
			for(int i=0;i<response.commonPrefixes().size();i++,rspStartIndex++){
				if(rspStartIndex >= myPageStart && rspStartIndex <= myPageEnd){
					list.add(new S3KeyBO(bucket,response.commonPrefixes().get(i).prefix()));
				}
			}
		}
		
		if(!CollectionUtils.isEmpty(response.contents())){
			for(int i=0;i<response.contents().size();i++,rspStartIndex++){
				if(rspStartIndex >= myPageStart && rspStartIndex <= myPageEnd){
					S3Object s3Object = response.contents().get(i);
					S3KeyBO s3KeyBO = new S3KeyBO(bucket,s3Object.key());
					s3KeyBO.setLastModified(DateUtils.from(s3Object.lastModified()));
					s3KeyBO.setContentLength(s3Object.size());
					s3KeyBO.setETag(s3Object.eTag());
					s3KeyBO.setIsFile(Boolean.TRUE);
					
					list.add(s3KeyBO);
				}
			}
		}
		
		
	}
	
}
