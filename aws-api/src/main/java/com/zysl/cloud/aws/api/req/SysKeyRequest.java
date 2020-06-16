package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "创建操作请求对象")
public class SysKeyRequest extends BaseReqeust {
	
	private static final long serialVersionUID = -4110290877043549213L;
	
	//格式：  scheme://编号或IP/完整路径#版本号
	//scheme枚举     |      完整路径
	//   s3          |     bucket/key
	//   ftp         |     目录/文件
	//   sharepoint  |     目录/文件
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	
}
