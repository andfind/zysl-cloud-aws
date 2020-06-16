package com.zysl.cloud.aws.utils;


import com.zysl.cloud.utils.StringUtils;

import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BizUtil {

    /**
     * 无后缀的临时文件名：tmp_原文件名_yyyyMMddHHmmssSSS_3位随机数_
     * @param fileName
     * @return
     */
    public static String getTmpFileNameWithoutSuffix(String fileName){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateStr = sdf.format(new Date());
        Random random = new Random();

        if(fileName.indexOf(".") > -1){
            fileName = fileName.substring(0,fileName.lastIndexOf("."));
        }
        fileName += "_"+ dateStr + "_" + random.nextInt(1000) + "_";

        return fileName;
    }

    /**
     * 获取pdf文件名
     * @param fileName
     * @return

    public static String parseFName(String fileName,Integer formatType){
        if(fileName.indexOf(".") > -1){
            return fileName.substring(0,fileName.lastIndexOf(".") + 1) + SaveFormat.getName(formatType).toLowerCase();
        }
        return null;
    }*/

    /**
     * 获取/目录最后一个字符串
     * @param str
     * @return
     */
    public static String subLastString(String str){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        String[] strs = str.split("/");
        return strs[strs.length-1];
    }

    /**
     * 入参path格式化
     * scheme://编号或IP/完整路径#版本号
     * 完整路径格式:  /bucket/key   或者   /目录/文件
     * @description
     * @author miaomingming
     * @date 15:38 2020/6/16
     * @param path
     * @return java.net.URI
     **/
    public static URI formatPathURI(String path){
        try{
            URI uri = new URI(path);
            if(StringUtils.isNotEmpty(uri.getPath())
                && uri.getPath().split("/").length > 2){
                return uri;
            }
        }catch (URISyntaxException e){
            throw new AppLogicException("path.URISyntaxException", RespCodeEnum.ILLEGAL_PARAMETER.getCode());
        }
        throw new AppLogicException("path.URISyntaxException", RespCodeEnum.ILLEGAL_PARAMETER.getCode());
    }
}
