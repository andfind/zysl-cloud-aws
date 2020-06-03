package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Setter
@Getter
@ApiModel(description = "文件系统文件操作请求列表对象")
public class SysFileArrayRequest extends BaseReqeust {
	
	@ApiModelProperty(value = "文件对象列表", name = "sysFileList",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	private List<SysFileRequest> sysFileList;
	
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"SysFileArrayRequest\":{");
		if (sysFileList != null) {
			sb.append("sysFileList=").append(sysFileList);
		}
		sb.append("},\"super-SysFileArrayRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
	
	@Override
	public String getEsLogMsg() {
		if(!CollectionUtils.isEmpty(this.getSysFileList()) && this.getSysFileList().get(0) != null){
			return StringUtils.join(this.getSysFileList().get(0).getPath(),this.getSysFileList().get(0).getFileName());
		}else{
			return "no-data";
		}
	}
}
