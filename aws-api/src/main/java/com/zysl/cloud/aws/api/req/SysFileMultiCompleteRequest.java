package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.aws.api.dto.PartInfoDTO;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "文件系统分片文件操作请求对象")
public class SysFileMultiCompleteRequest extends SysFileMultiStartRequest {
	
	private static final long serialVersionUID = 7702184886928445103L;
	@ApiModelProperty(value = "分片文件ID", name = "uploadId",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String uploadId;
	@ApiModelProperty(value = "分片上传数据", name = "eTagList", dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	private List<PartInfoDTO> eTagList;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileMultiCompleteRequest\":{");
		if (uploadId != null) {
			sb.append("uploadId='").append(uploadId).append('\'');
		}
		if (eTagList != null) {
			sb.append(", eTagList=").append(eTagList);
		}
		sb.append("},\"super-SysFileMultiCompleteRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
