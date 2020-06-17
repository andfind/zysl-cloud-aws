package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.LengthChar;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseBucketRequestV implements IValidator {

	@LengthChar(min = 3, max = 63)
	@NotBlank
	private String bucketName;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase){
		String pattern = "^[a-zA-Z0-9.\\-_]{3,63}$";
		//判断存储桶是否满足命名规则
		if(StringUtils.isNotBlank(bucketName) && !Pattern.compile(pattern).matcher(bucketName).matches()){
			errors.add("存储桶不满足命名规则.");
		}
	}
}
