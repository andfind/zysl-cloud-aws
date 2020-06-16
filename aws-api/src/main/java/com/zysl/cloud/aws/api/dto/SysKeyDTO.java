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
@ApiModel(description = "文件系统对象返回对象")
public class SysKeyDTO implements Serializable {
	
	private static final long serialVersionUID = 5855827655850640540L;
	@ApiModelProperty(value = "是否文件：1是0目录", name = "isFile", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer isFile;
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	@ApiModelProperty(value = "分区文件大小", name = "size", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Long size;
	@ApiModelProperty(value = "最后修改时间", name = "lastModified", dataType = SwaggerConstants.DATA_TYPE_OBJECT)
	private Date lastModified;
	@ApiModelProperty(value = "versionId", name = "versionId", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String versionId;
	@ApiModelProperty(value = "versionNo", name = "versionNo", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer versionNo;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysKeyDTO\":{");
		if (isFile != null) {
			sb.append("isFile=").append(isFile);
		}
		if (path != null) {
			sb.append(", path='").append(path).append('\'');
		}
		if (size != null) {
			sb.append(", size=").append(size);
		}
		if (lastModified != null) {
			sb.append(", lastModified=").append(lastModified);
		}
		if (versionId != null) {
			sb.append(", versionId='").append(versionId).append('\'');
		}
		if (versionNo != null) {
			sb.append(", versionNo=").append(versionNo);
		}
		sb.append("}}");
		return sb.toString();
	}
}
