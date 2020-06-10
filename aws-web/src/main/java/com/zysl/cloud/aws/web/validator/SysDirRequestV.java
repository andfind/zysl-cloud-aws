package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.web.constants.WebConstants;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.pl.REGON;

@Setter
@Getter
public class SysDirRequestV implements IValidator {

  @NotBlank
  private String path;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(StringUtils.isNotEmpty(this.path) && !Pattern.matches(WebConstants.VALID_PATH_PATTERN, this.path)){
			errors.add(WebConstants.VALID_PATH_DESC);
		}
	}
}
