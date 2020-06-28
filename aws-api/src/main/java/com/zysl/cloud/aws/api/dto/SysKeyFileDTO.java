package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统对象返回对象")
public class SysKeyFileDTO extends SysKeyDTO {
	
	private static final long serialVersionUID = 5855827655850640540L;
	@ApiModelProperty(value = "是否文件", name = "isFile", dataType = SwaggerConstants.DATA_TYPE_BOOLEAN)
	private Boolean isFile = Boolean.FALSE;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysKeyFileDTO\":{");
		if (isFile != null) {
			sb.append("isFile=").append(isFile);
		}
		sb.append("},\"super-SysKeyFileDTO\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
