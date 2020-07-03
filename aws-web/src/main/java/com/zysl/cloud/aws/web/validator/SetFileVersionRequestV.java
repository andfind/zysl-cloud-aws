package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.config.ValidatorConfig;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.EnumValue;
import com.zysl.cloud.utils.validator.impl.LengthChar;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 设置文件版本接口入参
 */
@Setter
@Getter
public class SetFileVersionRequestV implements IValidator {


    @LengthChar(min = 3, max = 63)
    @NotBlank
    private String bucketName;

    //版本权限状态  Enabled：启动 Suspended：关闭
    @NotBlank
    @EnumValue(target = {"Enabled","Suspended"})
    private String status;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase){
        String pattern = ValidatorConfig.S3_BUCKET_VALID_PATTERN;//"^[a-z0-9.\\-_]{3,63}$";
        //判断存储桶是否满足命名规则
        if(StringUtils.isNotEmpty(bucketName) && !Pattern.compile(pattern).matcher(bucketName).matches()){
            errors.add(ValidatorConfig.S3_BUCKET_VALID_DESC);
        }
        
    }
}
