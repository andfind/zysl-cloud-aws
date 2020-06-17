package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.req.BaseBucketRequest;
import com.zysl.cloud.aws.api.req.GetBucketsRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/aws/bucket")
public interface S3BucketSrv {
	/**
	 * 创建bucket
	 * @description
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@GetMapping("/createBucket")
	BaseResponse<String> createBucket(BaseBucketRequest request);


	/**
	 * 设置文件夹的版本控制权限
	 * @description
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/setVersion")
	BaseResponse<String> updateFileVersion(@RequestBody SetFileVersionRequest request);

	/**
	 * 查询所有bucket列表
	 * @description
	 * @author miaomingming
	 * @param request
	 * @return com.zysl.cloud.utils.common.BasePaginationResponse<java.lang.String>
	 **/
	@GetMapping("/getBuckets")
	BasePaginationResponse<String> getBuckets(GetBucketsRequest request);
	
	/**
	 * 查询bucket信息
	 * @description
	 * @author miaomingming
	 * @date 9:14 2020/6/17
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@GetMapping("/info")
	BaseResponse<String> info(BaseBucketRequest request);
	
	/**
	 * 删除bucket
	 * @description
	 * @author miaomingming
	 * @date 9:44 2020/6/17
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@GetMapping("/delete")
	BaseResponse<String> delete(BaseBucketRequest request);
}
