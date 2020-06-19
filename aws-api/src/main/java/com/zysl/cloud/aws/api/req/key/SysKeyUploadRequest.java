package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "上传文件流请求对象")
public class SysKeyUploadRequest extends SysKeyRequest {
	
	private static final long serialVersionUID = -7745824360969448935L;
	@ApiModelProperty(value = "是否覆盖", name = "isCover",dataType = SwaggerConstants.DATA_TYPE_BOOLEAN)
	private Boolean isCover = true;
	
	@ApiModelProperty(value = "下载用的文件名", name = "fileName",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysKeyUploadRequest\":{");
		if (isCover != null) {
			sb.append("isCover=").append(isCover);
		}
		if (fileName != null) {
			sb.append(", fileName='").append(fileName).append('\'');
		}
		sb.append("},\"super-SysKeyUploadRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return super.getEsLogMsg();
	}
}
