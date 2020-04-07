package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统目录操作请求对象")
public class SysDirRequest extends BaseReqeust {
	
	private static final long serialVersionUID = 1501500542319778448L;
	
	@ApiModelProperty(value = "路径所在类型：s3/local/ftp", name = "type",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String type;
	
	@ApiModelProperty(value = "路径所在服务器编号", name = "serverNo",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String serverNo;
	
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysDirRequest\":{");
		if (type != null) {
			sb.append("type='").append(type).append('\'');
		}
		if (serverNo != null) {
			sb.append(", serverNo='").append(serverNo).append('\'');
		}
		if (path != null) {
			sb.append(", path='").append(path).append('\'');
		}
		sb.append("},\"super-SysDirRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
