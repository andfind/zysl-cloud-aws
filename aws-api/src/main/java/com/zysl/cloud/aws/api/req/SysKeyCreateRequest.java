package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "创建文件请求对象")
public class SysKeyCreateRequest extends SysKeyRequest {
	
	private static final long serialVersionUID = -7745824360969448935L;
	@ApiModelProperty(value = "是否覆盖：1是0否,默认0", name = "isOverWrite",dataType = SwaggerConstants.DATA_TYPE_NUMBER)
	private Integer isOverWrite;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysKeyCreateRequest\":{");
		if (isOverWrite != null) {
			sb.append("isOverWrite=").append(isOverWrite);
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
