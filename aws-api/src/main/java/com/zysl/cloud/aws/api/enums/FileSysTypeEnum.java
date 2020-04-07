package com.zysl.cloud.aws.api.enums;

import lombok.Getter;

/**
 * 文件系统类型
 * @description
 * @author miaomingming
 * @date 9:19 2020/4/7
 * @return
 **/
@Getter
public enum  FileSysTypeEnum {
	S3("s3", "s3"),
	LOCAL("local", "local"),
	FTP("ftp", "ftp");
	
	private String code;
	
	private String desc;
	
	FileSysTypeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	/**
	 * 根据code获取value
	 * @param code
	 * @return
	 */
	public String getDesc(String code){
		for(FileSysTypeEnum in : FileSysTypeEnum.values()){
			if(code.equals(in.getCode())){
				return in.getDesc();
			}
		}
		return null;
	}
}
