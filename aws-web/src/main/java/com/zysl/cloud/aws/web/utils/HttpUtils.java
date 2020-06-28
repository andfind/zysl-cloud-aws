package com.zysl.cloud.aws.web.utils;

import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.enums.DownTypeEnum;
import com.zysl.cloud.aws.api.req.SysFileUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
		long max = BizConstants.MULTI_DOWNLOAD_FILE_MAX_SIZE -1;
		log.info("range:{},max:{}",range,max);
		Long[] byteLength = new Long[2];
		byteLength[0] = 0L;
		byteLength[1] = max;
		if(StringUtils.isBlank(range) || range.length() <= 6){
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
			if(end - start >= max){
				end = start + max;
			}
			
			byteLength[0] = start;
			byteLength[1] = end;
			log.info("start:{},end:{}",start,end);
			return byteLength;
		}catch (Exception e){
			log.error("multi.download.range.format.error:{},",range,e);
			throw new AppLogicException(ErrCodeEnum.MULTI_DOWNLOAD_FILE_FORMAT_RANGE_ERROR.getCode());
		}
	}
	
	/**
	 * 下载文件流
	 * @description
	 * @author miaomingming
	 * @date 11:19 2020/4/9
	 * @param request
	 * @param response
	 * @param fileName
	 * @param bodys
	 * @return void
	 **/
	public static void downloadFileByte(HttpServletRequest request, HttpServletResponse response, String fileName,byte[] bodys){
		downloadFileByte(request,response,fileName,bodys, DownTypeEnum.FILE.getContentType());
	}
	
	public static void downloadFileByte(HttpServletRequest request, HttpServletResponse response, String fileName,byte[] bodys,String contentType){
		try {
			//1下载文件流
			OutputStream outputStream = response.getOutputStream();
			response.setCharacterEncoding("UTF-8");
			//告诉浏览器输出内容为流
			response.setContentType(contentType);
			
			if(DownTypeEnum.FILE.getContentType().equals(contentType)){
				setFileName(request,response,fileName);
			}
			
			outputStream.write(bodys);
			outputStream.flush();
			outputStream.close();
			
		} catch (IOException e) {
			log.error("ES_LOG_EXCEPTION {} IOException:{}", fileName, ExceptionUtil.getMessage(e));
			throw new AppLogicException(ErrCodeEnum.DOWNLOAD_FILE_ERROR.getCode());
		}
	}
	
	public static void setFileName(HttpServletRequest request, HttpServletResponse response, String fileName){
		//获取浏览器名（IE/Chome/firefox）
		String userAgent = request.getHeader("User-Agent");
		try{
			// IE浏览器
			if (isIE(userAgent)) {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}else{
				// 谷歌
				fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
			}
			response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
		}catch (UnsupportedEncodingException e){
			log.warn("ES_LOG {} UnsupportedEncodingException", fileName,e);
		}
	}
	/**
	 * 判断是否IE浏览器
	 * @description
	 * @author miaomingming
	 * @param userAgent
	 * @return boolean
	 **/
	public static boolean isIE(String userAgent){
		if(StringUtils.isEmpty(userAgent)){
			return Boolean.FALSE;
		}
		userAgent = userAgent.toUpperCase();
		if(userAgent.contains("MSIE") ||
			(userAgent.indexOf("GECKO")>0 && userAgent.indexOf("RV:11")>0)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 从HttpServletRequest读取上传文件流
	 * @description
	 * @author miaomingming
	 * @date 11:18 2020/4/9
	 * @param httpServletRequest
	 * @return byte[]
	 **/
	public static byte[] getBytesFromHttpRequest(HttpServletRequest httpServletRequest)throws AppLogicException{
		try {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)httpServletRequest;
			MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
			return multipartFile == null ? null : multipartFile.getBytes();
		} catch (IOException e) {
			log.error("--uploadFile获取文件流异常--：{}", e);
			throw new AppLogicException("获取文件流异常");
		}
	}
	public static byte[] getBytesFromHttpRequest(HttpServletRequest httpServletRequest, SysFileUploadRequest request)throws AppLogicException{
		try {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)httpServletRequest;
			MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
			if(StringUtils.isEmpty(request.getFileName())){
				request.setFileName(multipartFile.getOriginalFilename());
			}
			return multipartFile.getBytes();
		} catch (IOException e) {
			log.error("--uploadFile获取文件流异常--：{}", e);
			throw new AppLogicException("获取文件流异常");
		}
	}
	
	public static byte[] getBytesFromHttpRequest(HttpServletRequest httpServletRequest, SysKeyUploadRequest request)throws AppLogicException{
		try {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)httpServletRequest;
			MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
			if(StringUtils.isEmpty(request.getFileName())){
				request.setFileName(multipartFile.getOriginalFilename());
			}
			return multipartFile.getBytes();
		} catch (IOException e) {
			log.error("--uploadFile获取文件流异常--：{}", e);
			throw new AppLogicException("获取文件流异常");
		}
	}
	
	
}
