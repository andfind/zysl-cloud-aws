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
@ApiModel(description = "文件系统文件返回对象")
public class SysFileDTO implements Serializable {
	
	private static final long serialVersionUID = 2743498670283234269L;
	
	@ApiModelProperty(value = "是否文件：1是0目录", name = "isFile", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Integer isFile;
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	@ApiModelProperty(value = "文件名称", name = "fileName", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;
	@ApiModelProperty(value = "分区文件大小", name = "size", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
	private Long size;
	@ApiModelProperty(value = "最后修改时间", name = "lastModified", dataType = SwaggerConstants.DATA_TYPE_OBJECT)
	private Date lastModified;
	@ApiModelProperty(value = "文件内容md5", name = "contentMd5", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String contentMd5;
	@ApiModelProperty(value = "versionId", name = "versionId", dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String versionId;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileDTO\":{");
		if (isFile != null) {
			sb.append("isFile=").append(isFile);
		}
		if (path != null) {
			sb.append(", path='").append(path).append('\'');
		}
		if (fileName != null) {
			sb.append(", fileName='").append(fileName).append('\'');
		}
		if (size != null) {
			sb.append(", size=").append(size);
		}
		if (lastModified != null) {
			sb.append(", lastModified=").append(lastModified);
		}
		if (contentMd5 != null) {
			sb.append(", contentMd5='").append(contentMd5).append('\'');
		}
		if (versionId != null) {
			sb.append(", versionId='").append(versionId).append('\'');
		}
		sb.append("}}");
		return sb.toString();
	}
}