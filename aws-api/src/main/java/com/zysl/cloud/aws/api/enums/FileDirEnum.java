package com.zysl.cloud.aws.api.enums;

import lombok.Getter;

@Getter
public enum FileDirEnum {

    FILE(1, "文件"),
    DIR(0, "目录");

    private Integer code;

    private String desc;

    FileDirEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取value
     * @param code
     * @return
     */
    public String getDesc(String code){
        for(FileDirEnum in : FileDirEnum.values()){
            if(code.equals(in.getCode())){
                return in.getDesc();
            }
        }
        return null;
    }
}
