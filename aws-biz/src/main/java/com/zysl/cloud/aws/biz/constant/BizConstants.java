package com.zysl.cloud.aws.biz.constant;

import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.utils.StringUtils;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BizConstants {
    
    @Autowired
    private WebConfig webConfig;
    
    //租后更新bucket列表时间
    public static Date LAST_UPDATE_BUCKET_LIST_DATE = new Date();
    //更新bucketList间隔，单位秒
    public static Long MAX_INTERVAL_UPDATE_BUCKET_LIST= 600L;
    
    //转pdf的水印标签位置
    public static final Integer PDF_MARK_IMG_WIDTH = 300;
    public static final Integer PDF_MARK_IMG_HEIGHT = 300;
    public static final Integer PDF_MARK_TEXT_WIDTH = 300;
    public static final Integer PDF_MARK_TEXT_HEIGHT = 300;

    //s3对象tag中版本号的key
    public static final String S3_TAG_KEY_VERSION_NO = "verNo";
    public static final String S3_TAG_KEY_FILE_NAME = "fileName";
    
//    // 分享默认目录
//    public static final String SHARE_DEFAULT_FOLDER = "share";
    
    // 分片下载单次最大字节数，bizconfig启动时会初始化
    public static long MULTI_DOWNLOAD_FILE_MAX_SIZE = 0L;
    
    //路径分隔符
    public static final String PATH_SEPARATOR = "/";
    //盘符分隔符
    public static final String DISK_SEPARATOR = ":";
    
    @PostConstruct
    public void init() {
        log.info("init.multipartDownloadMaxFileSize:{}", webConfig.getMultipartDownloadMaxFileSize());
        try{
            if(StringUtils.isNotBlank(webConfig.getMultipartDownloadMaxFileSize())){
                String data = webConfig.getMultipartDownloadMaxFileSize();
                if (data.endsWith("KB")) {
                    MULTI_DOWNLOAD_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-2)) * 1024;
                }else if(data.endsWith("MB")){
                    MULTI_DOWNLOAD_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-2))  * 1024  * 1024;
                }else if (data.endsWith("B")) {
                    MULTI_DOWNLOAD_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-1));
                }
            }
        }catch (Exception e){
            log.error("init.multipartDownloadMaxFileSize.error:{}",webConfig.getMultipartDownloadMaxFileSize());
        }
    }
    
}
