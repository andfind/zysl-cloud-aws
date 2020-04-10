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
@ApiModel(description = "文件系统文件操作请求对象")
public class SysFileRenameRequest extends BaseReqeust {
	
	private static final long serialVersionUID = 4750285446159417885L;
	@ApiModelProperty(value = "源路径", name = "source",required = true,dataType = SwaggerConstants.DATA_TYPE_OBJECT)
	private SysFileRequest source;
	
	@ApiModelProperty(value = "目的路径", name = "target",required = true,dataType = SwaggerConstants.DATA_TYPE_OBJECT)
	private SysFileRequest target;
	
	@ApiModelProperty(value = "是否覆盖：1是0否", name = "isOverWrite",required = true,dataType = SwaggerConstants.DATA_TYPE_NUMBER)
	private Integer isOverWrite;
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileRenameRequest\":{");
		if (source != null) {
			sb.append("source=").append(source);
		}
		if (target != null) {
			sb.append(", target=").append(target);
		}
		if (isOverWrite != null) {
			sb.append(", isOverWrite=").append(isOverWrite);
		}
		sb.append("},\"super-SysFileRenameRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		StringBuffer sb = new StringBuffer(64);
		if(source != null){
			sb.append(StringUtils.join(source.getPath(),source.getFileName()))
				.append(" ");
		}
		if(target != null){
			sb.append(StringUtils.join(target.getPath(),target.getFileName()));
		}
		
		return sb.toString();
	}
}
