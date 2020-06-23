package com.zysl.cloud.aws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class BizConfig {

    //word转pdf用到的字体
    @Value("${spring.pdf.FONT_FILE}")
    public String FONT_FILE;
    

    //s3文件存放word转pdf的文件夹
    @Value("${pdf.default.root.path}")
    public String pdfDefaultRootPath;

    @Value("${spring.download.date}")
    public String DOWNLOAD_TIME;


    @Value("${share.file.bucket.name}")
    public String shareFileBucket;
    
    
    @Value("${pdf.file.bucket.name}")
    public String pdfFileBucket;

    /**
     * 版本号，测试用，可以不配置
     **/
    @Value("${application.version}")
    public String curVer;

    @Value("${aws.config.path}")
    public String awsConfigPath;


}
