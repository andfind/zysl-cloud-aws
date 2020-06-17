package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3KeyBO implements Serializable {
	
	private static final long serialVersionUID = 1305151760299486233L;
	
	public S3KeyBO(){}
	
	
	public S3KeyBO(String bucket){
		this.setBucket(bucket);
	}
	
	public S3KeyBO(String bucket,String key){
		this.setBucket(bucket);
		this.setKey(key);
	}
	
	public S3KeyBO(String key,String versionId,Long contentLength){
		this.setKey(key);
		this.setVersionId(versionId);
		this.setContentLength(contentLength);
	}
	
	
	private String bucket;
	
	private String key;
	
	private  String contentEncoding;
	
	private  Long contentLength;
	
	private  String contentType;
	
	private Date expires;
	
	private String versionId;
	//下载范围
	private String range;
	//上传时间
	private Date uploadTime;
	//最后修改时间
	private Date lastModified;
	//内容md5
	private String eTag;
	//标签 List
	List<TagBO> tagList;
	//数据主体
	private byte[] bodys;
	
	//分片id
	private String uploadId;
	//分片上传编号
	private Integer partNumber;
	//断点续传标记
	private List<MultipartUploadBO> eTagList;
	
	//子对象 List
	List<S3KeyBO> subKeyList;
	
	//自定义-版本号-存在tag
	private Integer versionNo;
	//是否已删除
	private Boolean isDeleted;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"S3KeyBO\":{");
		if (bucket != null) {
			sb.append("bucket='").append(bucket).append('\'');
		}
		if (key != null) {
			sb.append(", key='").append(key).append('\'');
		}
		if (contentEncoding != null) {
			sb.append(", contentEncoding='").append(contentEncoding).append('\'');
		}
		if (contentLength != null) {
			sb.append(", contentLength=").append(contentLength);
		}
		if (contentType != null) {
			sb.append(", contentType='").append(contentType).append('\'');
		}
		if (expires != null) {
			sb.append(", expires=").append(expires);
		}
		if (versionId != null) {
			sb.append(", versionId='").append(versionId).append('\'');
		}
		if (range != null) {
			sb.append(", range='").append(range).append('\'');
		}
		if (uploadTime != null) {
			sb.append(", uploadTime=").append(uploadTime);
		}
		if (lastModified != null) {
			sb.append(", lastModified=").append(lastModified);
		}
		if (eTag != null) {
			sb.append(", eTag='").append(eTag).append('\'');
		}
		if (tagList != null) {
			sb.append(", tagList=").append(tagList);
		}
		if (bodys != null) {
			sb.append(", bodys").append("_length=").append(bodys.length);
		}
		if (uploadId != null) {
			sb.append(", uploadId='").append(uploadId).append('\'');
		}
		if (partNumber != null) {
			sb.append(", partNumber=").append(partNumber);
		}
		if (eTagList != null) {
			sb.append(", eTagList=").append(eTagList);
		}
		if (subKeyList != null) {
			sb.append(", subKeyList=").append(subKeyList);
		}
		if (versionNo != null) {
			sb.append(", versionNo=").append(versionNo);
		}
		if (isDeleted != null) {
			sb.append(", isDeleted=").append(isDeleted);
		}
		sb.append("}}");
		return sb.toString();
	}
}
