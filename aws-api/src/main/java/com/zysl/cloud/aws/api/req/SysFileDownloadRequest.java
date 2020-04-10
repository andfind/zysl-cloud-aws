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
@ApiModel(description = "文件系统文件下载请求对象")
public class SysFileDownloadRequest extends SysFileRequest {
	
	@ApiModelProperty(value = "文件id，判断下载权限", name = "userId",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String userId;
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileDownloadRequest\":{");
		if (userId != null) {
			sb.append("userId='").append(userId).append('\'');
		}
		sb.append("},\"super-SysFileDownloadRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return StringUtils.join(this.getPath(),this.getFileName(),":",this.getVersionId());
	}
}
