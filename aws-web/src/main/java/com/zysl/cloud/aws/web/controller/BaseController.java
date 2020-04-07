package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;

public class BaseController extends com.zysl.cloud.utils.common.BaseController {

	/**
	 * 切个s3的key为路径+文件名
	 * @description
	 * @author miaomingming
	 * @date 14:43 2020/3/26
	 * @param s3ObjectBO
	 * @param s3Key
	 * @return void
	 **/
	public void setPathAndFileName(S3ObjectBO s3ObjectBO,String s3Key){
		if(StringUtils.isBlank(s3Key)){
			return;
		}
		if(s3ObjectBO == null){
			s3ObjectBO = new S3ObjectBO();
		}
		if(s3Key.startsWith("/")){
			s3Key = s3Key.substring(1);
		}
		if(s3Key.endsWith("/")){
			s3ObjectBO.setPath(s3Key);
			s3ObjectBO.setFileName("");
		}else{
			s3ObjectBO.setPath(s3Key.substring(0,s3Key.lastIndexOf("/")+1));
			s3ObjectBO.setFileName(s3Key.substring(s3Key.lastIndexOf("/")+1));
		}
	}
	
	/**
	 * 文件系统操作设置bucket及path，
	 * dirPath格式: s3:[s3服务器编号/]bucketName:path
	 * 例如:  s3:s001/temp-001:/a/b/c
	 * 例如:  s3:temp-001:/a/b/c
	 * 例如:  s3:temp-001:/a/b/c/
	 * @description
	 * @author miaomingming
	 * @date 16:08 2020/4/4
	 * @param s3ObjectBO
	 * @param dirPath
	 * @return void
	 **/
	public void setBucketAndPath(S3ObjectBO s3ObjectBO,String dirPath){
		if(StringUtils.isBlank(dirPath)){
			return;
		}
		if(s3ObjectBO == null){
			s3ObjectBO = new S3ObjectBO();
		}
	}
	


	public static void main(String[] args) {
		BaseController t = new BaseController();
		S3ObjectBO s3ObjectBO = new S3ObjectBO();

		t.setPathAndFileName(s3ObjectBO, "a/");
		System.out.println(s3ObjectBO);
	}

}
