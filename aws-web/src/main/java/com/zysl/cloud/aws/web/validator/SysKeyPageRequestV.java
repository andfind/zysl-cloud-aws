package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.config.ValidatorConfig;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysKeyPageRequestV implements IValidator {

	 @NotBlank
	 private String path;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		PathUriBO pathUriBO = ObjectFormatUtils.formatS3PathURI(path);
		if(pathUriBO == null
			|| StringUtils.isEmpty(pathUriBO.getScheme())
			|| StringUtils.isEmpty(pathUriBO.getHost())
			){
			errors.add(ValidatorConfig.AWS_PATH_FORMAT_ERROR_DESC);
			return;
		}
		if(!Pattern.matches(ValidatorConfig.S3_BUCKET_VALID_PATTERN, pathUriBO.getHost())){
			errors.add(ValidatorConfig.S3_BUCKET_VALID_DESC);
		}
		if(!StringUtils.isEmpty(pathUriBO.getKey()) && !Pattern.matches(ValidatorConfig.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
			errors.add(ValidatorConfig.S3_KEY_VALID_DESC);
		}
	}
}
