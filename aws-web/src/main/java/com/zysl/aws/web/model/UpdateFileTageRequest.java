package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.services.s3.model.Tagging;

import java.io.Serializable;
import java.util.List;

/**
 * 修改文件tage入参对象
 */
@Setter
@Getter
public class UpdateFileTageRequest implements Serializable {
    private static final long serialVersionUID = -7591314280634497629L;
    //服务器bucket名称
    private String bucket;
    //文件集合
    private List<KeyVersionDTO> keyList;
    //标签集合
    private List<TageDTO> tageList;

    @Override
    public String toString() {
        return "UpdateFileTageRequest{" +
                "bucket='" + bucket + '\'' +
                ", keyList=" + keyList +
                ", tageList=" + tageList +
                '}';
    }
}