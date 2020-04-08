package com.zysl.cloud.aws.rule.service;

import com.zysl.cloud.aws.api.dto.FilePartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiCompleteRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiStartRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiUploadRequest;
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
	
	/**
	 * 启动分片上传，返回uploadId
	 * @description
	 * @author miaomingming
	 * @date 9:09 2020/4/8
	 * @param request
	 * @return java.lang.String
	 **/
	String multiUploadStart(SysFileMultiStartRequest request);
	
	/**
	 * 取消分片上传
	 * @description
	 * @author miaomingming
	 * @date 9:12 2020/4/8
	 * @param request
	 * @return void
	 **/
	void multiUploadAbort(SysFileMultiRequest request);
	
	/**
	 * 分片数据上传
	 * @description
	 * @author miaomingming
	 * @date 9:31 2020/4/8
	 * @param request
	 * @param bodys
	 * @return void
	 **/
	String multiUploadBodys(SysFileMultiUploadRequest request,byte[] bodys);
	
	/**
	 * 分片数据完成提交
	 * @description
	 * @author miaomingming
	 * @date 9:33 2020/4/8
	 * @param request
	 * @return void
	 **/
	void multiUploadComplete(SysFileMultiCompleteRequest request);
	
	/**
	 * 分片数据列表查询
	 * @description
	 * @author miaomingming
	 * @date 9:37 2020/4/8
	 * @param request
	 * @return com.zysl.cloud.aws.api.dto.FilePartInfoDTO
	 **/
	FilePartInfoDTO multiUploadInfo(SysFileMultiStartRequest request);
}
