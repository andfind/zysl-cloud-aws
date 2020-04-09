package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.req.BizFileShareRequest;
import com.zysl.cloud.aws.api.req.DownloadFileRequest;
import com.zysl.cloud.aws.api.req.GetVideoRequest;
import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.utils.common.BaseResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/biz/file")
public interface BizFileSrv {
	
	/**
	 * 文件是否存在
	 * @description
	 * @author miaomingming
	 * @date 17:01 2020/4/7
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.Boolean>
	 **/
	@PostMapping("/exist")
	BaseResponse<Boolean> isExist(@RequestBody SysFileExistRequest request);
	
	/**
	 * 文件分享
	 * @description
	 * @author miaomingming
	 * @date 9:32 2020/4/9
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysFileDTO>
	 **/
	@PostMapping("/share")
	BaseResponse<SysFileDTO> shareFile(@RequestBody BizFileShareRequest request);
	
	/**
	 * 分享文件下载
	 * @description
	 * @author miaomingming
	 * @date 9:46 2020/4/9
	 * @param response
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@GetMapping("/shareDownload")
	BaseResponse<String> shareFileDownload(HttpServletRequest request,HttpServletResponse response, SysFileDownloadRequest downloadRequest);
	
	/**
	 * 视频文件下载
	 * @description
	 * @author miaomingming
	 * @date 10:11 2020/4/9
	 * @param request
	 * @param response
	 * @param downloadRequest
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@GetMapping("/videoDownload")
	BaseResponse<String> vedioFileDownload(HttpServletRequest request,HttpServletResponse response, SysFileDownloadRequest downloadRequest);
}
