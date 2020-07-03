package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建目录folder请求对象
 */
@Getter
@Setter
public class CreateFolderRequestV implements IValidator {

	@NotBlank
	private String folderName;
	@NotBlank
	private String bucketName;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {

	}
}
