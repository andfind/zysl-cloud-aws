package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@ApiModel(description = "复制操作请求对象")
public class SysKeyCopyRequest extends BaseReqeust {
	
	
	private static final long serialVersionUID = 326556756619029135L;
	//格式：  scheme://bucket或IP/完整路径#版本号
	//scheme枚举     |      完整路径
	//   s3          |     key
	//   ftp         |     目录/文件
	//   sharepoint  |     目录/文件
	@ApiModelProperty(value = "源路径", name = "srcPath",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String srcPath;
	
	@ApiModelProperty(value = "目标路径", name = "destPath",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String destPath;
	
	@ApiModelProperty(value = "是否覆盖", name = "isCover",dataType = SwaggerConstants.DATA_TYPE_BOOLEAN)
	private Boolean isCover = true;
	
	@Override
	public String getEsLogMsg() {
		return StringUtils.join(this.srcPath,"->",this.destPath);
	}
	
}
