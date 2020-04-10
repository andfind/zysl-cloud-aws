package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysFileRequestV implements IValidator {

  @NotBlank
  @Pattern(
      regexp = "^[0-9a-zA-Z]+:[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#]+$",
      message = "路径不能输入以下字符$*{}[]^|?&@=;:+,%`\">~<#")
  private String path;

  @NotBlank
  @Pattern(
      regexp = "[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#/\\\\]+$",
      message = "文件名不能输入以下字符$*{}[]^|?&@=;:+,%`\">~<#")
  private String fileName;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
	
	}
}
