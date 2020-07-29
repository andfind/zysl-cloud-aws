package com.zysl.cloud.aws.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * s3对象信息
 */
@Getter
@Setter
public class ObjectInfoBO implements Serializable {

    private static final long serialVersionUID = 8036080657342894989L;

    //bucket
    private String bucket;
    //对象名称
    private String key;
    //文件上传时间
    private Instant uploadTime;
    //文件大小
    private Long fileSize;
    //文件内容md5
    private String contentMd5;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{\"ObjectInfoBO\":{");
        if (bucket != null) {
            sb.append("bucket='").append(bucket).append('\'');
        }
        if (key != null) {
            sb.append(", bucketKey='").append(key).append('\'');
        }
        if (uploadTime != null) {
            sb.append(", uploadTime=").append(uploadTime);
        }
        if (fileSize != null) {
            sb.append(", fileSize=").append(fileSize);
        }
        if (contentMd5 != null) {
            sb.append(", contentMd5='").append(contentMd5).append('\'');
        }
        sb.append("}}");
        return sb.toString();
    }
}
