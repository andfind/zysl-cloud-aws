package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysDirRequestV implements IValidator {
	
	@NotBlank
	private String path;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
	
	}
}
