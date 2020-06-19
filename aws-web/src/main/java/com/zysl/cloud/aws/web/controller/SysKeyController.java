package com.zysl.cloud.aws.web.controller;

import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.srv.SysKeySrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.validator.SysFileRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SysKeyController extends BaseController implements SysKeySrv {
	
	@Autowired
	private ISysKeyManager sysKeyManager;
	@Autowired
	private WebConfig webConfig;
	
	@Override
	public BaseResponse<SysKeyDTO> create(SysKeyCreateRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, SysKeyDTO.class,req -> {
			request.formatPathURI();
			sysKeyManager.create(request);
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"create");
	}
	
	
	@Override
	public BaseResponse<SysKeyDTO> upload(HttpServletRequest httpServletRequest, SysKeyUploadRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, SysKeyDTO.class,req -> {
			request.formatPathURI();
			Boolean isCover = request.getIsCover() != null ? request.getIsCover() : Boolean.TRUE;
			request.setIsCover(isCover);
			
			byte[] bodys = HttpUtils.getBytesFromHttpRequest(httpServletRequest,request);
			
			sysKeyManager.upload(request,bodys);
			
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"upload");
	}
	
	@ResponseBody
	@Override
	public BaseResponse<String> download(HttpServletRequest request, HttpServletResponse response, SysKeyDownloadRequest downRequest) {
//		BaseResponse<String> baseResponse = new BaseResponse<>();
//		baseResponse.setSuccess(Boolean.FALSE);
//		downRequest.formatPathURI();
//
//		try{
//			if(!validator(baseResponse,downRequest, SysFileRequestV.class)){
//				return baseResponse;
//			}
//			log.info("download {} [ES_LOG_START]", downRequest.getPath());
//
//			//临时权限校验
//			if(!checkOwner(downRequest)){
//				baseResponse.setCode(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_FAILED.getCode());
//				return baseResponse;
//			}
//
//
//			//获取标签中的文件名称
//			SysKeyDTO fileDTO = sysKeyManager.info(downRequest);
//			if(fileDTO == null){
//				baseResponse.setCode(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
//				response.setStatus(RespCodeEnum.NOT_EXISTED.getCode());
//				return baseResponse;
//			}
//
//			//从头信息取Range:bytes=0-1000
//			String range = request.getHeader("Range");
//			log.info("download {} Range={}",downRequest.getPath(),range);
//			//对Range数值做校验
//			Long[] byteLength = HttpUtils.checkRange(range);
//
//			if(StringUtils.isBlank(range)){
//				log.info("{}--range is null",downRequest.getPath());
//				byteLength[1] = webConfig.getDownloadMaxFileSize() * 1024 * 1024L;
//				if(fileDTO.getSize() > byteLength[1]){
//					log.info("fileSize:{},range:{},file:{}",fileDTO.getSize(),range,StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
//					baseResponse.setCode(RespCodeEnum.ILLEGAL_PARAMETER.getCode());
//					baseResponse.setMsg("文件大小超过" + webConfig.getDownloadMaxFileSize() + "m只能分片下载.");
//					return baseResponse;
//				}
//				range = StringUtils.join("bytes=",byteLength[0],"-",byteLength[1]);
//			}
//
//			//返回数据
//			byte[] bodys = sysFileManager.getFileBodys(downRequest,range);
//
//
//			if(!StringUtils.isBlank(range)){
//				//设置响应头：Content-Range: bytes 0-2000/4932
//				byteLength[1] = byteLength[1] > fileDTO.getSize()-1 ? fileDTO.getSize()-1 : byteLength[1];
//				String rspRange = StringUtils.join("bytes ",byteLength[0],"-",byteLength[1], BizConstants.PATH_SEPARATOR,fileDTO.getSize());
//				response.setHeader("Content-Range",rspRange);
//			}
//
//
//			//下载数据
//			HttpUtils.downloadFileByte(request,response,fileDTO.getFileName(),bodys);
//			log.info("baseResponse:{}", JSON.toJSONString(baseResponse));
//			log.info("download {} [ES_LOG_SUCCESS]",downRequest.getPath());
//			return null;
//		}catch (AppLogicException e){
//			log.error("download.AppLogicException:",e);
//			log.error("download {} {} [ES_LOG_EXCEPTION]",downRequest.getPath(),e.getMessage());
//			baseResponse.setMsg(e.getMessage());
//			baseResponse.setCode(e.getExceptionCode());
//			if(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode().equals(e.getExceptionCode())){
//				response.setStatus(RespCodeEnum.NOT_EXISTED.getCode());
//			}else{
//				response.setStatus(RespCodeEnum.FAILED.getCode());
//			}
//			return baseResponse;
//		}catch (Exception e){
//			log.error("download.Exception:",e);
//			log.error("download {} {} [ES_LOG_EXCEPTION]",downRequest.getPath(),e.getMessage());
//			baseResponse.setMsg(e.getMessage());
//			response.setStatus(RespCodeEnum.FAILED.getCode());
//			return baseResponse;
//		}
		
		return null;
	}
	
	@Override
	public BaseResponse<SysKeyDTO> info(SysKeyRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, SysKeyDTO.class,req -> {
			request.formatPathURI();
			
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"info");
	}
	
	@Override
	public BaseResponse<String> delete(@RequestBody SysKeyDeleteRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, String.class,req -> {
			request.formatPathURI();
			Boolean isPhy = request.getIsPhy() != null ? request.getIsPhy() : Boolean.FALSE;
			request.setIsPhy(isPhy);
			
			sysKeyManager.delete(request);
			return RespCodeEnum.SUCCESS.getCode();
		},"delete");
	}
}
