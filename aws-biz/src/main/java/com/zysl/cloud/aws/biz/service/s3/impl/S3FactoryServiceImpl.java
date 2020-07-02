package com.zysl.cloud.aws.biz.service.s3.impl;

import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.config.LogConfig;
import com.zysl.cloud.aws.config.S3ServerConfig;
import com.zysl.cloud.aws.prop.S3ServerProp;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.AttributeMap;

@Slf4j
@Service
public class S3FactoryServiceImpl implements IS3FactoryService {

	//s3服务器 key为SERVER_NO
	private final String S3_SERVER_PROP_MAP_NAME = "s3_server_prop_map";

	@Autowired
	private S3ServerConfig s3ServerConfig;
	
	@Resource
	private RedisTemplate<Object,Object> redisTemplate;
	
	@Resource
	private LogConfig logConfig;
	
	

	@Override
	public String getServerNo(String bucketName){
		return getBucketServerNoMap().get(bucketName);
	}


	@Override
	public S3Client getS3ClientByServerNo(String serverNo){
		Map<String, S3ServerProp> s3ServerPropMap = (Map<String, S3ServerProp>)redisTemplate.opsForValue().get(S3_SERVER_PROP_MAP_NAME);
		if(s3ServerPropMap != null && !s3ServerPropMap.isEmpty()){
			for(S3ServerProp s3ServerProp:s3ServerPropMap.values()){
				if(s3ServerProp.getServerNo().equals(serverNo)){
					return createS3Client(s3ServerProp);
				}
			}
		}
		
		log.info(logConfig.getLogTemplate(),"getS3ClientByServerNo",serverNo,"not.exist.serverNo");
		
		throw new AppLogicException(ErrCodeEnum.S3_SERVER_NO_NOT_EXIST.getCode());
	}


	@Override
	public S3Client getS3ClientByBucket(String bucketName){
		return getS3ClientByBucket(bucketName,Boolean.FALSE);
	}

	@Override
	public S3Client getS3ClientByBucket(String bucketName,Boolean isWrite) throws AppLogicException{
		Map<String, S3ServerProp> s3ServerPropMap = (Map<String, S3ServerProp>)redisTemplate.opsForValue().get(S3_SERVER_PROP_MAP_NAME);
		if(s3ServerPropMap != null && !s3ServerPropMap.isEmpty()){
			for(S3ServerProp s3ServerProp:s3ServerPropMap.values()){
				if(s3ServerProp.getBucketMap() != null && !s3ServerProp.getBucketMap().isEmpty()){
					if(s3ServerProp.getBucketMap().containsKey(bucketName)){
						//写操作但是服务器没有空间
						if(s3ServerProp.getNoSpace() != null && s3ServerProp.getNoSpace()
							&& isWrite != null && isWrite){
							log.warn(logConfig.getLogTemplate(),"getS3ClientByBucket",s3ServerProp.getServerNo(),"s3.serverNo..no.space");
							throw new AppLogicException(ErrCodeEnum.S3_NO_SPACE_WARN.getCode());
						}
						return getS3ClientByServerNo(s3ServerProp.getServerNo());
					}
				}
			}
		}
		
		log.warn(logConfig.getLogTemplate(),"getS3ClientByBucket",bucketName,"s3.bucket..not.exist");
		throw new AppLogicException(ErrCodeEnum.S3_BUCKET_NOT_EXIST.getCode());
	}

	@Override
	public Boolean isExistBucket(String bucketName){
		return getBucketServerNoMap().containsKey(bucketName);
	}

	@Override
	public void addBucket(String bucketName,String serverNo){
		updateBucket(serverNo);
	}
	
