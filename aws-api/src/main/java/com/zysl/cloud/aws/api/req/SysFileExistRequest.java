package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统文件操作请求对象")
public class SysFileExistRequest extends BaseReqeust {
	
	@ApiModelProperty(value = "文件名", name = "fileName",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	private List<SysDirRequest> paths;
	@ApiModelProperty(value = "文件名", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;
	@ApiModelProperty(value = "版本号", name = "versionId",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String versionId;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileExistRequest\":{");
		if (paths != null) {
			sb.append("paths=").append(paths);
		}
		if (fileName != null) {
			sb.append(", fileName='").append(fileName).append('\'');
		}
		if (versionId != null) {
			sb.append(", versionId='").append(versionId).append('\'');
		}
		sb.append("},\"super-SysFileExistRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
