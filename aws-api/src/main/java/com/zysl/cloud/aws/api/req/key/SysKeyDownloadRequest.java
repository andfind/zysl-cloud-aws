package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统文件下载请求对象")
public class SysKeyDownloadRequest extends SysKeyRequest {
	
	@ApiModelProperty(value = "文件id，判断下载权限", name = "userId",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String userId;
	
	@ApiModelProperty(value = "文件类型(据此设置返回的contentType)：0普通1视频，默认0", name = "userId",dataType = SwaggerConstants.DATA_TYPE_NUMBER)
	private Integer type;
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysKeyDownloadRequest\":{");
		if (userId != null) {
			sb.append("userId='").append(userId).append('\'');
		}
		if (type != null) {
			sb.append(", type=").append(type);
		}
		sb.append("},\"super-SysKeyDownloadRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return super.getEsLogMsg();
	}
}
