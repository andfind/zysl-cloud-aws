package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.web.constants.WebConstants;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
public class SysKeyDeleteListRequestV implements IValidator {
	
	@NotNull
	private List<SysKeyDeleteRequest> pathList;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		if(CollectionUtils.isEmpty(pathList)){
			errors.add("pathList不能为空.");
		}
		for(SysKeyDeleteRequest request:pathList){
			//path不能为空
			if(StringUtils.isEmpty(request.getPath())){
				errors.add("path不能为空.");
			}else{
				SysKeyRequestV sysKeyRequestV = BeanCopyUtil.copy(request,SysKeyRequestV.class);
				sysKeyRequestV.customizedValidate(errors,userCase);
			}
		}
		
	}
	
}
