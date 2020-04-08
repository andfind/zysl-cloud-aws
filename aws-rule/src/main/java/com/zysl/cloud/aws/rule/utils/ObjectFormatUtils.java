package com.zysl.cloud.aws.rule.utils;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.enums.FileDirEnum;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;

public class ObjectFormatUtils {
	
	/**
	 * 拆分bucket和路径
	 * @description
	 * @author miaomingming
	 * @date 14:59 2020/4/7
	 * @param s3ObjectBO
	 * @param filePath 包含bucketName
	 * @return void
	 **/
	public static void setBucketAndPath(S3ObjectBO s3ObjectBO,String filePath){
		if(StringUtils.isBlank(filePath) || s3ObjectBO == null){
			return;
		}
		if(filePath.indexOf(":") > -1){
			s3ObjectBO.setBucketName(filePath.substring(0,filePath.indexOf(":")));
			s3ObjectBO.setPath(filePath.substring(filePath.indexOf(":")+2));
			//s3的路径不需要/开头，但是需要/结尾
			if(!s3ObjectBO.getPath().endsWith("/")){
				s3ObjectBO.setPath(s3ObjectBO.getPath()+"/");
			}
		}
		
	}
	
	/**
	 * 合并bucket、path、fileName
	 * @description
	 * @author miaomingming
	 * @date 15:02 2020/4/7
	 * @param dto
	 * @param bucket
	 * @param s3Key
	 * @return void
	 **/
	public static void setPathAndFileName(SysFileDTO dto,String bucket,String s3Key){
		if(StringUtils.isBlank(s3Key) || dto == null){
			return;
		}
		if(s3Key.startsWith("/")){
			s3Key = s3Key.substring(1);
		}
		if(s3Key.endsWith("/")){
			dto.setPath(bucket + ":/" + s3Key);
		}else{
			dto.setPath(bucket + ":/" + s3Key.substring(0,s3Key.lastIndexOf("/")+1));
			dto.setFileName(s3Key.substring(s3Key.lastIndexOf("/")+1));
		}
	}
	
	/**
	 * 创建S3ObjectBO，设置bucket和路径
	 * @description
	 * @author miaomingming
	 * @date 15:01 2020/4/7
	 * @param request
	 * @return com.zysl.cloud.aws.domain.bo.S3ObjectBO
	 **/
	public static S3ObjectBO createS3ObjectBO(SysFileRequest request){
		S3ObjectBO s3ObjectBO = new S3ObjectBO();
		s3ObjectBO.setFileName(request.getFileName());
		s3ObjectBO.setVersionId(request.getVersionId());
		
		setBucketAndPath(s3ObjectBO,request.getPath());
		
		return s3ObjectBO;
	}
	
	/**
	 * s3ObjectBO转SysFileDTO
	 * @description
	 * @author miaomingming
	 * @date 17:25 2020/4/7
	 * @param bo
	 * @return void
	 **/
	public static SysFileDTO  s3ObjectBOToSysFileDTO(S3ObjectBO bo){
		SysFileDTO dto = new SysFileDTO();
		dto.setIsFile(FileDirEnum.FILE.getCode());
		dto.setVersionId(bo.getVersionId());
		dto.setSize(bo.getContentLength());
		dto.setLastModified(bo.getLastModified());
		setPathAndFileName(dto,bo.getBucketName(),bo.getFileName());
		
		return dto;
	}
}
