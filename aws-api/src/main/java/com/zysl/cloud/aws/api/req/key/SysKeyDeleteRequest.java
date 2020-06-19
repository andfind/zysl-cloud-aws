package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "创建操作请求对象")
public class SysKeyDeleteRequest extends SysKeyRequest {
	
	
	private static final long serialVersionUID = 2438046431206803286L;
	@ApiModelProperty(value = "是否物理删除", name = "isPhy",required = true,dataType = SwaggerConstants.DATA_TYPE_BOOLEAN)
	private Boolean isPhy;
	
	
	@Override
	public String getEsLogMsg() {
		return super.getEsLogMsg();
	}
}
