package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.SysFileUploadRequest;
import com.zysl.cloud.aws.api.req.SysKeyCreateRequest;
import com.zysl.cloud.utils.common.BaseResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sys/key")
public interface SysKeySrv {
	
	@PostMapping("/create")
	BaseResponse<SysKeyDTO> create(HttpServletRequest req, SysKeyCreateRequest request);
}
