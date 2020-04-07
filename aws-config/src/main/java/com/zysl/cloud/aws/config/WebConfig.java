package com.zysl.cloud.aws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Getter
@Setter
public class WebConfig {
	
	//文件系统缺省类型：s3
	@Value("${file.system.type.default}")
	public String fileSystemTypeDefault;
	
	//文件系统缺省服务器编号：s002
	@Value("${file.system.serverNo.default}")
	public String fileSystemServerNoDefault;
	
	/**
	 * 分片上传单次最大字长度，支持MB、KB、B
	 **/
	@Value("${multipart.upload.max-file-size}")
	private String multipartUploadMaxFileSize;
	
	/**
	 * 单次下载最大长度，单位M
	 **/
	@Value("${download.max-file-size}")
	private Integer downloadMaxFileSize;
}
