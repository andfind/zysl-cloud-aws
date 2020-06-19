package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.utils.common.BaseResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sys/key")
public interface SysKeySrv {
	/**
	 * 创建对象
	 * 上传base64数据字符串
	 * @description
	 * @author miaomingming
	 * @date 11:46 2020/6/17
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	@PostMapping("/create")
	BaseResponse<SysKeyDTO> create(@RequestBody SysKeyCreateRequest request);
	
	/**
	 * 上传文件流
	 * @description
	 * @author miaomingming
	 * @date 11:45 2020/6/19
	 * @param req
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	@PostMapping("/upload")
	BaseResponse<SysKeyDTO> upload(HttpServletRequest req, SysKeyUploadRequest request);
	
	/**
	 * 查询信息
	 * @description
	 * @author miaomingming
	 * @date 16:53 2020/6/19
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	@PostMapping("/info")
	BaseResponse<SysKeyDTO> info(@RequestBody SysKeyRequest request);
	
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
