package com.zysl.cloud.aws.rule.service;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

public interface ISysFileManager {
	

	/**
	 * 复制
	 * @description
	 * @author miaomingming
	 * @date 11:37 2020/4/7
	 * @param source
	 * @param target
	 * @param isOverWrite
	 * @return void
	 **/
	void copy(SysFileRequest source,SysFileRequest target,Boolean isOverWrite);
	
	/**
	 * 移动
	 * @description
	 * @author miaomingming
	 * @date 11:38 2020/4/7
	 * @param source
	 * @param target
	 * @return void
	 **/
	void move(SysFileRequest source,SysFileRequest target);
	
	/**
	 * 删除
	 * @description
	 * @author miaomingming
	 * @date 11:39 2020/4/7
	 * @param request
	 * @return void
	 **/
	void delete(SysFileRequest request);
	
	/**
	 * 查询文件信息
	 * @description
	 * @author miaomingming
	 * @date 14:47 2020/4/7
	 * @param request
	 * @return com.zysl.cloud.aws.api.dto.SysFileDTO
	 **/
	SysFileDTO info(SysFileRequest request);
	
	/**
	 * 数据上传
	 * @description
	 * @author miaomingming
	 * @date 15:14 2020/4/7
	 * @param request
	 * @param bodys
	 * @return void
	 **/
	void upload(SysFileRequest request,byte[] bodys,Boolean isOverWrite);
	
	/**
	 * 获取文件对象数据
	 * @description
	 * @author miaomingming
	 * @date 15:44 2020/4/7
	 * @param request
	 * @param range
	 * @return byte[]
	 **/
	byte[] getFileBodys(SysFileRequest request,String range);
	
	/**
	 * 文件版本列表查询
	 * @description
	 * @author miaomingming
	 * @date 17:20 2020/4/7
	 * @param request
	 * @return java.util.List<com.zysl.cloud.aws.api.dto.SysFileDTO>
	 **/
	List<SysFileDTO> listVersions(SysFileListRequest request);
}
