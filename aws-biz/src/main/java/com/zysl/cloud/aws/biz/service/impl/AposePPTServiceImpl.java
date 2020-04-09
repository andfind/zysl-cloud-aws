package com.zysl.cloud.aws.biz.service.impl;

import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.IPPTService;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AposePPTServiceImpl implements IPPTService {

    
    @Override
    public byte[] changePPTToPDF(byte[] inBuff){
        log.info("===inBuff.length:{}",inBuff == null ? 0 : inBuff.length);
        if(inBuff == null || inBuff.length == 0 ){
            return null;
        }
    
        byte[] outBuff = null;
        ByteArrayInputStream is = null;
        OutputStream os = new ByteArrayOutputStream();
        // 验证License
        getLicense();
    
        try{
            is = new ByteArrayInputStream(inBuff);
            Presentation pres = new Presentation(is);
            // 保存转换的pdf文件
            pres.save(os, SaveFormat.Pdf);
        
            outBuff = ((ByteArrayOutputStream) os).toByteArray();
            os.close();
        
            return outBuff;
        }catch (Exception e){
            log.error("===changePPTToPDF=== error ：{}", e);
            throw new AppLogicException(ErrCodeEnum.PPT_FILE_TO_PDF_ERROR.getCode());
        }finally {
            try {
                if(os != null){
                    os.close();
                }
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                log.error("===changeWordToPDFByApose===stream close error ：{}", e);
            }
        }
    }

    private boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            log.error("--apose校验异常：{}--", e);
            throw new AppLogicException(ErrCodeEnum.APOSE_SIGN_CHECK_ERROR.getCode());
        }
        return result;
    }
}
