package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.config.ValidatorConfig;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysDirRequestV implements IValidator {

  @NotBlank
  private String path;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(StringUtils.isNotEmpty(this.path) && !Pattern.matches(ValidatorConfig.VALID_PATH_PATTERN, this.path)){
			errors.add(ValidatorConfig.VALID_PATH_DESC);
		}
	}
}
