package com.zysl.cloud.aws.web.validator;

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
		String pathP = "^[0-9a-zA-Z\\-_]+:[^\\*\\|\\?\\\\<>:\"]+$";
		String fileNameP = "[^\\*\\|\\?\\\\<>:\"/]+$";
		if(!Pattern.matches(pathP, this.path)){
			errors.add("路径不能输入以下字符\\ : \" | * ? < >");
		}
		if(!Pattern.matches(fileNameP, this.fileName)){
			errors.add("文件名不能输入以下字符\\ : \" | * ? < > /");
		}
	}
}
