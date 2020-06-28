package com.zysl.cloud.aws.rule.service;

import com.zysl.cloud.aws.api.dto.PartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.dto.SysKeyFileDTO;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiCompleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteListRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyMultiUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.utils.common.MyPage;
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
	List<SysKeyFileDTO> infoList(SysKeyRequest request, MyPage myPage);
	
	/**
	 * 版本列表查询
	 * @description
	 * @author miaomingming
	 * @date 16:27 2020/6/17
	 * @param request
	 * @return java.util.List<com.zysl.cloud.aws.api.dto.SysKeyDTO>
	 **/
	List<SysKeyDTO> versionList(SysKeyRequest request, MyPage myPage);
	
	/**
	 * 删除对象
	 * @description
	 * @author miaomingming
	 * @date 11:39 2020/6/17
	 * @param request
	 * @return void
	 **/
	void delete(SysKeyDeleteRequest request);
	
	
	/**
	 * 批量删除
	 * @description
	 * @author miaomingming
	 * @date 11:38 2020/6/22
	 * @param request
	 * @return void
	 **/
	void deleteList(SysKeyDeleteListRequest request);
	
	/**
	 * 查询范围内对象数据流
	 * @description
	 * @author miaomingming
	 * @date 9:43 2020/6/22
	 * @param request
	 * @param range
	 * @return byte[]
	 **/
	byte[] getBody(SysKeyRequest request,String range);
	
	/**
	 * 标签列表查询
	 * @description
	 * @author miaomingming
	 * @date 10:06 2020/6/22
	 * @param request
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	List<TagBO> tagList(SysKeyRequest request);
	
	
	
	/**
	 * 复制对象
	 * @description
	 * @author miaomingming
	 * @date 15:18 2020/6/22
	 * @param source
	 * @param target
	 * @param isCover
	 * @return void
	 **/
	void copy(SysKeyRequest source,SysKeyRequest target,Boolean isCover);
	
	/**
	 * 取消分片上传对象
	 * @description
	 * @author miaomingming
	 * @date 11:35 2020/6/28
	 * @param request
	 * @return void
	 **/
	void multiAbort(SysKeyRequest request);
	
	/**
	 * 分片列表查询
	 * @description
	 * @author miaomingming
	 * @date 11:40 2020/6/28
	 * @param request
	 * @return java.util.List<com.zysl.cloud.aws.api.dto.PartInfoDTO>
	 **/
	List<PartInfoDTO> multiList(SysKeyRequest request);
	
	/**
	 * 分片上传
	 * 不存在则创建分片对象
	 * 上传完成后自动提交
	 * @description
	 * @author miaomingming
	 * @date 11:47 2020/6/28
	 * @param request
	 * @return java.lang.Boolean  是否完成
	 **/
	Boolean multiUpload(SysKeyMultiUploadRequest request,byte[] bodys,Long contentLength);
	
}
