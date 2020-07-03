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
		
		if(!Pattern.matches(ValidatorConfig.VALID_PATH_PATTERN, this.target.getPath())){
			errors.add(ValidatorConfig.VALID_PATH_DESC);
		}
		
		if(StringUtils.isNotEmpty(this.source.getFileName()) && !Pattern.matches(ValidatorConfig.VALID_FILE_NAME_PATTERN, this.source.getFileName())){
			errors.add("源"+ValidatorConfig.VALID_FILE_NAME_DESC);
		}
		if(StringUtils.isNotEmpty(this.target.getFileName()) && !Pattern.matches(ValidatorConfig.VALID_FILE_NAME_PATTERN, this.target.getFileName())){
			errors.add("目标"+ValidatorConfig.VALID_FILE_NAME_DESC);
		}
	}
	
}
