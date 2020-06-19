package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "创建文件请求对象")
public class SysKeyCreateRequest extends SysKeyRequest {
	
	private static final long serialVersionUID = 7761690283840948077L;
	@ApiModelProperty(value = "文件流base64", name = "data",dataType = SwaggerConstants.DATA_TYPE_NUMBER)
	private String data;
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysKeyCreateRequest\":{");
		if (data != null) {
			sb.append("data='").append(data).append('\'');
		}
		sb.append("},\"super-SysKeyCreateRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return super.getEsLogMsg();
	}
}
