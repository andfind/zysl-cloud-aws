package com.zysl.cloud.aws.biz.service;

public interface IWordService {

    /**
     * word转pdf
     * @param inBuff word文件数据
     * @param imgMarkSign 图片水印标记
     * @param textMark 水印文字
     */
    byte[] changeWordToPDF(byte[] inBuff,Boolean imgMarkSign, String textMark);

    /**
     * word转其他格式
     * @param inBuff
     * @param toFormatType
     */
    byte[] changeWordToByApose(byte[] inBuff,Integer toFormatType);
    
    /**
     * word转pdf
     * @description
     * @author miaomingming
     * @date 11:35 2020/4/9
     * @param inBuff
     * @return byte[]
     **/
    byte[] changeWordToPDF(byte[] inBuff);


}
