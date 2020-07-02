package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.biz.utils.DataAuthUtils;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.LogConfig;
import com.zysl.cloud.aws.domain.bo.FilePartInfoBO;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.MyPage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.MultipartUpload;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import sun.misc.BASE64Decoder;

@Slf4j
@Service("s3KeyService")
public class S3KeyServiceImpl implements IS3KeyService<S3KeyBO> {
	
	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private LogConfig logConfig;
	
	@Override
	public S3KeyBO create(S3Client s3Client,S3KeyBO t) {
		log.info(logConfig.getLogTemplate(),"createKey-param",t.getKey(),t);
		
		//获取目标文件标签内容
		Tagging tagging = S3Utils.creatTagging(t.getTagList());
		
		PutObjectRequest.Builder request = PutObjectRequest.builder()
												.bucket(t.getBucket())
												.key(t.getKey())
												.tagging(tagging)
												.contentEncoding(t.getContentEncoding())
												.expires(t.getExpires() == null ? null : t.getExpires().toInstant());
		
		RequestBody requestBody = null;
		if(t.getBodys() == null){
			requestBody = RequestBody.empty();
		}else{
			requestBody = RequestBody.fromBytes(t.getBodys());
		}
		
		s3FactoryService.callS3MethodWithBody(request.build(), requestBody,s3Client, S3Method.PUT_OBJECT);
		log.info(logConfig.getLogTemplate(),"createKey",t.getKey(),"success");
		
		return t;
	}
	
	@Override
	public void delete(S3Client s3Client,S3KeyBO t) {
		log.info(logConfig.getLogTemplate(),"deleteKey-param",t.getKey(),t);
		if(StringUtils.isNotEmpty(t.getKey())){
			DeleteObjectRequest request = DeleteObjectRequest.builder()
				.bucket(t.getBucket())
				.key(t.getKey())
				.versionId(t.getVersionId())
				.build();
			
			s3FactoryService.callS3Method(request, s3Client, S3Method.DELETE_OBJECT);
		}
		
		log.info(logConfig.getLogTemplate(),"deleteKey-param",t.getKey(),"success");
	}
	
	@Override
	public void deleteList(S3Client s3Client, String bucket, List<S3KeyBO> s3KeyBOs) {
		log.info(logConfig.getLogTemplate(),"deleteKeyList-param","param.size",CollectionUtils.isEmpty(s3KeyBOs) ? 0 : s3KeyBOs.size());
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
		log.info(logConfig.getLogTemplate(),"deleteKeyList-param","success.size",CollectionUtils.isEmpty(s3KeyBOs) ? 0 : s3KeyBOs.size());
	}
	
