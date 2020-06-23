package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.web.constants.WebConstants;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class SysKeyCopyRequestV implements IValidator {
	
	@NotNull
	private String srcPath;
	
	@NotNull
	private String destPath;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		PathUriBO srcBO = checkPath(errors,this.srcPath,"srcPath");
		PathUriBO destBO = checkPath(errors,this.destPath,"destPath");
		
		if(srcBO.getKey().endsWith(BizConstants.PATH_SEPARATOR) && !destBO.getKey().endsWith(BizConstants.PATH_SEPARATOR)){
			errors.add("destPath为对象时srcPath不能是目录.");
		}
	}
	
	
	private PathUriBO checkPath(List<String> errors,String path,String msgName){
		if(StringUtils.isEmpty(path)){
			errors.add(msgName + "不能为空.");
		}
		
		PathUriBO pathUriBO = ObjectFormatUtils.formatS3PathURI(path);
		if(pathUriBO == null
			|| StringUtils.isEmpty(pathUriBO.getScheme())
			|| StringUtils.isEmpty(pathUriBO.getHost())
			|| StringUtils.isEmpty(pathUriBO.getKey())
			){
			errors.add(msgName + "格式化异常.");
			return pathUriBO;
		}
		
		if(!Pattern.matches(WebConstants.S3_BUCKET_VALID_PATTERN, pathUriBO.getHost())){
			errors.add(WebConstants.S3_BUCKET_VALID_DESC);
		}
		if(!Pattern.matches(WebConstants.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
			errors.add(WebConstants.S3_KEY_VALID_DESC);
		}
		return pathUriBO;
	}
}
