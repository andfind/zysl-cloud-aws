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
	//是否物理删除，1是0否，默认0
	private Integer deleteStore;
}
