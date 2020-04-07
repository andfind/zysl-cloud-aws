package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统分片文件操作请求对象")
public class SysFileMultiRequest extends BaseReqeust {
	
	private static final long serialVersionUID = -796859476482005238L;
	@ApiModelProperty(value = "路径所在类型：s3/local/ftp", name = "type",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String type;
	
	@ApiModelProperty(value = "路径所在服务器编号", name = "serverNo",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String serverNo;
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	@ApiModelProperty(value = "文件名", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;
	@ApiModelProperty(value = "分片文件ID", name = "uploadId",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String uploadId;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileMultiRequest\":{");
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
		if (uploadId != null) {
			sb.append(", uploadId='").append(uploadId).append('\'');
		}
		sb.append("},\"super-SysFileMultiRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
