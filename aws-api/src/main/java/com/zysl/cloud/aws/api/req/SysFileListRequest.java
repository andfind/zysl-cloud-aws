package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件版本列表查询")
public class SysFileListRequest extends BasePaginationRequest {
	
	
	private static final long serialVersionUID = -5183865353901373725L;
	@ApiModelProperty(value = "路径所在类型：s3/local/ftp", name = "type",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String type;
	
	@ApiModelProperty(value = "路径所在服务器编号", name = "serverNo",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String serverNo;
	
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	@ApiModelProperty(value = "文件名", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileListRequest\":{");
		if (type != null) {
			sb.append("type='").append(type).append('\'');
		}
		if (serverNo != null) {
			sb.append(", serverNo='").append(serverNo).append('\'');
		}
		if (path != null) {
			sb.append(", path='").append(path).append('\'');
		}
		if (fileName != null) {
			sb.append(", fileName='").append(fileName).append('\'');
		}
		sb.append("},\"super-SysFileListRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
