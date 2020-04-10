package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.pl.REGON;

@Setter
@Getter
public class SysDirRequestV implements IValidator {

  @NotBlank
  @Pattern(
      regexp = "^[0-9a-zA-Z]+:[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#]+$",
      message = "路径不能输入以下字符$*{}[]^|?&@=;:+,%`\">~<#")
  private String path;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
	
	}
}
