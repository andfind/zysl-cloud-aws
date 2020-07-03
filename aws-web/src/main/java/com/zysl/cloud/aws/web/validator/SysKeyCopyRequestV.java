package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.config.ValidatorConfig;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysKeyCopyRequestV implements IValidator {
	
	@NotNull
	private String srcPath;
	
	@NotNull
	private String destPath;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
		PathUriBO srcBO = checkPath(errors,this.srcPath,"srcPath");
		PathUriBO destBO = checkPath(errors,this.destPath,"destPath");
		
		if(srcBO != null && StringUtils.isNotEmpty(srcBO.getKey()) && srcBO.getKey().endsWith(BizConstants.PATH_SEPARATOR)
			&& destBO != null && StringUtils.isNotEmpty(destBO.getKey()) && !destBO.getKey().endsWith(BizConstants.PATH_SEPARATOR)){
			errors.add(ValidatorConfig.COPY_OBJECT_CHECK_MSG);
		}
	}
	
	
	private PathUriBO checkPath(List<String> errors,String path,String msgName){
		if(StringUtils.isEmpty(path)){
			errors.add(msgName + " can not null.");
		}
		
		PathUriBO pathUriBO = ObjectFormatUtils.checkS3PathURINotNull(path,errors);
		if(errors.size() > 0){
			return pathUriBO;
		}
		
		if(!Pattern.matches(ValidatorConfig.S3_BUCKET_VALID_PATTERN, pathUriBO.getHost())){
			errors.add(ValidatorConfig.S3_BUCKET_VALID_DESC);
		}
		if(!Pattern.matches(ValidatorConfig.S3_KEY_VALID_PATTERN, pathUriBO.getKey())){
			errors.add(ValidatorConfig.S3_KEY_VALID_DESC);
		}
		return pathUriBO;
	}
}
