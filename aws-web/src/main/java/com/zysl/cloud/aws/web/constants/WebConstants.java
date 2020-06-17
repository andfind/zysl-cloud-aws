package com.zysl.cloud.aws.web.constants;

public class WebConstants {
	//入参路径正则校验及返回描述
	public static final String VALID_PATH_PATTERN = "^[0-9a-zA-Z\\-_]+:[^\\*\\|\\?\\\\<>:\"]+$";
  	public static final String VALID_PATH_DESC = "盘符只能输入数字字母及下划线,路径不能输入以下字符\\ : \" | * ? < >";
	//入参fileName正则校验及返回描述
	public static final String VALID_FILE_NAME_PATTERN = "[^\\*\\|\\?\\\\<>:\"/]+$";
  	public static final String VALID_FILE_NAME_DESC = "文件名不能输入以下字符\\ : \" | * ? < > /";
	
	
	
	
	//入参路径正则校验及返回描述
	public static final String S3_BUCKET_VALID_PATTERN = "^[0-9a-zA-Z\\-_]++$";
	public static final String S3_BUCKET_VALID_DESC = "bucket只能输入数字字母及下划线";
	//入参fileName正则校验及返回描述
	public static final String S3_KEY_VALID_PATTERN = "[^\\*\\|\\?\\\\<>:\"]+$";
	public static final String S3_KEY_VALID_DESC = "key不能输入以下字符\\ : \" | * ? < >";
 
}
