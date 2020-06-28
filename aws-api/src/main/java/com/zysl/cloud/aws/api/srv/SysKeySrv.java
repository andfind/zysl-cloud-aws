package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.PartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.dto.SysKeyFileDTO;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyCopyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteListRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyExistRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyMultiUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyPageRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.common.BasePaginationResponse;
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
	 * 下载
	 * @description
	 * @author miaomingming
	 * @date 18:31 2020/6/19
	 * @param req
	 * @param rsp
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@GetMapping("/download")
	BaseResponse<String> download(HttpServletRequest req, HttpServletResponse rsp, SysKeyDownloadRequest request);
	
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
	
	/**
	 * 批量删除对象
	 * @description
	 * @author miaomingming
	 * @date 11:11 2020/6/22
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/deleteList")
	BaseResponse<String> deleteList(@RequestBody SysKeyDeleteListRequest request);
	
	/**
	 * 查询对象列表
	 * @description
	 * @author miaomingming
	 * @date 11:11 2020/6/22
	 * @param request
	 * @return com.zysl.cloud.utils.common.BasePaginationResponse<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	@PostMapping("/list")
	BasePaginationResponse<SysKeyFileDTO> infoList(@RequestBody SysKeyPageRequest request);
	
	/**
	 * 查询版本列表
	 * @description
	 * @author miaomingming
	 * @date 11:12 2020/6/22
	 * @param request
	 * @return com.zysl.cloud.utils.common.BasePaginationResponse<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	@PostMapping("/versions")
	BasePaginationResponse<SysKeyDTO> versionList(@RequestBody SysKeyPageRequest request);
	
	/**
	 * 复制对象
	 * @description
	 * @author miaomingming
	 * @date 11:13 2020/6/22
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/copy")
	BaseResponse<String> copy(@RequestBody SysKeyCopyRequest request);
	
	
	/**
	 * 对象是否存在
	 * 默认在announce及announce02查找
	 * @description
	 * @author miaomingming
	 * @date 14:21 2020/6/23
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.Boolean>
	 **/
	@PostMapping("/exist")
	BaseResponse<Boolean> isExist(@RequestBody SysKeyExistRequest request);
	
	/**
	 * 分片上传
	 * @description
	 * @author miaomingming
	 * @date 11:17 2020/6/28
	 * @param req
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/multiUpload")
	BaseResponse<SysKeyDTO> multiUpload(HttpServletRequest req, SysKeyMultiUploadRequest request);
	
	/**
	 * 分片列表查询
	 * @description
	 * @author miaomingming
	 * @date 11:28 2020/6/28
	 * @param request
	 * @return com.zysl.cloud.utils.common.BasePaginationResponse<com.zysl.cloud.aws.api.dto.PartInfoDTO>
	 **/
	@PostMapping("/multiList")
	BasePaginationResponse<PartInfoDTO> multiList(@RequestBody SysKeyPageRequest request);
	
	/**
	 * 取消分片
	 * @description
	 * @author miaomingming
	 * @date 11:28 2020/6/28
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.Boolean>
	 **/
	@PostMapping("/multiAbort")
	BaseResponse<Boolean> multiAbort(@RequestBody SysKeyRequest request);
}
