package com.zysl.cloud.aws.web.validator;

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
public class SysFileRequestV implements IValidator {

  @NotBlank
  private String path;

  @NotBlank
  private String fileName;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(StringUtils.isNotEmpty(this.path) && !Pattern.matches(WebConstants.VALID_PATH_PATTERN, this.path)){
			errors.add(WebConstants.VALID_PATH_DESC);
		}
		if(StringUtils.isNotEmpty(this.fileName) && !Pattern.matches(WebConstants.VALID_FILE_NAME_PATTERN, this.fileName)){
			errors.add(WebConstants.VALID_FILE_NAME_DESC);
		}
	}
}
