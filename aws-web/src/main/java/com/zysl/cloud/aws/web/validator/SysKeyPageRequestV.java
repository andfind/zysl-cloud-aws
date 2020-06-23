package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.web.constants.WebConstants;
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
			errors.add("path format error.");
			return;
		}
		if(!Pattern.matches(WebConstants.S3_BUCKET_VALID_PATTERN, pathUriBO.getHost())){
			errors.add(WebConstants.S3_BUCKET_VALID_DESC);
		}
		if(!StringUtils.isEmpty(pathUriBO.getKey()) && !Pattern.matches(WebConstants.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
			errors.add(WebConstants.S3_KEY_VALID_DESC);
		}
	}
}
