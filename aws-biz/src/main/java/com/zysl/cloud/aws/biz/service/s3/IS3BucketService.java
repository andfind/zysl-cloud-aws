package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.api.req.BucketFileRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.utils.common.MyPage;
import java.util.List;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

public interface IS3BucketService {

	/**
	 * 查询s3服务器的bucket列表
	 * @description
	 * @author miaomingming
	 * @date 21:29 2020/3/22
	 * @param s3
	 * @return java.util.List<software.amazon.awssdk.services.s3.model.Bucket>
	 **/
	List<Bucket> getBucketList(S3Client s3);

	/**
	 * 查询s3服务器的bucket列表，没有传入serviceNo则查询所有
	 * @description
	 * @author miaomingming
	 * @date 10:26 2020/3/25
	 * @param serviceNo
	 * @return java.util.List<java.lang.String>
	 **/
	List<String> getS3Buckets(String serviceNo);

	/**
	 * 创建存储桶bucket
	 * @param bucketName
	 * @param serviceNo
	 * @return java.lang.Boolean
	 **/
	Boolean createBucket(String bucketName, String serviceNo);


	/**
	 * 设置bucket版本控制
	 * @description
	 * @author miaomingming
	 * @date 11:01 2020/3/25
	 * @param request
	 * @return java.lang.Boolean
	 **/
	Boolean setBucketVersion(SetFileVersionRequest request);

	/**
	 * 设置bucket的标签
	 * @param t
	 * @return
	 */
	Boolean putBucketTag(S3ObjectBO t);

	/**
	 * 获取bucket的标签
	 * @param bucketName
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	List<TagBO> getBucketTag(String bucketName);
	
	/**
	 * 查询bucket信息-->原生函数只有云信息，待讨论需求
	 * @description
	 * @author miaomingming
	 * @date 9:45 2020/6/17
	 * @param bucketName
	 * @return void
	 **/
	void getBucketInfo(String bucketName);
	
	/**
	 * 删除bucket
	 * @description
	 * @author miaomingming
	 * @date 9:46 2020/6/17
	 * @param bucketName
	 * @return void
	 **/
	void delete(S3Client s3Client,String bucketName);
}