	@Override
	public void updateBucket(String serverNo){
		Map<String, S3ServerProp> s3ServerPropMap = (Map<String, S3ServerProp>)redisTemplate.opsForValue().get(S3_SERVER_PROP_MAP_NAME);
		if(s3ServerPropMap != null && !s3ServerPropMap.isEmpty()){
			for(S3ServerProp s3ServerProp:s3ServerPropMap.values()){
				if(s3ServerProp.getServerNo().equals(serverNo)){
					s3ServerProp.setBucketMap(getBucketList(serverNo,createS3Client(s3ServerProp)));
				}
			}
			redisTemplate.opsForValue().set(S3_SERVER_PROP_MAP_NAME,s3ServerPropMap);
		}
	}

	@Override
	public Map<String, String> getBucketServerNoMap(){
		Map<String, String> bucketMap = new HashMap<>();
		Map<String, S3ServerProp> s3ServerPropMap = (Map<String, S3ServerProp>)redisTemplate.opsForValue().get(S3_SERVER_PROP_MAP_NAME);
		if(s3ServerPropMap != null && !s3ServerPropMap.isEmpty()){
			for(S3ServerProp s3ServerProp:s3ServerPropMap.values()){
				if(s3ServerProp.getBucketMap() != null && !s3ServerProp.getBucketMap().isEmpty()){
					bucketMap.putAll(s3ServerProp.getBucketMap());
				}
			}
		}
		
		return bucketMap;
	}

	@Override
	public boolean judgeBucket(String bucket1, String bucket2) {
		String serverNo = getServerNo(bucket1);
		return serverNo != null && serverNo.equals(getServerNo(bucket2));
	}


