package com.zysl.cloud.aws.web.utils;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpUtils {
	
	/**
	 * 校验range
	 * 	支持以下3种格式
	 * 	bytes=500-999` 表示第500-999字节范围的内容。
	 * 	bytes=-500` 表示最后500字节的内容。
	 * 	bytes=500-` 表示从第500字节开始到文件结束部分的内容。
	 * @description
	 * @author miaomingming
	 * @date 18:07 2020/4/2
	 * @param range
	 * @return java.lang.String
	 **/
	public static Long[] checkRange(String range){
		Long[] byteLength = new Long[2];
		byteLength[0] = 0L;
		byteLength[1] = BizConstants.MULTI_UPLOAD_FILE_MAX_SIZE-1;
		if(StringUtils.isBlank(range)){
			return byteLength;
		}
		long start = 0,end = 0;
		try{
			String[] ranges = range.substring(6).split("-");
			if(ranges.length != 2){
				log.error("multi.download.range.format.error:{}",range);
				throw new AppLogicException(ErrCodeEnum.MULTI_DOWNLOAD_FILE_FORMAT_RANGE_ERROR.getCode());
			}
			if(StringUtils.isNotBlank(ranges[0])){
				start = Long.parseLong(ranges[0]);
			}
			if(StringUtils.isNotBlank(ranges[0])){
				end = Long.parseLong(ranges[1]);
			}
			if(end - start >= BizConstants.MULTI_UPLOAD_FILE_MAX_SIZE - 1){
				end = start + BizConstants.MULTI_UPLOAD_FILE_MAX_SIZE - 1;
			}
			
			byteLength[0] = start;
			byteLength[1] = end;
			return byteLength;
		}catch (Exception e){
			log.error("multi.download.range.format.error:{},",range,e);
			throw new AppLogicException(ErrCodeEnum.MULTI_DOWNLOAD_FILE_FORMAT_RANGE_ERROR.getCode());
		}
	}
	
	public static void downloadFileByte(HttpServletRequest request, HttpServletResponse response, String fileName,byte[] bodys){
		try {
			//1下载文件流
			OutputStream outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
			response.setCharacterEncoding("UTF-8");
			
			String userAgent = request.getHeader("User-Agent").toUpperCase();//获取浏览器名（IE/Chome/firefox）
			
			
			if (userAgent.contains("MSIE") ||
				(userAgent.indexOf("GECKO")>0 && userAgent.indexOf("RV:11")>0)) {
				fileName = URLEncoder.encode(fileName, "UTF-8");// IE浏览器
			}else{
				fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");// 谷歌
			}
			response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
			
			outputStream.write(bodys);
			outputStream.flush();
			outputStream.close();
			
		} catch (IOException e) {
			log.error("--文件下载异常：--", e);
			throw new AppLogicException(ErrCodeEnum.DOWNLOAD_FILE_ERROR.getCode());
		}
	}
	
	

}