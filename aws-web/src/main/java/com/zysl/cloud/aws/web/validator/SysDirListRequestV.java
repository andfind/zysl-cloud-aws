package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysDirListRequestV implements IValidator {
	
	@NotBlank
	private String path;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(path.indexOf(":") == -1 ){
			errors.add("列表查询路径格式异常.");
		}else{
			String str = path.substring(path.indexOf(":")+2);
			if(StringUtils.isBlank(str)){
				errors.add("列表查询不能针对根目录.");
			}
		}
	}
}
