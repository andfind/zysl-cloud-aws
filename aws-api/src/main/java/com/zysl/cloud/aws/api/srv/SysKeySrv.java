package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.SysFileUploadRequest;
import com.zysl.cloud.aws.api.req.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.SysKeyRequest;
import com.zysl.cloud.utils.common.BaseResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sys/key")
public interface SysKeySrv {
	/**
	 * 创建对象
	 * @description
	 * @author miaomingming
	 * @date 11:46 2020/6/17
	 * @param req
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	@PostMapping("/create")
	BaseResponse<SysKeyDTO> create(HttpServletRequest req, SysKeyCreateRequest request);

	/**
	 * 删除对象
	 * @description
	 * @author miaomingming
	 * @date 11:46 2020/6/17
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/delete")
	BaseResponse<String> delete(@RequestBody SysKeyDeleteRequest request);
}
