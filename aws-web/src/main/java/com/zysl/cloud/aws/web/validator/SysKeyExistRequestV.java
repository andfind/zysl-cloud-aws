package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.web.constants.WebConstants;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class SysKeyExistRequestV implements IValidator {
	
	private List<String> paths;
	
	@NotNull
	private String fileName;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		
		if(!CollectionUtils.isEmpty(paths)){
			for(String path:paths){
				checkPath(errors,path,"path");
			}
		}
		
	}
	
	
	private void checkPath(List<String> errors,String path,String msgName){
		if(StringUtils.isEmpty(path)){
			errors.add(msgName + "不能为空.");
		}
		
		PathUriBO pathUriBO = ObjectFormatUtils.formatS3PathURI(path);
		if(pathUriBO == null
			|| StringUtils.isEmpty(pathUriBO.getScheme())
			|| StringUtils.isEmpty(pathUriBO.getHost())
			){
			errors.add(msgName + "格式化异常.");
		}
		if(StringUtils.isNotEmpty(pathUriBO.getKey())){
			if(!pathUriBO.getKey().endsWith(BizConstants.PATH_SEPARATOR)){
				errors.add(msgName + "不能是对象.");
			}
			if(!Pattern.matches(WebConstants.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
				errors.add(WebConstants.S3_KEY_VALID_DESC);
			}
		}
		
		
		if(!Pattern.matches(WebConstants.S3_BUCKET_VALID_PATTERN, pathUriBO.getHost())){
			errors.add(WebConstants.S3_BUCKET_VALID_DESC);
		}
		
	}
}