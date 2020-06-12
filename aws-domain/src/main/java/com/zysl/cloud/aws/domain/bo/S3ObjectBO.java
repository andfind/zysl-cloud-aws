package com.zysl.cloud.aws.domain.bo;

import com.zysl.cloud.utils.StringUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3ObjectBO extends BaseFileBO implements Serializable {

	private static final long serialVersionUID = 930239064897318736L;

	private String bucketName;

	private Date lastModified;

	//是否物理删除，1是0否，默认0
	private Integer deleteStore;
	//权限校验参数
	private String userId;
	//断点续传id
	private String uploadId;
	//续传次数
	private Integer partNumber;
	//断点续传标记
	private String eTag;
	private List<MultipartUploadBO> eTagList;
	//标签中的文件名
	private String tagFilename;

	//元素 List
	//标签 List
	List<TagBO> tagList;
	//子目录 List
	List<ObjectInfoBO> folderList;
	//子文件 List
	List<ObjectInfoBO> fileList;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"S3ObjectBO\":{");
		if (bucketName != null) {
			sb.append("bucketName='").append(bucketName).append('\'');
		}
		if (lastModified != null) {
			sb.append(", lastModified=").append(lastModified);
		}
		if (deleteStore != null) {
			sb.append(", deleteStore=").append(deleteStore);
		}
		if (userId != null) {
			sb.append(", userId='").append(userId).append('\'');
		}
		if (uploadId != null) {
			sb.append(", uploadId='").append(uploadId).append('\'');
		}
		if (partNumber != null) {
			sb.append(", partNumber=").append(partNumber);
		}
		if (eTag != null) {
			sb.append(", eTag='").append(eTag).append('\'');
		}
		if (eTagList != null) {
			sb.append(", eTagList=").append(eTagList);
		}
		if (tagFilename != null) {
			sb.append(", tagFilename='").append(tagFilename).append('\'');
		}
		if (tagList != null) {
			sb.append(", tagList=").append(tagList);
		}
		if (folderList != null) {
			sb.append(", folderList=").append(folderList);
		}
		if (fileList != null) {
			sb.append(", fileList=").append(fileList);
		}
		sb.append("},\"super-S3ObjectBO\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	/**
	 * 返回
	 * @description
	 * @author miaomingming
	 * @param
	 * @return java.lang.String
	 **/
	public String key(){
		return StringUtils.join(this.getBucketName(),":",this.getPath());
	}
}
