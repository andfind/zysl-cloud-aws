package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统文件操作请求对象")
public class SysFileRequest extends BaseReqeust {
	
	private static final long serialVersionUID = -8429291212825076219L;
	@ApiModelProperty(value = "路径所在类型：s3/local/ftp", name = "type",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String type;
	
	@ApiModelProperty(value = "路径所在服务器编号", name = "serverNo",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String serverNo;
	
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	@ApiModelProperty(value = "文件名", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;
	@ApiModelProperty(value = "版本号", name = "versionId",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String versionId;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileRequest\":{");
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
		if (versionId != null) {
			sb.append(", versionId='").append(versionId).append('\'');
		}
		sb.append("},\"super-SysFileRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
