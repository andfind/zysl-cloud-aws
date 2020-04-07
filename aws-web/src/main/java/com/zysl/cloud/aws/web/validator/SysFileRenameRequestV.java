package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotNull;

public class SysFileRenameRequestV implements IValidator {
	
	@NotNull
	private SysFileRequest source;
	@NotNull
	private SysFileRequest target;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(StringUtils.isBlank(source.getPath())){
			errors.add("源路径不能为空");
		}
		if(StringUtils.isBlank(target.getPath())){
			errors.add("目的路径不能为空");
		}
	}
	
}
