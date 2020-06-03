package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysFileExistRequestV implements IValidator {
	
	@NotNull
	private List<SysDirRequest> paths;
	
	@NotBlank
	private String fileName;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		for(SysDirRequest dir:paths){
			if(StringUtils.isEmpty(dir.getPath())){
				errors.add("查询路径不能为空");
			}
		}
	}
}
