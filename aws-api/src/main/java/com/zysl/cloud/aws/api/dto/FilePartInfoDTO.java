package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询分区上传记录返回对象
 */
@Setter
@Getter
@ApiModel(description = "查询分区上传记录返回对象")
public class FilePartInfoDTO implements Serializable {
    
    private static final long serialVersionUID = 713457311306224690L;
    @ApiModelProperty(value = "文件分片上传ID", name = "uploadId", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String uploadId;
    
    @ApiModelProperty(value = "分片上传数据", name = "eTagList", dataType = SwaggerConstants.DATA_TYPE_ARRAY)
    private List<PartInfoDTO> eTagList;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{\"FilePartInfoDTO\":{");
        if (uploadId != null) {
            sb.append("uploadId='").append(uploadId).append('\'');
        }
        if (eTagList != null) {
            sb.append(", eTagList=").append(eTagList);
        }
        sb.append("}}");
        return sb.toString();
    }
}
