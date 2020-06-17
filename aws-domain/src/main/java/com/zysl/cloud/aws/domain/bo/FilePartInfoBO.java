package com.zysl.cloud.aws.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class FilePartInfoBO implements Serializable {
    private static final long serialVersionUID = -3016264913382019065L;

    private Integer partNumber;
    private Date lastModified;
    private String eTag;
    private Long size;
    private String uploadId;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{\"FilePartInfoBO\":{");
        if (partNumber != null) {
            sb.append("partNumber=").append(partNumber);
        }
        if (lastModified != null) {
            sb.append(", lastModified=").append(lastModified);
        }
        if (eTag != null) {
            sb.append(", eTag='").append(eTag).append('\'');
        }
        if (size != null) {
            sb.append(", size=").append(size);
        }
        if (uploadId != null) {
            sb.append(", uploadId='").append(uploadId).append('\'');
        }
        sb.append("}}");
        return sb.toString();
    }
}
