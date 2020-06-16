package com.zysl.cloud.aws.rule.service;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.SysKeyRequest;

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
}
