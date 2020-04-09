package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统文件下载请求对象")
public class BizFileShareRequest extends SysFileRequest {
	
	@ApiModelProperty(value = "文件id，判断下载权限", name = "userId",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String userId;
	@ApiModelProperty(value = "最大下载次数", name = "maxDownloadAmout",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer maxDownloadAmout;
	@ApiModelProperty(value = "最大有效时长(小时)", name = "maxHours",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer maxHours;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"BizFileShareRequest\":{");
		if (userId != null) {
			sb.append("userId='").append(userId).append('\'');
		}
		if (maxDownloadAmout != null) {
			sb.append(", maxDownloadAmout=").append(maxDownloadAmout);
		}
		if (maxHours != null) {
			sb.append(", maxHours=").append(maxHours);
		}
		sb.append("},\"super-BizFileShareRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
