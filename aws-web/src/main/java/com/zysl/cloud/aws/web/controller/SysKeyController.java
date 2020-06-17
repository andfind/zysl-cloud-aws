package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.SysKeyRequest;
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
	public BaseResponse<SysKeyDTO> create(HttpServletRequest httpServletRequest, SysKeyCreateRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, SysKeyDTO.class,req -> {
			byte[] bodys = HttpUtils.getBytesFromHttpRequest(httpServletRequest);
			
			boolean isOverWrite = request.getIsOverWrite() == null || request.getIsOverWrite() == 1 ? Boolean.TRUE : Boolean.FALSE;
			
			sysKeyManager.create(request,bodys,isOverWrite);
			
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"upload");
	}
	
	@Override
	public BaseResponse<String> delete(@RequestBody SysKeyDeleteRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, String.class,req -> {
			SysKeyRequest sysKeyRequest = BeanCopyUtil.copy(request,SysKeyRequest.class);
			Boolean isPhy = request.getIsPhy() != null ? request.getIsPhy() : Boolean.FALSE;
			sysKeyManager.delete(sysKeyRequest,isPhy);
			return RespCodeEnum.SUCCESS.getCode();
		},"delete");
	}
}
