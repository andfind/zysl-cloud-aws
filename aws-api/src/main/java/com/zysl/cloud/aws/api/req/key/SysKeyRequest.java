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
@ApiModel(description = "创建操作请求对象")
public class SysKeyRequest extends BaseReqeust {
	
	private static final long serialVersionUID = -4110290877043549213L;
	
	//格式：  scheme://bucket或IP/完整路径#版本号
	//scheme枚举     |      完整路径
	//   s3          |     key
	//   ftp         |     目录/文件
	//   sharepoint  |     目录/文件
	@ApiModelProperty(value = "路径", name = "path",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String path;
	
	
	//以下字段，根据上面的path字段转化过来
	
	@ApiModelProperty(value = "协议：s3，sharePoint、ftp", name = "scheme",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String scheme;
	
	@ApiModelProperty(value = "bucket或者IP", name = "host",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String host;
	
	@ApiModelProperty(value = "完整路径", name = "key",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String key;
	
	@ApiModelProperty(value = "版本", name = "versionId",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String versionId;
	
	@Override
	public String getEsLogMsg() {
		return this.path;
	}
	
	
	public SysKeyRequest(){}
	
	public SysKeyRequest(String path){
		this.setPath(path);
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
		if(StringUtils.isEmpty(this.path)){
			return;
		}
		try{
			URI uri = new URI(this.path);
			this.setScheme(uri.getScheme());
			this.setVersionId(uri.getFragment());
			this.setHost(uri.getHost());
			if(StringUtils.isNotEmpty(uri.getPath())){
				if(FileSysTypeEnum.S3.getCode().equals(this.getScheme())){
					this.setKey(uri.getPath().substring(1));
				}else{
					this.setKey(uri.getPath());
				}
			}
		}catch (URISyntaxException e){
			log.warn("ES_LOG_Exception {} {}",this.path, ExceptionUtil.getMessage(e));
		}
	}
}
