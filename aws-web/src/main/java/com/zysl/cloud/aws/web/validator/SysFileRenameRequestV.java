package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysFileRenameRequestV implements IValidator {
	
	@NotNull
	private SysFileRequest source;
	@NotNull
	private SysFileRequest target;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(source == null || StringUtils.isBlank(source.getPath())){
			errors.add("源路径不能为空");
		}
		if(target == null || StringUtils.isBlank(target.getPath())){
			errors.add("目的路径不能为空");
		}
		
		String pathP = "^[0-9a-zA-Z\\-_]+:[^\\*\\|\\?\\\\<>:\"]+$";
		String fileNameP = "[^\\*\\|\\?\\\\<>:\"/]+$";
		if(!Pattern.matches(pathP, this.target.getPath())){
			errors.add("路径不能输入以下字符\\ : \" | * ? < >");
		}
		if(!Pattern.matches(fileNameP, this.target.getFileName())){
			errors.add("文件名不能输入以下字符\\ : \" | * ? < > /");
		}
	}
	
}
