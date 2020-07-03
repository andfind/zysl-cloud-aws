package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.config.ValidatorConfig;
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
		
		for(SysFileRequest request:sysFileList){
			//path不能为空
			if(StringUtils.isEmpty(request.getPath())){
				errors.add("path不能为空.");
			}else if(!Pattern.matches(ValidatorConfig.VALID_PATH_PATTERN, request.getPath())){
				errors.add(ValidatorConfig.VALID_PATH_DESC);
			}
			if(StringUtils.isNotEmpty(request.getFileName()) && !Pattern.matches(ValidatorConfig.VALID_FILE_NAME_PATTERN, request.getFileName())){
				errors.add(ValidatorConfig.VALID_FILE_NAME_DESC);
			}
			
			if(!errors.isEmpty()){
				break;
			}
		}
		
	}
	
}