	@Override
	public void deleteAllKey(S3Client s3Client,String bucket){
		log.info(logConfig.getLogTemplate(),"deleteAllKey-param",bucket,"param");
		ListObjectVersionsRequest.Builder request = ListObjectVersionsRequest.builder().bucket(bucket);
		
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
			this.deleteList(s3Client,bucket,versionList);
		}
		log.info(logConfig.getLogTemplate(),"deleteAllKey",bucket,"success");
	}
	
	@Override
	public void copy(S3Client s3Client, S3KeyBO src, S3KeyBO dest) {
		log.info(logConfig.getLogTemplate(),"copyKey-param",src.getKey(),dest.getKey());
		String srcUrl = null;
		try{
			srcUrl = java.net.URLEncoder.encode(StringUtils.join(src.getBucket(),BizConstants.PATH_SEPARATOR,src.getKey()), "utf-8");
		}catch (UnsupportedEncodingException e){
			throw new AppLogicException(ErrCodeEnum.S3_COPY_SOURCE_ENCODE_ERROR.getCode());
		}
		//复制文件
		CopyObjectRequest.Builder request = CopyObjectRequest.builder()
			.copySource(srcUrl)
			.bucket(src.getBucket())
			.key(dest.getKey());
		
		s3FactoryService.callS3Method(request.build(), s3Client, S3Method.COPY_OBJECT, Boolean.FALSE);
		
		log.info(logConfig.getLogTemplate(),"copyKey-success",src.getKey(),dest.getKey());
	}
	
	
	@Override
	public S3KeyBO getBaseInfo(S3Client s3Client,S3KeyBO t) {
		log.info(logConfig.getLogTemplate(),"getBaseInfo-param",t.getKey(),t);
		
		HeadObjectRequest.Builder request = HeadObjectRequest.builder()
												.bucket(t.getBucket())
												.key(t.getKey())
												.versionId(t.getVersionId());
		
		HeadObjectResponse response = s3FactoryService.callS3Method(request.build(), s3Client, S3Method.HEAD_OBJECT, Boolean.FALSE);
		log.info(logConfig.getLogTemplate(),"getBaseInfo-response",t.getKey(),response);
		
		if(response != null){
			t.setVersionId(response.versionId());
			t.setContentLength(response.contentLength());
			t.setExpires(DateUtils.from(response.expires()));
			t.setLastModified(DateUtils.from(response.lastModified()));
			t.setContentEncoding(response.contentEncoding());
			t.setContentType(response.contentType());
			
			log.info(logConfig.getLogTemplate(),"getBaseInfo-rst",t.getKey(),t);
			return t;
		}
		
		return null;
	}
	
	@Override
	public S3KeyBO getInfoAndBody(S3Client s3Client, S3KeyBO s3KeyBO) {
		log.info(logConfig.getLogTemplate(),"getInfoAndBody-param",s3KeyBO.getKey(),s3KeyBO);
		//获取下载对象
		GetObjectRequest.Builder request = GetObjectRequest.builder()
												.bucket(s3KeyBO.getBucket())
												.key(s3KeyBO.getKey())
												.versionId(s3KeyBO.getVersionId())
												.range(s3KeyBO.getRange());
		
		try{
			ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObject(request.build(), ResponseTransformer.toBytes());
			if(responseBytes == null || responseBytes.response() == null){
				return null;
			}
			GetObjectResponse response = responseBytes.response();
			s3KeyBO.setContentLength(response.contentLength());
			s3KeyBO.setETag(response.eTag());
			s3KeyBO.setLastModified(DateUtils.from(response.lastModified()));
			s3KeyBO.setBodys(responseBytes.asByteArray());
			s3KeyBO.setExpires(DateUtils.from(response.expires()));
			
			return  s3KeyBO;
		}catch (NoSuchKeyException e){
			log.warn(logConfig.getLogTemplate(),"getInfoAndBody-NoSuchKeyException",s3KeyBO.getKey(),"NoSuchKeyException");
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
		}catch (AwsServiceException | SdkClientException e){
			log.error(logConfig.getLogTemplate(),"getInfoAndBody-SdkClientException",s3KeyBO.getKey(),ExceptionUtil.getMessage(e),e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_AWS_SERVICE_EXCEPTION.getCode());
		}catch (Exception e){
			log.error(logConfig.getLogTemplate(),"getInfoAndBody-Exception",s3KeyBO.getKey(),ExceptionUtil.getMessage(e),e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_ERROR.getCode());
		}
	}
	
	
	@Override
	public List<TagBO> getTagList(S3Client s3Client,S3KeyBO t){
		log.info(logConfig.getLogTemplate(),"getTagList-param",t.getKey(),t);
		//查询文件的标签信息
		GetObjectTaggingRequest.Builder request = GetObjectTaggingRequest.builder()
													.bucket(t.getBucket())
													.key(t.getKey())
													.versionId(t.getVersionId());
		GetObjectTaggingResponse response = s3FactoryService.callS3Method(request.build(), s3Client, S3Method.GET_OBJECT_TAGGING, false);
		log.info(logConfig.getLogTemplate(),"getTagList-response",t.getKey(),response);
		
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
		log.info(logConfig.getLogTemplate(),"getTagList-rst",t.getKey(),JSON.toJSONString(tagList));
		return tagList;
	}
	
	@Override
	public List<S3KeyBO> getVersions(S3Client s3Client,S3KeyBO t, MyPage myPage) {
		log.info(logConfig.getLogTemplate(),"getVersions-param",t.getKey(),t);
		int myPageStart = (myPage.getPageNo()-1) * myPage.getPageSize();
		int myPageEnd =  myPage.getPageNo() * myPage.getPageSize()-1;
		int curIndex = 0;
		
		ListObjectVersionsRequest.Builder request = ListObjectVersionsRequest.builder().
			bucket(t.getBucket()).
			prefix(t.getKey());
		
		ListObjectVersionsResponse response = null;
		int totalRecords = 0;
		String nextMarker = null;
		List<S3KeyBO> versionList = Lists.newArrayList();
		while (response == null || response.isTruncated()){
			curIndex = totalRecords;
			request.keyMarker(nextMarker);
			response = s3FactoryService.callS3Method(request.build(),s3Client, S3Method.LIST_OBJECT_VERSIONS);
			nextMarker = response.nextKeyMarker();
			//未删除文件
			if(!CollectionUtils.isEmpty(response.versions())){
				totalRecords += response.versions().size();
				
				for(ObjectVersion obj:response.versions()){
					if(curIndex >= myPageStart && curIndex <= myPageEnd){
						S3KeyBO s3KeyBO = new S3KeyBO(obj.key(),obj.versionId(),obj.size());
						s3KeyBO.setLastModified(Date.from(obj.lastModified()));
						s3KeyBO.setIsDeleted(Boolean.FALSE);
						versionList.add(s3KeyBO);
					}
					curIndex++;
				}
				
				
			}
		}
		
		myPage.setTotalRecords(totalRecords);
		log.info(logConfig.getLogTemplate(),"getVersions-rst",t.getKey(),StringUtils.join("versionList.size:",versionList.size()));
		return versionList;
	}
	
	@Override
	public List<S3KeyBO> list(S3Client s3Client, S3KeyBO s3KeyBO, MyPage myPage) {
		log.info(logConfig.getLogTemplate(),"list-param",s3KeyBO.getKey(),s3KeyBO);
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
	
	@Override
	public void setTagList(S3Client s3Client,S3KeyBO t,List<TagBO> tagBOList){
	    log.info(logConfig.getLogTemplate(), "setTagList-param", t.getKey(), JSON.toJSON(tagBOList));
		//查询文件的标签信息
		PutObjectTaggingRequest.Builder request = PutObjectTaggingRequest.builder()
													.bucket(t.getBucket())
													.key(t.getKey())
													.tagging(S3Utils.creatTagging(tagBOList))
													.versionId(t.getVersionId());
		PutObjectTaggingResponse response = s3FactoryService.callS3Method(request.build(), s3Client, S3Method.PUT_OBJECT_TAGGING, false);
		log.info(logConfig.getLogTemplate(), "setTagList-response", t.getKey(), response);
		
	}
	
	@Override
	public String createMultipartUpload(S3Client s3, S3KeyBO s3KeyBO) {
		log.info(logConfig.getLogTemplate(), "createMultipartUpload-param", s3KeyBO.getKey(), s3KeyBO);
		//获取目标文件标签内容
		Tagging tagging = S3Utils.creatTagging(s3KeyBO.getTagList());
		//获取入参
		CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
												.bucket(s3KeyBO.getBucket())
												.key(s3KeyBO.getKey())
												.tagging(tagging)
												.build();
		
		CreateMultipartUploadResponse response = s3FactoryService.callS3Method(request, s3, S3Method.CREATE_MULTIPART_UPLOAD);
		log.info(logConfig.getLogTemplate(), "createMultipartUpload-response", s3KeyBO.getKey(), response);
		
		return response.uploadId();
	}
	
	@Override
	public S3KeyBO uploadPart(S3Client s3, S3KeyBO s3KeyBO) {
		log.info(logConfig.getLogTemplate(), "uploadPart-param", s3KeyBO.getKey(), s3KeyBO);
		
		UploadPartRequest request = UploadPartRequest.builder()
										.bucket(s3KeyBO.getBucket())
										.key(s3KeyBO.getKey())
										.uploadId(s3KeyBO.getUploadId())
										.partNumber(s3KeyBO.getPartNumber())
										.build();
		
		RequestBody requestBody = RequestBody.fromBytes(s3KeyBO.getBodys());
		
		UploadPartResponse response = s3FactoryService.callS3MethodWithBody(request, requestBody, s3, S3Method.UPLOAD_PART);
		log.info(logConfig.getLogTemplate(), "uploadPart-response", s3KeyBO.getKey(), response);
		
		s3KeyBO.setETag(response.eTag());
		return s3KeyBO;
	}
	
	@Override
	public S3KeyBO completeMultipartUpload(S3Client s3, S3KeyBO s3KeyBO) {
		log.info(logConfig.getLogTemplate(), "completeMultipartUpload-param", s3KeyBO.getKey(), s3KeyBO);
		
		List<CompletedPart> completedParts = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(s3KeyBO.getETagList())){
			s3KeyBO.getETagList().forEach(obj -> {
				completedParts.add(CompletedPart.builder()
							.partNumber(obj.getPartNumber())
							.eTag(obj.getETag()).build());
			});
		}
		
		CompleteMultipartUploadRequest request =CompleteMultipartUploadRequest.builder()
														.bucket(s3KeyBO.getBucket())
														.key(s3KeyBO.getKey())
														.uploadId(s3KeyBO.getUploadId())
														.multipartUpload(CompletedMultipartUpload.builder()
															.parts(completedParts).build())
														.build();
		
		CompleteMultipartUploadResponse response = s3FactoryService.callS3Method(request, s3, S3Method.COMPLETE_MULTIPART_UPLOAD);
		log.info(logConfig.getLogTemplate(), "completeMultipartUpload-response", s3KeyBO.getKey(), response);
		
		s3KeyBO.setVersionId(response.versionId());
		return s3KeyBO;
	}
	
	@Override
	public void abortMultipartUpload(S3Client s3, S3KeyBO s3KeyBO) {
		log.info(logConfig.getLogTemplate(), "abortMultipartUpload-param", s3KeyBO.getKey(), s3KeyBO);
		
		AbortMultipartUploadRequest request = AbortMultipartUploadRequest.builder()
												.bucket(s3KeyBO.getBucket())
												.key(s3KeyBO.getKey())
												.uploadId(s3KeyBO.getUploadId())
												.build();
		
		AbortMultipartUploadResponse response = s3FactoryService.callS3Method(request, s3, S3Method.ABORT_MULTIPART_UPLOAD);
		log.info(logConfig.getLogTemplate(), "abortMultipartUpload-response", s3KeyBO.getKey(), response);
	}
	
	@Override
	public List<FilePartInfoBO> listParts(S3Client s3, S3KeyBO s3KeyBO) {
		log.info(logConfig.getLogTemplate(), "listParts-param", s3KeyBO.getKey(), s3KeyBO);
		
		ListPartsRequest request = ListPartsRequest.builder()
										.bucket(s3KeyBO.getBucket())
										.key(s3KeyBO.getKey())
										.uploadId(s3KeyBO.getUploadId())
										.build();
		
		ListPartsResponse response = s3FactoryService.callS3Method(request, s3, S3Method.LIST_PARTS);
		List<Part> partList = response.parts();
		
		List<FilePartInfoBO> filePartInfoBOS = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(partList)){
			partList.forEach(obj -> {
				FilePartInfoBO filePartInfoBO = new FilePartInfoBO();
				filePartInfoBO.setETag(obj.eTag());
				filePartInfoBO.setPartNumber(obj.partNumber());
				filePartInfoBO.setSize(obj.size());
				filePartInfoBO.setLastModified(Date.from(obj.lastModified()));
				filePartInfoBOS.add(filePartInfoBO);
			});
		}
		return filePartInfoBOS;
	}
	@Override
	public String getMultiUploadId(S3Client s3,S3KeyBO s3KeyBO){
		log.info(logConfig.getLogTemplate(), "getMultiUploadId-param", s3KeyBO.getKey(), s3KeyBO);
		
		ListMultipartUploadsRequest request = ListMultipartUploadsRequest.builder()
												.bucket(s3KeyBO.getBucket())
												.prefix(s3KeyBO.getKey())
												.build();
		ListMultipartUploadsResponse response = s3FactoryService.callS3Method(request, s3, S3Method.LIST_MULTIPART_UPLOADS);
		log.info(logConfig.getLogTemplate(), "getMultiUploadId-response", s3KeyBO.getKey(), response);
		
		List<MultipartUpload> uploads = response.uploads();
		if(!CollectionUtils.isEmpty(uploads)){
			MultipartUpload multipartUpload = uploads.get(0);
			return multipartUpload.uploadId();
		}
		return null;
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
