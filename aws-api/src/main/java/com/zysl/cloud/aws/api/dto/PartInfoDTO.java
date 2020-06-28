package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "分区上传记录对象")
public class PartInfoDTO implements Serializable {
	
	
	private static final long serialVersionUID = 7053195640073584735L;
	@ApiModelProperty(value = "分区上传次数", name = "partNumber", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer partNumber;
	@ApiModelProperty(value = "分区文件内容MD5", name = "eTag", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String eTag;
	@ApiModelProperty(value = "分区文件大小", name = "size", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer size;
	@ApiModelProperty(value = "最后修改时间", name = "lastModified", dataType = SwaggerConstants.DATA_TYPE_OBJECT)
	private Date lastModified;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"PartInfoDTO\":{");
		if (partNumber != null) {
			sb.append("partNumber=").append(partNumber);
		}
		if (eTag != null) {
			sb.append(", eTag='").append(eTag).append('\'');
		}
		if (size != null) {
			sb.append(", size=").append(size);
		}
		if (lastModified != null) {
			sb.append(", lastModified=").append(lastModified);
		}
		sb.append("}}");
		return sb.toString();
	}
}
