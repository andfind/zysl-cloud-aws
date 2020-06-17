package com.zysl.cloud.aws.rule.service;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.SysKeyRequest;
import java.util.List;

public interface ISysKeyManager {
	/**
	 * 创建对象
	 * @description
	 * @author miaomingming
	 * @param request
	 * @param bodys
	 * @param isOverWrite 是否覆盖
	 * @return void
	 **/
	void create(SysKeyRequest request,byte[] bodys,Boolean isOverWrite);
	
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
	 * @param isPhy
	 * @return void
	 **/
	void delete(SysKeyRequest request,Boolean isPhy);
}
