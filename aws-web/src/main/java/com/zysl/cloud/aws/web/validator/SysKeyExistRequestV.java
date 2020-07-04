package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.config.ValidatorConfig;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
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
				checkPath(errors,path);
			}
		}
		
	}
	
	
	private void checkPath(List<String> errors,String path){
		if(StringUtils.isEmpty(path)){
			errors.add("path can not null.");
		}
		
		PathUriBO pathUriBO = ObjectFormatUtils.formatS3PathURI(path);
		if(pathUriBO == null
			|| StringUtils.isEmpty(pathUriBO.getScheme())
			|| StringUtils.isEmpty(pathUriBO.getHost())
			){
			errors.add(ValidatorConfig.AWS_PATH_FORMAT_ERROR_DESC);
		}
		if(StringUtils.isNotEmpty(pathUriBO.getKey())){
			if(!pathUriBO.getKey().endsWith(BizConstants.PATH_SEPARATOR)){
				errors.add(ValidatorConfig.AWS_FILE_EXIST_CHEK_KEY_DESC);
			}
			if(!Pattern.matches(ValidatorConfig.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
				errors.add(ValidatorConfig.S3_KEY_VALID_DESC);
			}
		}
		
		
		if(!Pattern.matches(ValidatorConfig.S3_BUCKET_VALID_PATTERN, pathUriBO.getHost())){
			errors.add(ValidatorConfig.S3_BUCKET_VALID_DESC);
		}
		
	}
}
