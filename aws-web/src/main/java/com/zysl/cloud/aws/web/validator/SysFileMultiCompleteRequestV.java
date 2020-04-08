package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.dto.PartInfoDTO;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysFileMultiCompleteRequestV implements IValidator {
	@NotBlank
	private String path;
	@NotBlank
	private String fileName;
	@NotBlank
	private String uploadId;
	@NotNull
	private List<PartInfoDTO> eTagList;
	
	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {
	
	}
}
