package com.zysl.cloud.aws.biz.enums;

import com.zysl.cloud.utils.enums.RespCodeEnum;
import lombok.Getter;

//错误编号，在 com.zysl.cloud.utils.enums.RespCodeEnum 基础上扩展
//格式5xxyyzz:  xx、yy、xx表示3层分类编号，xx在这里定义，例如5010001
//大类定义
//501yyzz:s3调用异常
//502yyzz:s3及bucket相关
//503yyzz:s3的key相关
//504yyzz:文件类型转换相关，比如word、pdf
//505yyzz:
//506yyzz:
//507yyzz:
//508yyzz:
@Getter
public enum  ErrCodeEnum  {

	S3_SERVER_CALL_METHOD_NO_RESPONSE(5010001, "调用s3接口:无返回."),
	S3_SERVER_CALL_METHOD_RESPONSE_STATUS_ERROR(5010002, "调用s3接口:返回状态码异常."),
	S3_SERVER_CALL_METHOD_INVOKE_ERROR(5010003, "调用s3接口:反射处理异常."),
	S3_SERVER_CALL_METHOD_NO_SUCH(5010004, "调用s3接口:无此接口."),
	S3_SERVER_CALL_METHOD_ERROR(5010005, "调用s3接口:其他异常."),
	S3_SERVER_CALL_METHOD_NO_SUCH_KEY(5010005, "调用s3接口:无此key."),
	S3_SERVER_CALL_METHOD_AWS_SERVICE_EXCEPTION(5010006, "调用s3接口:awsServer异常"),
	S3_SERVER_CALL_METHOD_S3_EXCEPTION(5010007, "调用s3接口:s3异常."),

	S3_SERVER_NO_NOT_EXIST(5020001, "不存在的服务器编号."),
	S3_BUCKET_NOT_EXIST(5020002, "不存在的bucket编号."),
	S3_CREATE_BUCKET_EXIST(5020003, "创建bucket已存在."),

	S3_BUCKET_OBJECT_NOT_EXIST(5030001, "创建对象已存在."),


	WORD_FILE_NOT_EXIST(5040001, "word转pdf：找不到原始文件."),
	WORD_FILE_TO_PDF_ERROR(5040002, "word转pdf：转换异常."),
	WORD_FILE_TO_PDF_SIZE_ZERO(5040003, "word转pdf：转换后文件大小为0."),
	WORD_FILE_TO_PDF_ENCRYPTION_SIZE_ZERO(5040004, "word转pdf：加密后文件大小为0."),
	;

	private Integer code;

	private String desc;


	ErrCodeEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
