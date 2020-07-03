package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.config.ValidatorConfig;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysKeyOfficeRequestV implements IValidator {

	 @NotBlank
	 private String path;
	 
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		PathUriBO pathUriBO = ObjectFormatUtils.checkS3PathURINotNull(path,errors);
		if(errors.size() > 0){
			return ;
		}
		if(pathUriBO.getKey().endsWith(BizConstants.PATH_SEPARATOR)){
			errors.add("path must be object.");
		}
		
		if(!Pattern.matches(ValidatorConfig.S3_BUCKET_VALID_PATTERN, pathUriBO.getHost())){
			errors.add(ValidatorConfig.S3_BUCKET_VALID_DESC);
		}
		if(!Pattern.matches(ValidatorConfig.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
			errors.add(ValidatorConfig.S3_KEY_VALID_DESC);
		}
	}
}
