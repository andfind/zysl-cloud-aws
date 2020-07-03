package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件重命名入参对象
 */
@Setter
@Getter
public class ObjectRenameRequestV implements IValidator {

    @NotBlank
    private String bucketName;
    @NotBlank
    private String sourcekey;
    @NotBlank
    private String destKey;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
