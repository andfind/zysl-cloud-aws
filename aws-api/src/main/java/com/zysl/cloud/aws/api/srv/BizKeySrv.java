package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.BizFileOfficeToPdfRequest;
import com.zysl.cloud.aws.api.req.BizFileShareRequest;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.aws.api.req.key.BizKeyOfficeToPdfRequest;
import com.zysl.cloud.aws.api.req.key.BizKeyShareRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDownloadRequest;
import com.zysl.cloud.utils.common.BaseResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/biz/key")
public interface BizKeySrv {
	
	
	/**
	 * 文件分享
	 * @description
	 * @author miaomingming
	 * @date 9:32 2020/4/9
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysFileDTO>
	 **/
	@PostMapping("/share")
	BaseResponse<SysKeyDTO> shareFile(@RequestBody BizKeyShareRequest request);
	
	/**
	 * 分享文件下载
	 * @description
	 * @author miaomingming
	 * @date 17:31 2020/6/11
	 * @param request
	 * @param response
	 * @param downloadRequest
	 * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
	 **/
	@GetMapping("/shareDownload")
	BaseResponse<String> shareFileDownload(HttpServletRequest request, HttpServletResponse response,
		SysKeyDownloadRequest downloadRequest);
	
	
	/**
	 * office转pdf
	 * @description
	 * @author miaomingming
	 * @date 9:32 2020/4/9
	 * @param request
	 * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.SysFileDTO>
	 **/
	@PostMapping("/office2pdf")
	BaseResponse<SysKeyDTO> officeToPdf(@RequestBody BizKeyOfficeToPdfRequest request);
}
