package com.zysl.cloud.aws.api.req.key;

import com.zysl.cloud.aws.api.enums.FileSysTypeEnum;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BasePaginationRequest;
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
import org.springframework.util.CollectionUtils;

@Slf4j
@Setter
@Getter
@ApiModel(description = "批量删除操作请求对象")
public class SysKeyDeleteListRequest extends BaseReqeust {
	
	@ApiModelProperty(value = "路径列表", name = "pathList",required = true,dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	List<SysKeyDeleteRequest> pathList;
	
	
	@Override
	public String getEsLogMsg() {
		if(CollectionUtils.isEmpty(pathList)){
			return null;
		}
		StringBuffer sb = new StringBuffer(256);
		pathList.forEach(path->{sb.append(path.getPath()).append(";");});
		return sb.toString();
	}
	
	/**
	 * 转化path到其他字段
	 * @description
	 * @author miaomingming
	 * @date 15:08 2020/6/19
	 * @param
	 * @return void
	 **/
	public void formatPathURI(){
		if(CollectionUtils.isEmpty(pathList)){
			return;
		}
		pathList.forEach(path->{path.formatPathURI();});
	}
}
