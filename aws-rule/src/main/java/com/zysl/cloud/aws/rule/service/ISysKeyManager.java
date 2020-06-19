package com.zysl.cloud.aws.rule.service;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import java.util.List;

public interface ISysKeyManager {
	/**
	 * 创建对象
	 * @description
	 * @author miaomingming
	 * @param request
	 * @return void
	 **/
	void create(SysKeyCreateRequest request);
	
	/**
	 * 上传文件流
	 * @description
	 * @author miaomingming
	 * @date 15:11 2020/6/19
	 * @param request
	 * @param bodys
	 * @return void
	 **/
	void upload(SysKeyUploadRequest request,byte[] bodys);
	
	/**
	 * 对象信息查询
	 * @description
	 * @author miaomingming
	 * @date 16:11 2020/6/16
	 * @param request
	 * @return com.zysl.cloud.aws.api.dto.SysKeyDTO
	 **/
	SysKeyDTO info(SysKeyRequest request);
	
	/**
	 * 对象列表查询
	 * @description
	 * @author miaomingming
	 * @date 16:27 2020/6/17
	 * @param request
	 * @return java.util.List<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	List<SysKeyDTO> keyList(SysKeyRequest request);
	
	/**
	 * 版本列表查询
	 * @description
	 * @author miaomingming
	 * @date 16:27 2020/6/17
	 * @param request
	 * @return java.util.List<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	List<SysKeyDTO> versionList(SysKeyRequest request);
	
	/**
	 * 删除对象
	 * @description
	 * @author miaomingming
	 * @date 11:39 2020/6/17
	 * @param request
	 * @return void
	 **/
	void delete(SysKeyDeleteRequest request);
}
