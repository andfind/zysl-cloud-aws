package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "对象分享操作请求对象")
public class BizKeyShareRequest extends SysKeyRequest {
	
	@ApiModelProperty(value = "文件id，判断下载权限", name = "userId",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String userId;
	@ApiModelProperty(value = "最大下载次数", name = "maxDownloadAmout",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer maxDownloadAmout;
	@ApiModelProperty(value = "最大有效时长(小时)", name = "maxHours",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer maxHours;
	@ApiModelProperty(value = "分享的文件名", name = "shareName",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String shareName;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"BizKeyShareRequest\":{");
		if (userId != null) {
			sb.append("userId='").append(userId).append('\'');
		}
		if (maxDownloadAmout != null) {
			sb.append(", maxDownloadAmout=").append(maxDownloadAmout);
		}
		if (maxHours != null) {
			sb.append(", maxHours=").append(maxHours);
		}
		if (shareName != null) {
			sb.append(", shareName='").append(shareName).append('\'');
		}
		sb.append("},\"super-BizKeyShareRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return super.getEsLogMsg();
	}
}
