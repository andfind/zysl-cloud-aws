package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@ApiModel(description = "文件是否存在操作请求对象")
public class SysKeyExistRequest extends BaseReqeust {
	
	@ApiModelProperty(value = "路径列表", name = "paths",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	private List<String> paths;
	@ApiModelProperty(value = "文件名", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;
	
	@Override
	public String getEsLogMsg() {
		return this.fileName;
	}
	
	
	public SysKeyExistRequest(){}
	
	
}
