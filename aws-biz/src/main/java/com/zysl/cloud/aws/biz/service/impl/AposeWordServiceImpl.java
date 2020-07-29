package com.zysl.cloud.aws.biz.service.impl;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.IPDFService;
import com.zysl.cloud.aws.biz.service.IWordService;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.utils.LogHelper;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AposeWordServiceImpl implements IWordService {

    @Autowired
    IPDFService pdfService;
    @Autowired
    BizConfig bizConfig;

    @Override
    public byte[] changeWordToPDF(byte[] inBuff,Boolean imgMarkSign, String textMark){
        LogHelper.info(getClass(),"changeWordToPDF","changeWordToPDF",String.format("imgMarkSign:%s,textMark:%s",imgMarkSign,textMark));
        if(inBuff == null || inBuff.length == 0){
            return null;
        }
    
        byte[] outBuff = null;
        OutputStream os = new ByteArrayOutputStream();
        try{
           // step 1.word转pdf
            outBuff = changeWordToByApose(inBuff,SaveFormat.PDF);

            //step 2.加文字水印
            if(textMark != null && !"".equals(textMark)){
                outBuff = pdfService.addPdfTextMark(outBuff,textMark);
            }

            return outBuff;
        }catch (Exception e){
            LogHelper.error(getClass(),"changeWordToPDF","changeWordToPDF","异常",e);
    
            throw new AppLogicException(ErrCodeEnum.WORD_FILE_TO_PDF_ERROR.getCode());
        }finally {
            try {
                if(os != null){
                    os.close();
                }
            } catch (IOException e) {
                LogHelper.error(getClass(),"changeWordToPDF","os.close","异常",e);
            }
        }
    }

    @Override
    public byte[] changeWordToByApose(byte[] inBuff,Integer toFormatType){
        LogHelper.error(getClass(),"changeWordToByApose","changeWordToByApose","length:"+(inBuff == null ? 0 : inBuff.length));
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
            Document doc = new Document(is);
            // 保存转换的pdf文件
            doc.save(os, toFormatType);
    
            outBuff = ((ByteArrayOutputStream) os).toByteArray();
            os.close();
            
            return outBuff;
        }catch (Exception e){
            LogHelper.error(getClass(),"changeWordToPDFByApose","changeWordToPDFByApose","异常",e);
            throw new AppLogicException(ErrCodeEnum.WORD_FILE_TO_PDF_ERROR.getCode());
        }finally {
            try {
                if(os != null){
                    os.close();
                }
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                LogHelper.error(getClass(),"changeWordToPDFByApose","os.close","异常",e);
            }
        }
    }
    
    @Override
    public byte[] changeWordToPDF(byte[] inBuff){
        LogHelper.error(getClass(),"changeWordToPDF","changeWordToPDF","length:"+(inBuff == null ? 0 : inBuff.length));
        if(inBuff == null || inBuff.length == 0){
            return null;
        }
    
        byte[] outBuff = null;
        OutputStream os = new ByteArrayOutputStream();
        try{
            // step 1.word转pdf
            outBuff = changeWordToByApose(inBuff,SaveFormat.PDF);
        
            return outBuff;
        }catch (Exception e){
            log.error("转换pdf时office异常:",e);
            LogHelper.error(getClass(),"changeWordToPDF","changeWordToPDF","异常",e);
            throw new AppLogicException(ErrCodeEnum.WORD_FILE_TO_PDF_ERROR.getCode());
        }finally {
            try {
                if(os != null){
                    os.close();
                }
            } catch (IOException e) {
                LogHelper.error(getClass(),"changeWordToPDF","os.close","异常",e);
            }
        }
    }

    private boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("license.xml");
            if(is == null){
                is = new FileInputStream(new File(bizConfig.getAwsConfigPath() + "/license.xml"));
            }
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
            is.close();
        } catch (Exception e) {
            LogHelper.error(getClass(),"getLicense","getLicense","异常",e);
            throw new AppLogicException(ErrCodeEnum.APOSE_SIGN_CHECK_ERROR.getCode());
        }
        return result;
    }
}
