package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建断点续传入参
 */
@Setter
@Getter
public class CreateMultipartRequestV implements IValidator {
    private static final long serialVersionUID = 6528744834290825581L;

    //文件夹名称
    @NotBlank
    private String bucketName;
    //文件名
    @NotBlank
    private String fileId;

    private String fileName;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
