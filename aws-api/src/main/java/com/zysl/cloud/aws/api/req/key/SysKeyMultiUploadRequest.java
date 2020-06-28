package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "上传文件流请求对象")
public class SysKeyMultiUploadRequest extends SysKeyUploadRequest {
	
	@ApiModelProperty(value = "起始分片编号", name = "partNumber",dataType = SwaggerConstants.DATA_TYPE_NUMBER)
	private Integer partNumber ;
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysKeyMultiUploadRequest\":{");
		if (partNumber != null) {
			sb.append("partNumber=").append(partNumber);
		}
		sb.append("},\"super-SysKeyMultiUploadRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return super.getEsLogMsg();
	}
}
