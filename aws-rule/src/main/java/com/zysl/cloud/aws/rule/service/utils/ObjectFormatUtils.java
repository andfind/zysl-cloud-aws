package com.zysl.cloud.aws.rule.service.utils;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.enums.FileDirEnum;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.domain.bo.PathUriBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import java.net.URI;
import java.net.URISyntaxException;

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
		if(filePath.indexOf(BizConstants.DISK_SEPARATOR) > -1 && filePath.length() >= filePath.indexOf(BizConstants.DISK_SEPARATOR)+2){
			s3ObjectBO.setBucketName(filePath.substring(0,filePath.indexOf(":")));
			s3ObjectBO.setPath(filePath.substring(filePath.indexOf(":")+2));
			//s3的路径不需要/开头，但是需要/结尾
			if(!s3ObjectBO.getPath().endsWith(BizConstants.PATH_SEPARATOR) && s3ObjectBO.getPath().length() > 0){
				s3ObjectBO.setPath(s3ObjectBO.getPath()+BizConstants.PATH_SEPARATOR);
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
		if(s3Key.startsWith(BizConstants.PATH_SEPARATOR)){
			s3Key = s3Key.substring(1);
		}
		if(s3Key.endsWith(BizConstants.PATH_SEPARATOR)){
			dto.setPath(bucket + ":/" + s3Key);
		}else{
			dto.setPath(bucket + ":/" + s3Key.substring(0,s3Key.lastIndexOf(BizConstants.PATH_SEPARATOR)+1));
			dto.setFileName(s3Key.substring(s3Key.lastIndexOf(BizConstants.PATH_SEPARATOR)+1));
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
	
	/**
	 * 入参path格式化
	 * scheme://编号或IP/完整路径#版本号
	 * 完整路径格式:  /bucket/key   或者   /目录/文件
	 * @description
	 * @author miaomingming
	 * @date 15:38 2020/6/16
	 * @param pathUri
	 * @return java.net.URI
	 **/
	public static PathUriBO formatS3PathURI(String pathUri){
		try{
			PathUriBO bo = new PathUriBO();
			//bucket 及 key
			String[] paths = new String[2];
			URI uri = new URI(pathUri);
			String path = uri.getPath();
			//去掉首个/
			path = path.substring(1);
			bo.setBucket(path.substring(0,path.indexOf("/")));
			bo.setKey(path.substring(paths[0].length()+1));
			bo.setScheme(uri.getScheme());
			bo.setVersionId(uri.getFragment());
			bo.setServerNo(uri.getHost());
			return  bo;
		}catch (URISyntaxException e){
			throw new AppLogicException("path.URISyntaxException", RespCodeEnum.ILLEGAL_PARAMETER.getCode());
		}catch (Exception e){
			throw new AppLogicException("path.formatException", RespCodeEnum.ILLEGAL_PARAMETER.getCode());
		}
	}
}
