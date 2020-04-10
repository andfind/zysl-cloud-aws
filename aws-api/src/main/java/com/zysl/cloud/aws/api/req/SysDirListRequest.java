package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统目录列表查询请求对象")
public class SysDirListRequest extends BasePaginationRequest {
	
	private static final long serialVersionUID = 8535428496837409770L;
	@ApiModelProperty(value = "路径所在类型：s3/local/ftp", name = "type",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String type;
	
	@ApiModelProperty(value = "路径所在服务器编号", name = "serverNo",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String serverNo;
	
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysDirListRequest\":{");
		if (type != null) {
			sb.append("type='").append(type).append('\'');
		}
		if (serverNo != null) {
			sb.append(", serverNo='").append(serverNo).append('\'');
		}
		if (path != null) {
			sb.append(", path='").append(path).append('\'');
		}
		sb.append("},\"super-SysDirListRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg(){
		return this.path;
	}
}
