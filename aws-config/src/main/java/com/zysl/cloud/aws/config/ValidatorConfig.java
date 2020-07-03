package com.zysl.cloud.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:validator.properties")
public class ValidatorConfig {
	
	//入参路径正则校验及返回描述
	public static  String VALID_PATH_PATTERN = "^[0-9a-z\\-_]+:[^\\*\\|\\?\\\\<>:\"]+$";
	public static  String VALID_PATH_DESC = "盘符只能输入数字字母及下划线,路径不能输入以下字符\\ : \" | * ? < >";
	//入参fileName正则校验及返回描述
	public static  String VALID_FILE_NAME_PATTERN = "[^\\*\\|\\?\\\\<>:\"/]+$";
	public static  String VALID_FILE_NAME_DESC = "文件名不能输入以下字符\\ : \" | * ? < > /";
	
	
	//存储桶名称校验正则入参路径正则校验及返回描述
	public static  String S3_BUCKET_VALID_PATTERN = "^[0-9a-z\\-_]{3,63}$";
	public static  String S3_BUCKET_VALID_DESC = "存储桶不满足命名规则.";
	//入参fileName正则校验及返回描述
	public static  String S3_KEY_VALID_PATTERN = "[^\\*\\|\\?\\\\<>:\"]+$";
	public static  String S3_KEY_VALID_DESC = "key不能输入以下字符\\ : \" | * ? < >";
	
	
	public static String COPY_OBJECT_CHECK_MSG="destPath为对象时srcPath不能是目录";
	
	
	@Value("${bucket.name.format.msg}")
	public void setBucketFormatMsg(String msg){
		S3_BUCKET_VALID_DESC = msg;
	}
	
	@Value("${path.name.format.msg}")
	public void setPathFormatMsg(String msg){
		VALID_PATH_DESC = msg;
	}
	
	
	@Value("${file.name.format.msg}")
	public void setFileNameFormatMsg(String msg){
		S3_BUCKET_VALID_DESC = msg;
	}
	
	
	@Value("${key.name.format.msg}")
	public void setKeyFormatMsg(String msg){
		S3_KEY_VALID_DESC = msg;
	}
	
	@Value("${copy.object.check.msg}")
	public void setCopyObjectCheckMsg(String msg){
		COPY_OBJECT_CHECK_MSG = msg;
	}
}
