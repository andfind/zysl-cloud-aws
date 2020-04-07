package com.zysl.cloud.aws.biz.constant;

import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.utils.StringUtils;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BizConstants {
    
    @Autowired
    private WebConfig webConfig;
    
    
    //转pdf的水印标签位置
    public static final Integer PDF_MARK_IMG_WIDTH = 300;
    public static final Integer PDF_MARK_IMG_HEIGHT = 300;
    public static final Integer PDF_MARK_TEXT_WIDTH = 300;
    public static final Integer PDF_MARK_TEXT_HEIGHT = 300;

    
    // 分享默认目录
    public static final String SHARE_DEFAULT_FOLDER = "share";
    
    // 分片上传单次最大字节数，bizconfig启动时会初始化
    public static long MULTI_UPLOAD_FILE_MAX_SIZE = 0L;
    
    @PostConstruct
    public void init() {
        log.info("init.multipartUploadMaxFileSize:{}", webConfig.getMultipartUploadMaxFileSize());
        try{
            if(StringUtils.isNotBlank(webConfig.getMultipartUploadMaxFileSize())){
                String data = webConfig.getMultipartUploadMaxFileSize();
                if (data.endsWith("B")) {
                    MULTI_UPLOAD_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-1));
                }else if (data.endsWith("KB")) {
                    MULTI_UPLOAD_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-2)) * 1024;
                }else if(data.endsWith("MB")){
                    MULTI_UPLOAD_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-2))  * 1024  * 1024;
                }
            }
        }catch (Exception e){
            log.error("init.multipartUploadMaxFileSize.error:{}",webConfig.getMultipartUploadMaxFileSize());
        }
    }
    
}
