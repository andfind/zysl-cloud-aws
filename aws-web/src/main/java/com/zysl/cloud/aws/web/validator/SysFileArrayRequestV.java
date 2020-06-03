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
public class SysFileArrayRequestV implements IValidator {
	
	@NotNull
	private List<SysFileRequest> sysFileList;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		String pathP = "^[0-9a-zA-Z\\-_]+:[^\\*\\|\\?\\\\<>:\"]+$";
		String fileNameP = "[^\\*\\|\\?\\\\<>:\"/]+$";
		
		for(SysFileRequest request:sysFileList){
			if(StringUtils.isNotEmpty(request.getPath()) && !java.util.regex.Pattern.matches(pathP, request.getPath())){
				errors.add("路径不能输入以下字符\\ : \" | * ? < >");
			}
			if(StringUtils.isNotEmpty(request.getFileName()) && !Pattern.matches(fileNameP, request.getFileName())){
				errors.add("文件名不能输入以下字符\\ : \" | * ? < > /");
			}
			if(!errors.isEmpty()){
				break;
			}
		}
		
	}
	
}
