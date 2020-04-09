package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统文件下载请求对象")
public class BizFileOfficeToPdfRequest extends SysFileRequest {
	
	
	@ApiModelProperty(value = "文字水印", name = "textMark", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String textMark;
	
	@ApiModelProperty(value = "用户密码", name = "userPwd", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String userPwd;
	
	@ApiModelProperty(value = "所有者密码", name = "ownerPwd", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String ownerPwd;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"BizFileOfficeToPdfRequest\":{");
		if (textMark != null) {
			sb.append("textMark='").append(textMark).append('\'');
		}
		if (userPwd != null) {
			sb.append(", userPwd='").append(userPwd).append('\'');
		}
		if (ownerPwd != null) {
			sb.append(", ownerPwd='").append(ownerPwd).append('\'');
		}
		sb.append("},\"super-BizFileOfficeToPdfRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
