package com.zysl.cloud.aws.api.enums;

import lombok.Getter;


@Getter
public enum DownTypeEnum {

    FILE(0, "application/octet-stream","默认,下载文件流"),
    BASE64(1, "application/json","base64进制的String"),
    VIDEO(2, "video/mp4","视频");

    private Integer code;
    
    private String contentType;
    private String desc;

    DownTypeEnum(Integer code, String contentType,String desc) {
        this.code = code;
        this.contentType = contentType;
        this.desc = desc;
    }

    /**
     * 根据code获取value
     * @param code
     * @return
     */
    public String getDesc(String code){
        for(DownTypeEnum in : DownTypeEnum.values()){
            if(code.equals(in.getCode())){
                return in.getDesc();
            }
        }
        return null;
    }
}