	@Override
	public <T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName)
		throws AppLogicException{
		return callS3MethodWithBody(r,null,s3Client,methodName);
	}

	@Override
	public <T extends S3Response,R extends S3Request>T callS3MethodWithBody(R r, RequestBody requestBody,S3Client s3Client,String methodName) throws AppLogicException{
		log.warn(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,String.format("server:%s,param:%s",s3Client.serviceName(),r));
		long start = System.currentTimeMillis();
		T response = null;
		try{
			Object obj = null;
			if(requestBody == null){
				Method method = S3Client.class.getMethod(methodName, r.getClass());
				obj = method.invoke(s3Client,r);
			}else{
				Method method = S3Client.class.getMethod(methodName, r.getClass(),RequestBody.class);
				obj =method.invoke(s3Client,r,requestBody);
			}
			if(obj != null){
				response = (T)obj;
			}
			
			
			//结果判断
			if(response == null || response.sdkHttpResponse() == null ){
				log.warn(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,"no.response");
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_RESPONSE.getCode());
			} else if(!response.sdkHttpResponse().isSuccessful()){
				log.warn(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,String.format("response.status:%s",response.sdkHttpResponse().statusCode()));
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_RESPONSE_STATUS_ERROR.getCode());
			}else{
				log.warn(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,"success");
			}
		}catch (NoSuchKeyException e){
			log.warn(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,"NoSuchKeyException");
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
		}catch (BucketAlreadyExistsException e){
			log.warn(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,"BucketAlreadyExists");
			throw new AppLogicException(ErrCodeEnum.S3_CREATE_BUCKET_EXIST.getCode());
		}catch (IllegalAccessException | IllegalArgumentException e){
			log.error(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,ExceptionUtil.getMessage(e),e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_INVOKE_ERROR.getCode());
		}catch (InvocationTargetException e){
			if(e.getTargetException() instanceof NoSuchKeyException){
				log.warn(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,"noSuchKey");
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}else{
				log.error(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,e.getTargetException().getMessage(),e);
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_S3_EXCEPTION.getCode());
			}
		}catch (Exception e){
			log.error(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,StringUtils.join(r,"->",ExceptionUtil.getMessage(e)),e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_ERROR.getCode());
		}finally{
			log.info(logConfig.getLogTemplate(),"callS3MethodWithBody",methodName,String.format("param:%s->use:%d",r,(System.currentTimeMillis()-start)));
		}
		return response;
	}




	@Override
	public <T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName,Boolean throwLogicException){
		log.info(logConfig.getLogTemplate(),"callS3Method",methodName,r);
		T response = null;
		try{
			response = callS3MethodWithBody(r,null,s3Client,methodName);
		}catch (AppLogicException e) {//AppLogicException
			if(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode().equals(e.getExceptionCode())){
				log.warn(logConfig.getLogTemplate(),"callS3Method",methodName,String.format("param:%s->noSuchKey",r));
			}else{
				log.warn(logConfig.getLogTemplate(),"callS3Method",methodName,String.format("param:%s->%s",r,ExceptionUtil.getMessage(e)));
			}
			
			if (throwLogicException) {
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_ERROR.getCode());
			}
		}
		return response;
	}

	/**
	 * 客户端连接护初始化
	 * @description
	 * @author miaomingming
	 * @date 17:55 2020/3/23
	 * @param
	 * @return void
	 **/
	@Override
	@PostConstruct
	public void amazonS3ClientInit(){
		log.info(logConfig.getLogTemplate(),"amazonS3ClientInit","init","start");
		
		Map<String, S3ServerProp> s3ServerPropMap = new HashMap<>();
		List<S3ServerProp> s3ServerProps = s3ServerConfig.getServers();
		if(!CollectionUtils.isEmpty(s3ServerProps)){
			for (S3ServerProp props : s3ServerProps) {
				//bucket列表
				props.setBucketMap(getBucketList(props.getServerNo(),createS3Client(props)));
				s3ServerPropMap.put(props.getServerNo(),props);

//				log.info("=amazonS3ClientInit.success:serverNo:{}-->{}=",props.getServerNo(),props.getEndpoint());
				log.warn(logConfig.getLogTemplate(),"amazonS3ClientInit","init",String.format("serverNo:%s-->%s",props.getServerNo(),props.getEndpoint()));
			}
		}else{
			log.warn(logConfig.getLogTemplate(),"amazonS3ClientInit","init","no server found");
		}
		
		redisTemplate.opsForValue().set(S3_SERVER_PROP_MAP_NAME,s3ServerPropMap);
		
		log.info(logConfig.getLogTemplate(),"amazonS3ClientInit","init","success");
	}

	
	private Map<String,String> getBucketList(String serverNo,S3Client s3Client){
		Map<String,String> bucketMap = new HashMap<>();
		ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
		ListBucketsResponse response = s3Client.listBuckets(listBucketsRequest);
		
		if(response != null && !CollectionUtils.isEmpty(response.buckets())){
			response.buckets().forEach(bucket -> {
				bucketMap.put(bucket.name(),serverNo);
				log.info(logConfig.getLogTemplate(),"getBucketList","bucket",bucket.name());
			});
		}
		return bucketMap;
	}
	
	
	private S3Client createS3Client(S3ServerProp props){
		log.info(logConfig.getLogTemplate(),"createS3Client-param",props.getServerNo(),props);
		DefaultSdkHttpClientBuilder defaultSdkHttpClientBuilder = new DefaultSdkHttpClientBuilder();
		AttributeMap attributeMap = AttributeMap.builder()
			.put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
			.put(SdkHttpConfigurationOption.WRITE_TIMEOUT, Duration.ofSeconds(300))//写入超时
			.put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofSeconds(300))//读取超时
			.put(SdkHttpConfigurationOption.CONNECTION_TIMEOUT, Duration.ofSeconds(10))//连接超时
			.put(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, Duration.ofSeconds(300))//连接最大空闲超时
			.build();
		
		//初始化s3连接
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());
		S3Client s3Client = S3Client
			.builder()
			.httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap))
			.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
			.endpointOverride(URI.create(props.getEndpoint()))
			.region(Region.US_EAST_1)
			.build();
		
		log.info(logConfig.getLogTemplate(),"createS3Client",props.getServerNo(),"success");
		return s3Client;
	}
	

}
