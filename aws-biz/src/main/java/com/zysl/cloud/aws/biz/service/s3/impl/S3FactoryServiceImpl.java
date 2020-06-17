package com.zysl.cloud.aws.biz.service.s3.impl;

import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.config.S3ServerConfig;
import com.zysl.cloud.aws.prop.S3ServerProp;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
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
		
		log.warn("not.exist.serverNo:{}",serverNo);
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
							log.error("s3.no.space.serverNo:{}",s3ServerProp.getServerNo());
							throw new AppLogicException(ErrCodeEnum.S3_NO_SPACE_WARN.getCode());
						}
						return getS3ClientByServerNo(s3ServerProp.getServerNo());
					}
				}
			}
		}
		
		log.error("s3.not.exist.bucket:{}",bucketName);
		throw new AppLogicException(ErrCodeEnum.S3_BUCKET_NOT_EXIST.getCode());
	}

	@Override
	public Boolean isExistBucket(String bucketName){
		return getBucketServerNoMap().containsKey(bucketName);
	}

	@Override
	public void addBucket(String bucketName,String serverNo){
		Map<String, S3ServerProp> s3ServerPropMap = (Map<String, S3ServerProp>)redisTemplate.opsForValue().get(S3_SERVER_PROP_MAP_NAME);
		if(s3ServerPropMap != null && !s3ServerPropMap.isEmpty()){
			for(S3ServerProp s3ServerProp:s3ServerPropMap.values()){
				if(s3ServerProp.getServerNo().equals(serverNo)){
					Map<String, String> bucketMap = s3ServerProp.getBucketMap();
					if(bucketMap == null){
						bucketMap = new HashMap<>();
					}
					bucketMap.put(bucketName,serverNo);
					s3ServerProp.setBucketMap(bucketMap);
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
		log.info("=callS3Method:service_name:{},methodName:{},param:{}=",S3Client.SERVICE_NAME,methodName, r);
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

			if(response == null || response.sdkHttpResponse() == null ){
				log.error("callS3Method.invoke({})->no.response",methodName);
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_RESPONSE.getCode());
			} else if(!response.sdkHttpResponse().isSuccessful()){
				log.error("callS3Method.invoke({})->response.status.error:{}",methodName,response.sdkHttpResponse().statusCode());
				
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_RESPONSE_STATUS_ERROR.getCode());
			}else{
				log.debug("callS3Method.invoke({}).success:{}",methodName, response);
			}
		}catch (NoSuchKeyException e){
			log.error("callS3Method.invoke({}).NoSuchKeyException",methodName);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
		}catch (BucketAlreadyExistsException e){
			log.error("callS3Method.invoke({}).BucketAlreadyExistsException",methodName);
			throw new AppLogicException(ErrCodeEnum.S3_CREATE_BUCKET_EXIST.getCode());
		}catch (IllegalAccessException | IllegalArgumentException  e){
			log.error("callS3Method.invoke({}).error:{}",methodName,e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_INVOKE_ERROR.getCode());
		}catch (InvocationTargetException e){
			log.error("callS3Method.invoke({}).error=>{}",methodName,e.getTargetException().getMessage(),e);
			if(e.getTargetException() instanceof NoSuchKeyException){
				log.error("noSuchKey {} {} [ES_LOG_EXCEPTION]", r, ExceptionUtil.getMessage(e));
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}else{
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_S3_EXCEPTION.getCode());
			}
			
			
		}catch (Exception e){
			log.error("callS3Method.error({}),param:{},err:",methodName,r,e);
			throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_ERROR.getCode());
		}finally{
			log.info("=callS3Method.end:service_name:{},methodName:{},param:{}=use:{}",S3Client.SERVICE_NAME,methodName, r,(System.currentTimeMillis()-start));
		}
		return response;
	}




	@Override
	public <T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName,Boolean throwLogicException){
		log.info("=callS3Method:service_name:{},methodName:{},param:{}=",S3Client.SERVICE_NAME,methodName, r);
		T response = null;
		try{
			response = callS3MethodWithBody(r,null,s3Client,methodName);
		}catch (AppLogicException e) {//AppLogicException
			if(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode().equals(e.getExceptionCode())){
				log.warn("ES_LOG callS3Method.noSuchKey({}) {}", methodName, r);
			}else{
				log.warn("ES_LOG callS3Method.error({}) {}->{}", methodName, r,ExceptionUtil.getMessage(e));
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
	@PostConstruct
	private void amazonS3ClientInit(){
		log.info("=amazonS3ClientInit.start=");
		
		Map<String, S3ServerProp> s3ServerPropMap = new HashMap<>();
		List<S3ServerProp> s3ServerProps = s3ServerConfig.getServers();
		if(!CollectionUtils.isEmpty(s3ServerProps)){
			for (S3ServerProp props : s3ServerProps) {
				//bucket列表
				props.setBucketMap(getBucketList(props.getServerNo(),createS3Client(props)));
				s3ServerPropMap.put(props.getServerNo(),props);

				log.info("=amazonS3ClientInit.success:serverNo:{}-->{}=",props.getServerNo(),props.getEndpoint());
			}
		}else{
			log.info("=amazonS3ClientInit.warn:no server found.=");
		}
		
		redisTemplate.opsForValue().set(S3_SERVER_PROP_MAP_NAME,s3ServerPropMap);
		
		log.info("=amazonS3ClientInit.end=");
	}

	
	private Map<String,String> getBucketList(String serverNo,S3Client s3Client){
		Map<String,String> bucketMap = new HashMap<>();
		ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
		ListBucketsResponse response = s3Client.listBuckets(listBucketsRequest);
		
		if(response != null && !CollectionUtils.isEmpty(response.buckets())){
			response.buckets().forEach(bucket -> {
				bucketMap.put(bucket.name(),serverNo);
				log.info("=amazonS3BucketInit.found.bucket:{}=", bucket.name());
			});
		}
		return bucketMap;
	}
	
	
	private S3Client createS3Client(S3ServerProp props){
		log.info("ES_LOG createS3Client-param {}",props.getServerNo());
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
		
		log.info("ES_LOG createS3Client-end {}",props.getServerNo());
		return s3Client;
	}
	

}
