package com.zysl.cloud.aws.biz.service.s3.impl;

import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.utils.DataAuthUtils;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.LogConfig;
import com.zysl.cloud.aws.domain.bo.FilePartInfoBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.LogHelper;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.Delete;
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
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.model.TaggingDirective;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import sun.misc.BASE64Decoder;

@Slf4j
@Service("s3FileService")
public class S3FileServiceImpl implements IS3FileService<S3ObjectBO> {

	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private BizConfig bizConfig;
	@Autowired
	private DataAuthUtils dataAuthUtils;
	@Autowired
	private IS3BucketService s3BucketService;
	@Autowired
	private LogConfig logConfig;


	@Override
	public S3ObjectBO create(S3ObjectBO t){
		LogHelper.info(getClass(),"createFile.param",t.bucketKey(),t.toString());
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName(),Boolean.TRUE);

		PutObjectRequest.Builder request = PutObjectRequest.builder()
												.bucket(t.getBucketName())
												.key(StringUtils.join(t.getPath() ,t.getFileName()))
												.contentEncoding(t.getContentEncoding())
												.expires(t.getExpires() == null ? null : t.getExpires().toInstant());
		//获取目标文件标签内容
		Tagging tagging = S3Utils.creatTagging(t.getTagList());
		if(null != tagging){
			request.tagging(tagging);
		}

		PutObjectResponse response = s3FactoryService.callS3MethodWithBody(request.build(),RequestBody.fromBytes(t.getBodys()),s3Client, S3Method.PUT_OBJECT);
		LogHelper.info(getClass(),"createFile.response",t.getPath(),response);

