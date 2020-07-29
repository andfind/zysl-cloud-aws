package com.zysl.cloud.aws.web.controller;


import com.zysl.cloud.aws.api.req.BaseBucketRequest;
import com.zysl.cloud.aws.api.req.GetBucketsRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.aws.api.srv.S3BucketSrv;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.rule.service.ISysBucketManager;
import com.zysl.cloud.aws.web.validator.BaseBucketRequestV;
import com.zysl.cloud.aws.web.validator.CreateBucketRequestV;
import com.zysl.cloud.aws.web.validator.SetFileVersionRequestV;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class S3BucketController extends BaseController implements S3BucketSrv {
	@Autowired
	private IS3BucketService s3BucketService;
	@Autowired
	private ISysBucketManager sysBucketManager;


	@Override
	public BaseResponse<String> createBucket(BaseBucketRequest request){
		return ServiceProvider.call(request, CreateBucketRequestV.class, String.class,req->{
			s3BucketService.createBucket(req.getBucketName(),req.getServerNo());
			return request.getBucketName();
		},"createBucket");
	}


	@Override
	public BaseResponse<String> updateFileVersion(@RequestBody SetFileVersionRequest request){
		return ServiceProvider.call(request, SetFileVersionRequestV.class, String.class,req->{
			s3BucketService.setBucketVersion(request);
			return RespCodeEnum.SUCCESS.getName();
		},"enableBucketVersion");
	}

	@Override
	public BasePaginationResponse<String> getBuckets(GetBucketsRequest request){
		return ServiceProvider.callList(request, null, String.class,(req,myPage)->{
			return s3BucketService.getS3Buckets(request.getServerNo());
		},"getBuckets");
	}
	
	@Override
	public BaseResponse<String> info(BaseBucketRequest request){
		return ServiceProvider.call(request, BaseBucketRequestV.class, String.class,req->{
			s3BucketService.getBucketInfo(request.getBucketName());
			return RespCodeEnum.SUCCESS.getName();
		},"bucketInfo");
	}
	
	
	
	@Override
	public BaseResponse<String> delete(BaseBucketRequest request){
		return ServiceProvider.call(request, BaseBucketRequestV.class, String.class,req->{
			sysBucketManager.delete(request.getBucketName());
			return RespCodeEnum.SUCCESS.getName();
		},"deleteBucket");
	}
}
