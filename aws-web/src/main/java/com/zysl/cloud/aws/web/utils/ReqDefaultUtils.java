package com.zysl.cloud.aws.web.utils;

import com.zysl.cloud.aws.api.req.SysDirListRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiStartRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReqDefaultUtils {
	
	@Autowired
	private WebConfig webConfig;
	
	public void setFileSystemDefault(SysDirRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	
	public void setFileSystemDefault(SysFileRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	
	public void setFileSystemDefault(SysFileListRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	
	public void setFileSystemDefault(SysFileMultiStartRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	
	public void setFileSystemDefault(SysDirListRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
		
	}
	
}
