package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.service.utils.ObjectFormatUtils;
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
public class SysKeyCreateRequestV implements IValidator {

	@NotBlank
	 private String path;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		PathUriBO pathUriBO = ObjectFormatUtils.formatS3PathURI(path);
		if(StringUtils.isNotEmpty(pathUriBO.getBucket()) && !Pattern.matches(WebConstants.S3_BUCKET_VALID_PATTERN, pathUriBO.getBucket())){
			errors.add(WebConstants.S3_BUCKET_VALID_DESC);
		}
		if(StringUtils.isNotEmpty(pathUriBO.getKey()) && !Pattern.matches(WebConstants.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
			errors.add(WebConstants.S3_KEY_VALID_DESC);
		}
	}
}
