package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统目录重命名请求对象")
public class SysDirRenameRequest extends BaseReqeust {
	
	private static final long serialVersionUID = 6107027209830944703L;
	@ApiModelProperty(value = "源路径", name = "srcPath",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String srcPath;
	@ApiModelProperty(value = "目的路径", name = "destPath",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String destPath;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysDirRenameRequest\":{");
		if (srcPath != null) {
			sb.append("srcPath='").append(srcPath).append('\'');
		}
		if (destPath != null) {
			sb.append(", destPath='").append(destPath).append('\'');
		}
		sb.append("},\"super-SysDirRenameRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return StringUtils.join(this.srcPath, " ", this.destPath);
	}
}
