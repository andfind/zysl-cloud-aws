package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.SysKeyRequest;
import com.zysl.cloud.aws.api.srv.SysFileSrv;
import com.zysl.cloud.aws.api.srv.SysKeySrv;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.validator.SysFileUploadRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyCreateRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SysKeyController extends BaseController implements SysKeySrv {
	
	@Autowired
	private ISysKeyManager sysKeyManager;
	
	@Override
	public BaseResponse<SysKeyDTO> create(HttpServletRequest httpServletRequest, SysKeyCreateRequest request){
		return ServiceProvider.call(request, SysKeyCreateRequestV.class, SysKeyDTO.class,req -> {
			byte[] bodys = HttpUtils.getBytesFromHttpRequest(httpServletRequest);
			
			boolean isOverWrite = request.getIsOverWrite() == null || request.getIsOverWrite() == 1 ? Boolean.TRUE : Boolean.FALSE;
			
			sysKeyManager.create(request,bodys,isOverWrite);
			
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"upload");
	}
}
