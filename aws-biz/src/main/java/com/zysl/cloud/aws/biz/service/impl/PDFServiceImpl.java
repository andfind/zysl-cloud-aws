package com.zysl.cloud.aws.biz.service.impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.IPDFService;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PDFServiceImpl implements IPDFService {

    @Autowired
    private BizConfig bizConfig;

    @Override
    public byte[] addPdfImgMark(byte[] inBuff, String markImagePath) {
        byte[] outBuff = null;
        OutputStream os = new ByteArrayOutputStream();
        try{
            PdfReader reader = new PdfReader(inBuff);
            PdfStamper stamp = new PdfStamper(reader, os);
            PdfContentByte under;

            PdfGState gs1 = new PdfGState();
            // 透明度设置
            gs1.setFillOpacity(0.8f);
            // 插入图片水印
            Image img = Image.getInstance(markImagePath);

            img.setAbsolutePosition(BizConstants.PDF_MARK_IMG_WIDTH, BizConstants.PDF_MARK_IMG_HEIGHT); // 坐标
            img.setRotation(-20);// 旋转 弧度
            img.setRotationDegrees(45);// 旋转 角度
            img.scaleAbsolute(200, 200);// 自定义大小
            // img.scalePercent(50);//依照比例缩放

            int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
            for (int i = 1; i <= pageSize; i++) {
                //under = stamp.getUnderContent(i);// 水印在之前文本下
                under = stamp.getOverContent(i);//水印在之前文本上
                under.setGState(gs1);// 图片水印 透明度
                under.addImage(img);// 图片水印
            }

            stamp.close();// 关闭
            reader.close();
    
            outBuff = ((ByteArrayOutputStream) os).toByteArray();
            os.close();
    
            return outBuff;
        }catch (Exception e){
            log.error("pdf加图片水印异常:{}",e);
            throw new AppLogicException("paf add ImgMark error.");
        }
    }

    
    @Override
    public byte[] addPdfTextMark(byte[] inBuff, String textMark){
        byte[] outBuff = null;
        int textWidth = BizConstants.PDF_MARK_TEXT_WIDTH;
        int textHeight = BizConstants.PDF_MARK_TEXT_WIDTH;
        OutputStream os = new ByteArrayOutputStream();
        try{
            PdfReader reader = new PdfReader(inBuff);
            PdfStamper stamp = new PdfStamper(reader, os);
            
            PdfContentByte under;
            
            BaseFont font = BaseFont.createFont(bizConfig.FONT_FILE, BaseFont.IDENTITY_H, true); // 使用系统字体
            
            int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
            for (int i = 1; i <= pageSize; i++) {
                //under = stamp.getUnderContent(i);// 水印在之前文本下
                for(int j=0;j<5;j++){
                    under = stamp.getOverContent(i);//水印在之前文本上
                    under.beginText();
                    under.setColorFill(new BaseColor(211,211,211));// 文字水印 颜色
                    under.setFontAndSize(font, 50);// 文字水印 字体及字号
                    under.setTextMatrix(textWidth + (j * 10), textHeight + (j * 50));// 文字水印 起始位置
                    under.showTextAligned(Element.ALIGN_CENTER, textMark, textWidth, textHeight + j*100, 45);//开始写入水印
                    under.endText();
                }
                
            }
            stamp.close();// 关闭
            reader.close();
    
            outBuff = ((ByteArrayOutputStream) os).toByteArray();
            os.close();
    
            return outBuff;
        }catch (Exception e){
            log.error("pdf加文字水印异常:{}",e);
            throw new AppLogicException(ErrCodeEnum.PDF_ADD_TEXT_MARK_ERROR.getCode());
        }
    }

    @Override
    public byte[] addPwd(byte[] inBuff, String userPwd, String ownerPwd){
        byte[] outBuff = null;
        OutputStream os = null;
        try{
            os = new ByteArrayOutputStream();
            PdfReader reader = new PdfReader(inBuff);
            PdfStamper stamp = new PdfStamper(reader, os);

            stamp.setEncryption(userPwd.getBytes(),ownerPwd.getBytes(), PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);

            stamp.close();// 关闭
            reader.close();

            outBuff = ((ByteArrayOutputStream) os).toByteArray();
            os.close();

            return outBuff;
        }catch (Exception e){
            log.error("===addPwd===error:{}",e);
            throw new AppLogicException(ErrCodeEnum.PDF_ADD_PWD_ERROR.getCode());
        }finally {
        }
    }
}
