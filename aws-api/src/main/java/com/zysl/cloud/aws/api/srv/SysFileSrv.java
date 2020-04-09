package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.FilePartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.req.SysDirListRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiCompleteRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiStartRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiUploadRequest;
import com.zysl.cloud.aws.api.req.SysFileRenameRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.SysFileUploadRequest;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sys/file")
public interface SysFileSrv {
	
	
	/**
	 * 创建目录
	 * @description
	 * @author miaomingming
	 * @date 8:45 2020/4/7
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/mkdir")
	BaseResponse<String> mkdir(@RequestBody SysDirRequest request);
	
	/**
	 * 查询子列表
	 * @description
	 * @author miaomingming
	 * @date 15:15 2020/4/4
	 * @param request
	 * @return
	 **/
	@PostMapping("/list")
	BasePaginationResponse<SysFileDTO> list(@RequestBody SysDirListRequest request);
	
	/**
	 * 复制
	 * @description
	 * @author miaomingming
	 * @date 15:15 2020/4/4
	 * @param request
	 * @return
	 **/
	@PostMapping("/copy")
	BaseResponse<String> copy(@RequestBody SysFileRenameRequest request);
	
	
	/**
	 * 移动(重命名)
	 * @description
	 * @author miaomingming
	 * @date 15:15 2020/4/4
	 * @param request
	 * @return
	 **/
	@PostMapping("/move")
	BaseResponse<String> move(@RequestBody SysFileRenameRequest request);
	
	
	/**
	 * 删除
	 * @description
	 * @author miaomingming
	 * @date 15:12 2020/4/4
	 * @param request
	 * @return
	 **/
	@PostMapping("/delete")
	BaseResponse<String> delete(@RequestBody SysFileRequest request);
	
	/**
	 * 文件信息查询
	 * @description
	 * @author miaomingming
	 * @date 15:42 2020/4/4
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysFileDTO>
	 **/
	@PostMapping("/info")
	BaseResponse<SysFileDTO> info(@RequestBody SysFileRequest request);
	
	
	/**
	 * 上传
	 * @description
	 * @author miaomingming
	 * @date 15:13 2020/4/4
	 * @param request
	 * @return
	 **/
	@PostMapping("/upload")
	BaseResponse<SysFileDTO> upload(HttpServletRequest req, SysFileUploadRequest request);
	
	/**
	 * 下载,支持分片下载
	 * @description
	 * @author miaomingming
	 * @date 15:45 2020/4/4
	 * @param req
	 * @param rsp
	 * @param request
	 * @return void
	 **/
	@GetMapping("/download")
	BaseResponse<String> download(HttpServletRequest req, HttpServletResponse rsp, SysFileDownloadRequest request);
	
	/**
	 * 分片上传-启动
	 * @description
	 * @author miaomingming
	 * @date 15:56 2020/4/4
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/ms")
	BaseResponse<String> multiUploadStart(@RequestBody SysFileMultiStartRequest request);
	
	/**
	 * 分片上传-传输
	 * @description
	 * @author miaomingming
	 * @date 15:56 2020/4/4
	 * @param req
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/mu")
	BaseResponse<String> multiUploadData(HttpServletRequest req, SysFileMultiUploadRequest request);
	
	/**
	 * 分片上传-完成
	 * @description
	 * @author miaomingming
	 * @date 15:56 2020/4/4
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/mc")
	BaseResponse<String> multiUploadComplete(@RequestBody SysFileMultiCompleteRequest request);
	
	/**
	 * 分片上传-取消
	 * @description
	 * @author miaomingming
	 * @date 15:56 2020/4/4
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@PostMapping("/ma")
	BaseResponse<String> multiUploadAbort(@RequestBody SysFileMultiRequest request);
	
	/**
	 * 分片上传信息查询
	 * @description
	 * @author miaomingming
	 * @date 15:59 2020/4/4
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.FilePartInfoDTO>
	 **/
	@PostMapping("/mq")
	BaseResponse<FilePartInfoDTO> multiUploadInfoQuery(@RequestBody SysFileMultiStartRequest request);
	
	
	
	/**
	 * 查询文件版本列表
	 * @description 
	 * @author miaomingming
	 * @date 17:02 2020/4/7 
	 * @param request
	 * @return com.zysl.cloud.utils.common.BasePaginationResponse<com.zysl.cloud.aws.api.req.SysFileRequest>
	 **/
	@PostMapping("/versions")
	BasePaginationResponse<SysFileDTO> listVersions(@RequestBody SysFileListRequest request);
	
}
