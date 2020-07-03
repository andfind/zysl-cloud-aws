package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.EnumValue;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryObjectsRequestV implements IValidator {
    private static final long serialVersionUID = -3261527596066639380L;

    //存储桶名称
    @NotBlank
    private String bucketName;
    //0默认全部，1仅目录2仅文件
    @EnumValue(target = {"0", "1", "2"},message="字段不在枚举范围")
    private Integer keyType;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
