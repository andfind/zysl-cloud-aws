package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysDirListRequestV implements IValidator {
	
	@NotBlank
	private String path;
	
	@Min(-1)
	@Max(1000)
	private Integer pageSize;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(path.indexOf(BizConstants.DISK_SEPARATOR) < 1 || path.length() < path.indexOf(BizConstants.DISK_SEPARATOR)+2 ){
			errors.add("列表查询路径格式异常.");
		}
	}
}
