package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.srv.SysKeySrv;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.validator.SysKeyRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SysKeyController extends BaseController implements SysKeySrv {
	
	@Autowired
	private ISysKeyManager sysKeyManager;
	
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
			
			byte[] bodys = HttpUtils.getBytesFromHttpRequest(httpServletRequest);
			
			sysKeyManager.upload(request,bodys);
			
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"upload");
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
