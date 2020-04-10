package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统分片文件上传操作请求对象")
public class SysFileMultiUploadRequest extends SysFileMultiStartRequest {
	
	private static final long serialVersionUID = 1490875657829372134L;
	@ApiModelProperty(value = "分片文件ID", name = "uploadId",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String uploadId;
	@ApiModelProperty(value = "分片编号", name = "partNumber",required = true,dataType = SwaggerConstants.DATA_TYPE_NUMBER)
	private Integer partNumber;
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileMultiRequest\":{");
		if (uploadId != null) {
			sb.append("uploadId='").append(uploadId).append('\'');
		}
		sb.append("},\"super-SysFileMultiRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		return StringUtils.join(this.getPath(),this.getFileName(),"->",this.partNumber);
	}
}