		t.setVersionId(this.getLastVersion(t));
		return t;
	}

	@Override
	public List<TagBO> mergeTags(List<TagBO> oldTagList, List<TagBO> tagList){
		List<TagBO> newTagList = Lists.newArrayList();

		//将新标签集合转成map
        Map<String, String> tagMap = tagList.stream().collect(Collectors.toMap(TagBO::getKey, TagBO::getValue));
        //遍历新增的标签和已有标签，标签key相同，则修改原有标签，标签key新增没有则保留

		if(CollectionUtils.isEmpty(oldTagList)){
			return tagList;
		}else{
			List<TagBO> removalList = oldTagList.stream().filter(obj -> StringUtils.isEmpty(tagMap.get(obj.getKey()))).collect(Collectors.toList());
			//将去重之后的集合和新添加的标签集合合并
			tagList.addAll(removalList);
			return tagList;
		}
	}

	@Override
	public List<TagBO> addTags(S3ObjectBO t, List<TagBO> tagList){
		List<TagBO> newTagList = Lists.newArrayList();

		//将新标签集合转成map
        Map<String, String> tagMap = tagList.stream().collect(Collectors.toMap(TagBO::getKey, TagBO::getValue));

        //查询对象原有标签
		List<TagBO> oldTagList = this.getTags(t);

		//遍历新增的标签和已有标签，标签key相同，则修改原有标签，标签key新增没有则保留
        List<TagBO> removalList = oldTagList.stream().filter(obj -> StringUtils.isEmpty(tagMap.get(obj.getKey()))).collect(Collectors.toList());
        //将去重之后的集合和新添加的标签集合合并
        tagList.addAll(removalList);

		return tagList;
	}

	@Override
	public String createMultipartUpload(S3ObjectBO t) {
		LogHelper.info(getClass(),"createMultipartUpload.param", t.bucketKey(),t.toString());
		
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//获取入参
        CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
												.bucket(t.getBucketName())
												.key(StringUtils.join(t.getPath(), t.getFileName()))
												.build();

		CreateMultipartUploadResponse response = s3FactoryService.callS3Method(request, s3, S3Method.CREATE_MULTIPART_UPLOAD);
		
		LogHelper.info(getClass(),"createMultipartUpload.response", t.bucketKey(),response);
		
		return response.uploadId();
	}

	@Override
	public S3ObjectBO uploadPart(S3ObjectBO t) {
		LogHelper.info(getClass(),"uploadPart.param", t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		
		UploadPartRequest request = UploadPartRequest.builder()
				.bucket(t.getBucketName())
				.key(StringUtils.join(t.getPath(), t.getFileName()))
				.uploadId(t.getUploadId())
				.partNumber(t.getPartNumber())
				.build();
		
		RequestBody requestBody = RequestBody.fromBytes(t.getBodys());

		UploadPartResponse response = s3FactoryService.callS3MethodWithBody(request, requestBody, s3, S3Method.UPLOAD_PART);
		t.setETag(response.eTag());
		
		LogHelper.info(getClass(),"uploadPart.response", t.bucketKey(),response);
		return t;
	}

	@Override
	public S3ObjectBO completeMultipartUpload(S3ObjectBO t) {
		LogHelper.info(getClass(),"completeMultipartUpload.param", t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		List<CompletedPart> completedParts = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(t.getETagList())){
			t.getETagList().forEach(obj -> {
				completedParts.add(CompletedPart.builder()
						.partNumber(obj.getPartNumber())
						.eTag(obj.getETag()).build());
			});
		}

		CompleteMultipartUploadRequest request =
				CompleteMultipartUploadRequest.builder()
						.bucket(t.getBucketName())
						.key(StringUtils.join(t.getPath(), t.getFileName()))
						.uploadId(t.getUploadId())
						.multipartUpload(CompletedMultipartUpload.builder()
								.parts(completedParts).build())
						.build();

		CompleteMultipartUploadResponse response = s3FactoryService.callS3Method(request, s3, S3Method.COMPLETE_MULTIPART_UPLOAD);
		t.setVersionId(response.versionId());
		
		LogHelper.info(getClass(),"completeMultipartUpload.response", t.bucketKey(),response);
		
		return t;
	}
	
	

	@Override
	public void abortMultipartUpload(S3ObjectBO t) {
		LogHelper.info(getClass(),"abortMultipartUpload.param", t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		
		AbortMultipartUploadRequest request = AbortMultipartUploadRequest.builder()
											.bucket(t.getBucketName())
											.key(StringUtils.join(t.getPath(), t.getFileName()))
											.uploadId(t.getUploadId())
											.build();
		
		AbortMultipartUploadResponse response = s3FactoryService.callS3Method(request, s3, S3Method.ABORT_MULTIPART_UPLOAD);
		
		LogHelper.info(getClass(),"completeMultipartUpload.response", t.bucketKey(),response);
	}

	@Override
	public String getLastVersion(S3ObjectBO t) {
		LogHelper.info(getClass(),"getLastVersion.param", t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3ObjectBO s3ObjectBO = this.getBaseInfo(t);
		
		if(s3ObjectBO != null && s3ObjectBO.getVersionId() != null){
			return  s3ObjectBO.getVersionId();
		}
		return null;
	}

	@Override
	public List<FilePartInfoBO> listParts(S3ObjectBO t) {
		LogHelper.info(getClass(),"listParts.param", t.bucketKey(),t.toString());
		
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		ListPartsRequest request = ListPartsRequest.builder()
				.bucket(t.getBucketName())
				.key(StringUtils.join(t.getPath(), t.getFileName()))
				.uploadId(t.getUploadId())
				.build();

		ListPartsResponse response = s3FactoryService.callS3Method(request, s3, S3Method.LIST_PARTS);
		LogHelper.info(getClass(),"listParts.response", t.bucketKey(),response);
		
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
	public String getMultiUploadId(S3ObjectBO t) {
		LogHelper.info(getClass(),"getMultiUploadId.param", t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		
		
		ListMultipartUploadsRequest request = ListMultipartUploadsRequest.builder()
				.bucket(t.getBucketName())
				.prefix(StringUtils.join(t.getPath(),t.getFileName()))
				.build();
		ListMultipartUploadsResponse response = s3FactoryService.callS3Method(request, s3, S3Method.LIST_MULTIPART_UPLOADS);
		LogHelper.info(getClass(),"getMultiUploadId.response", t.bucketKey(),response);
		
		List<MultipartUpload> uploads = response.uploads();
		if(!CollectionUtils.isEmpty(uploads)){
			MultipartUpload multipartUpload = uploads.get(0);
			return multipartUpload.uploadId();
		}
		return null;
	}


	@Override
	public void delete(S3ObjectBO t){
		LogHelper.info(getClass(),"deleteObject.param", t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		DeleteObjectsRequest deleteObjectsRequest = null;
		Delete delete = null;
		//逻辑删除或者指定版本号
		if(StringUtils.isNotBlank(t.getVersionId()) || DeleteStoreEnum.NOCOVER.getCode().equals(t.getDeleteStore())){
			ObjectIdentifier.Builder objectIdentifier = ObjectIdentifier.builder()
															.key(StringUtils.join(t.getPath() ,t.getFileName()));
			if(StringUtils.isNotBlank(t.getVersionId())){
				objectIdentifier.versionId(t.getVersionId());
			}
													;
			List<ObjectIdentifier> objects = new ArrayList<>();
			objects.add(objectIdentifier.build());
			delete = Delete.builder().objects(objects).build();
			
		}else if(DeleteStoreEnum.COVER.getCode().equals(t.getDeleteStore())){
			//删除整个文件信息, 先查询文件的版本信息
			List<S3ObjectBO> objectList = getVersions(t);

			List<ObjectIdentifier> objects = Lists.newArrayList();
			//查询文件的版本信息
			if(!CollectionUtils.isEmpty(objectList)){
				objectList.forEach(obj -> {
					ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
															.key(obj.getFileName())
															.versionId(obj.getVersionId()).build();
					objects.add(objectIdentifier);
				});
				
				//删除列表
				delete = Delete.builder().objects(objects).build();
			}
			
		}
		//逻辑删除
		if(delete != null){
			deleteObjectsRequest = DeleteObjectsRequest.builder()
										.bucket(t.getBucketName())
										.delete(delete)
										.build();
			//文件删除
			DeleteObjectsResponse response = s3FactoryService.callS3Method(deleteObjectsRequest, s3, S3Method.DELETE_OBJECTS);
			LogHelper.info(getClass(),"deleteObject.param", t.bucketKey(),response);
		}
		
	}

	@Override
	public void modify(S3ObjectBO t){
		LogHelper.info(getClass(),"modifyFile.param",t.bucketKey(),t.toString());
		//目前修改文件标签信息
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName(),Boolean.TRUE);

		//设置标签入参
		List<TagBO> tageList = t.getTagList();
		//文件tage设置参数
		PutObjectTaggingRequest.Builder request = PutObjectTaggingRequest.builder()
														.bucket(t.getBucketName())
														.key(StringUtils.join(t.getPath() ,t.getFileName()));
		
		Tagging tagging = S3Utils.creatTagging(tageList);
		if(tagging != null){
			request.tagging(tagging);
		}
		//设置版本
		if(!StringUtils.isEmpty(t.getVersionId())){
			request.versionId(t.getVersionId());
		}

		PutObjectTaggingResponse response = s3FactoryService.callS3Method(request.build(),s3,S3Method.PUT_OBJECT_TAGGING);
		LogHelper.info(getClass(),"modifyFile.response",t.bucketKey(),response);

	}

	@Override
	public void rename(S3ObjectBO src,S3ObjectBO dest){
		log.warn("--rename---TODO-----");
	}

	@Override
	public S3ObjectBO copy(S3ObjectBO src,S3ObjectBO dest){
		LogHelper.info(getClass(),"copyFile.response",src.bucketKey(),dest.bucketKey());
		//设置源文件路径，转码
		String copySourceUrl = null;
		try{
			copySourceUrl = src.getBucketName() + BizConstants.PATH_SEPARATOR + src.getPath() + src.getFileName();
			copySourceUrl = java.net.URLEncoder.encode(copySourceUrl, "utf-8");
			LogHelper.info(getClass(),"copyFile.copySourceUrl",src.bucketKey(),copySourceUrl);
		
		}catch (Exception e){
			throw new AppLogicException(ErrCodeEnum.S3_COPY_SOURCE_ENCODE_ERROR.getCode());
		}

		/**
		 * 判断两个bucket是否在同一台服务器，
		 * 不在一台服务器则下载上传，在则复制
		 */
		if(s3FactoryService.judgeBucket(src.getBucketName(), dest.getBucketName())){
			LogHelper.info(getClass(),"copyFile",src.bucketKey(),"serverNo is same");
			//获取s3初始化对象
			S3Client s3 = s3FactoryService.getS3ClientByBucket(src.getBucketName(),Boolean.TRUE);

			//复制文件
			CopyObjectRequest.Builder request = CopyObjectRequest.builder()
													.copySource(copySourceUrl)
													.bucket(dest.getBucketName())
													.key(StringUtils.join(dest.getPath() ,dest.getFileName()));
			
			Tagging tagging = S3Utils.creatTagging(dest.getTagList());
			if(tagging != null){
				request.tagging(tagging).taggingDirective(TaggingDirective.REPLACE);
			}
			
			CopyObjectResponse response = s3FactoryService.callS3Method(request.build(),s3,S3Method.COPY_OBJECT);
			LogHelper.info(getClass(),"copyFile.response",src.bucketKey(),response);
			dest.setVersionId(response.versionId());
		}else{
			log.debug("s3file.copy.judgeBucket.返回false,两个bucket不在同一台服务器");
			//查询源文件内容
			S3ObjectBO s3ObjectBO = this.getInfoAndBody(src);
			dest.setBodys(s3ObjectBO.getBodys());
			S3ObjectBO object = this.create(dest);
			dest.setVersionId(object.getVersionId());
		}
		return dest;
	}

	@Override
	public void move(S3ObjectBO src,S3ObjectBO dest){
		LogHelper.info(getClass(),"moveFile.param",src.getPath(),dest.getPath());
		//先复制文件
		this.copy(src, dest);
		//再删除文件
		this.delete(src);
		LogHelper.info(getClass(),"moveFile.success",src.getPath(),dest.getPath());
	}

	@Override
	public S3ObjectBO getBaseInfo(S3ObjectBO t){
		LogHelper.info(getClass(),"getBaseInfo.param",t.bucketKey(),t.toString());
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		HeadObjectRequest.Builder request = HeadObjectRequest.builder()
												.bucket(t.getBucketName())
												.key(StringUtils.join(t.getPath() ,t.getFileName()));
		if(StringUtils.isNotEmpty(t.getVersionId())){
			request.versionId(t.getVersionId());
		}

		HeadObjectResponse response = s3FactoryService.callS3Method(request.build(), s3Client, S3Method.HEAD_OBJECT, Boolean.FALSE);
		LogHelper.info(getClass(),"getBaseInfo.response",t.bucketKey(),response);
		
		if(response != null){
			t.setVersionId(response.versionId());
			t.setContentLength(response.contentLength());
			t.setExpires(DateUtils.from(response.expires()));
			t.setLastModified(DateUtils.from(response.lastModified()));
			t.setContentEncoding(response.contentEncoding());
			t.setContentLanguage(response.contentLanguage());
			t.setContentType(response.contentType());
			
			LogHelper.info(getClass(),"getBaseInfo.rst",t.bucketKey(),t.toString());
			return t;
		}
		
		return null;
	}

	@Override
	public S3ObjectBO getDetailInfo(S3ObjectBO t){
		LogHelper.info(getClass(),"getDetailInfo.param",t.bucketKey(),t.toString());
		
		//查询文件基础信息
		getBaseInfo(t);
		//查询文件标签
		List<TagBO> tagList = getTags(t);
		t.setTagList(tagList);

		return t;
	}

	@Override
	public S3ObjectBO getInfoAndBody(S3ObjectBO t){
		LogHelper.info(getClass(),"getInfoAndBody.param",t.bucketKey(),t.toString());
		//查询文件基础信息
		getDetailInfo(t);

		//查询文件内容
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		//获取下载对象
		GetObjectRequest.Builder request = GetObjectRequest.builder()
												.bucket(t.getBucketName())
												.key(StringUtils.join(t.getPath() ,t.getFileName()))
												.range(t.getRange());
		if(StringUtils.isNotEmpty(t.getVersionId())){
			request.versionId(t.getVersionId());
		}

		try{
			ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(request.build(),ResponseTransformer.toBytes());
			GetObjectResponse objectResponse = objectAsBytes.response();
			
			Date date1 = Date.from(objectResponse.lastModified());
			Date date2 = DateUtils.createDate(bizConfig.DOWNLOAD_TIME);
			
			byte[] bytes = objectAsBytes.asByteArray();
			LogHelper.info(getClass(),"getInfoAndBody.bytes.length",t.bucketKey(),(bytes == null ? "0" : bytes.length + ""));
			
			if(DateUtils.doCompareDate(date1, date2) < 0){
				//进行解码
				BASE64Decoder decoder = new BASE64Decoder();
				byte[] fileContent = new byte[0];
				try {
					fileContent = decoder.decodeBuffer(new String(bytes));
				} catch (IOException e) {
					LogHelper.info(getClass(),"getInfoAndBody",t.bucketKey(),"IOException");
				}
				t.setBodys(fileContent);
				return t;
			}else {
				t.setBodys(bytes);
				return t;
			}
		}catch (NoSuchKeyException e){
			LogHelper.warn(getClass(),"getInfoAndBody",t.bucketKey(),"NoSuchKeyException");
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
		}catch (AwsServiceException | SdkClientException  e){
			LogHelper.warn(getClass(),"getInfoAndBody",t.bucketKey(), e.getMessage(),e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_AWS_SERVICE_EXCEPTION.getCode());
		}catch (Exception e){
			log.error(logConfig.getLogTemplate(),"getInfoAndBody",t.bucketKey(), e.getMessage(),e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_ERROR.getCode());
		}
	}

	@Override
	public List<S3ObjectBO> getVersions(S3ObjectBO t){
		LogHelper.info(getClass(),"getVersions.param",t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3Client = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		ListObjectVersionsRequest request = ListObjectVersionsRequest.builder().
													bucket(t.getBucketName()).
													prefix(StringUtils.join(t.getPath() ,t.getFileName())).
													build();

		ListObjectVersionsResponse response = s3FactoryService.callS3Method(request,s3Client, S3Method.LIST_OBJECT_VERSIONS);
		
		List<ObjectVersion> list = response.versions();
		List<S3ObjectBO> versionList = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(list)){
			list.forEach(obj -> {
				S3ObjectBO s3Object = new S3ObjectBO();
				//版本信息
				s3Object.setVersionId(obj.versionId());
				s3Object.setContentLength(Long.valueOf(obj.size()));
				s3Object.setLastModified(Date.from(obj.lastModified()));
				//文件信息
				s3Object.setBucketName(response.name());
				s3Object.setFileName(response.prefix());
				versionList.add(s3Object);
			});
		}

		return versionList;
	}

	@Override
	public S3ObjectBO rename(S3ObjectBO t) {
		LogHelper.info(getClass(),"rename.param",t.bucketKey(),t.toString());

		//先查询文件内容
		S3ObjectBO s3ObjectBO = this.getInfoAndBody(t);

		t.setBodys(s3ObjectBO.getBodys());
		//在重新上传文件
		S3ObjectBO result = this.create(t);
		result.setTagFilename(t.getTagFilename());
		return result;
	}

	@Override
	public void checkDataOpAuth(S3ObjectBO s3ObjectBO,String opAuthTypes){
		LogHelper.info(getClass(),"checkDataOpAuth.param",s3ObjectBO.bucketKey(),s3ObjectBO.toString());
		String tokenAuth = dataAuthUtils.getUserAuth();

		//没传则不校验
		if(StringUtils.isBlank(tokenAuth)){
			LogHelper.info(getClass(),"checkDataOpAuth",s3ObjectBO.bucketKey(),"tokenAuth is null");
			return ;
		}

		String objAuths = null;
		// 是对象
		if (StringUtils.isNotBlank(s3ObjectBO.getFileName())) {
			//读取对象的标签--权限列表
			objAuths = S3Utils.getTagValue(getTags(s3ObjectBO),S3TagKeyEnum.USER_AUTH.getCode());
			if(dataAuthUtils.checkAuth(opAuthTypes,objAuths)){
				LogHelper.info(getClass(),"checkDataOpAuth.checkAuth.1",s3ObjectBO.bucketKey(),objAuths);
				return;
			}
		}
		//逐级往上检查目录
		String curPath = s3ObjectBO.getPath();
		if(StringUtils.isNotBlank(curPath) && !curPath.endsWith(BizConstants.PATH_SEPARATOR)){
			curPath += BizConstants.PATH_SEPARATOR;
		}
		S3ObjectBO bo = s3ObjectBO;
		bo.setBucketName(s3ObjectBO.getBucketName());
		while(StringUtils.isNotBlank(curPath)){
			bo.setPath(curPath);
			objAuths = S3Utils.getTagValue(getTags(s3ObjectBO),S3TagKeyEnum.USER_AUTH.getCode());
			if(dataAuthUtils.checkAuth(opAuthTypes,objAuths)){
				LogHelper.info(getClass(),"checkDataOpAuth.checkAuth.2",s3ObjectBO.bucketKey(),objAuths);
				return;
			}
			//截取上层目录
			curPath = curPath.substring(0,curPath.length() - 1);
			if(curPath.lastIndexOf(BizConstants.PATH_SEPARATOR) > -1){
				curPath = curPath.substring(0,curPath.lastIndexOf(BizConstants.PATH_SEPARATOR) + 1);
			}
		}
		
		//检查bucket
		List<TagBO> bucketTags = s3BucketService.getBucketTag(bo.getBucketName());
		if(!CollectionUtils.isEmpty(bucketTags)){
			objAuths = S3Utils.getTagValue(bucketTags,S3TagKeyEnum.USER_AUTH.getCode());
			if(dataAuthUtils.checkAuth(opAuthTypes,objAuths)){
				LogHelper.info(getClass(),"checkDataOpAuth.checkAuth.3",s3ObjectBO.bucketKey(),objAuths);
				return;
			}
		}

		//遍历后还是无法匹配
		LogHelper.warn(getClass(),"checkDataOpAuth.rst",s3ObjectBO.bucketKey(),"check.data.op.auth.failed");
		throw new AppLogicException(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_FAILED.getCode());
	}

	@Override
	public List<TagBO> getTags(S3ObjectBO t) {
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		//查询文件的标签信息
		GetObjectTaggingRequest.Builder request = GetObjectTaggingRequest.builder()
													.bucket(t.getBucketName())
													.key(StringUtils.join(t.getPath() ,t.getFileName()));
		if(StringUtils.isNotEmpty(t.getVersionId())){
			request.versionId(t.getVersionId());
		}
		GetObjectTaggingResponse response = s3FactoryService.callS3Method(request.build(), s3, S3Method.GET_OBJECT_TAGGING, false);
		log.debug("s3file.getDetailInfo.response:{}", response);

		if(null != response){
			List<Tag> list = response.tagSet();
			List<TagBO> tagList = Lists.newArrayList();
			if(!CollectionUtils.isEmpty(list)){
				list.forEach(obj -> {
					TagBO tag = new TagBO();
					tag.setKey(obj.key());
					tag.setValue(obj.value());
					tagList.add(tag);
				});
			}

			return tagList;
		}else{
			return Lists.newArrayList();
		}
	}

}
