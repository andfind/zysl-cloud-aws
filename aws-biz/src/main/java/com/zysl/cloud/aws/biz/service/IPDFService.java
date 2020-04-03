package com.zysl.cloud.aws.biz.service;

public interface IPDFService {
    /**
     * pdf加图片水印
     */
    public byte[] addPdfImgMark(byte[] inBuff, String outPdfFile);

    /**
     * pdf加文字水印
     * @description
     * @author miaomingming
     * @date 19:18 2020/4/3
     * @param inBuff
     * @param textMark
     * @return byte[]
     **/
    public byte[] addPdfTextMark(byte[] inBuff, String textMark);

    /**
     * pdf文件加密码
     * @param inBuff
     * @param userPwd
     * @param ownerPwd
     */
    public byte[] addPwd(byte[] inBuff, String userPwd, String ownerPwd);
}
