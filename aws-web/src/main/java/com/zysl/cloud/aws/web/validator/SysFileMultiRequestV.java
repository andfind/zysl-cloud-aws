package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysFileMultiRequestV implements IValidator {
	@NotBlank
	private String path;
	@NotBlank
	private String fileName;
	@NotBlank
	private String uploadId;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
	
	}
}
