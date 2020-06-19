package com.zysl.cloud.aws.biz.test;

import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.utils.StringUtils;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args){
		Test test = new Test();
		try{
			System.out.println((S3KeyBO)null);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	public void testUrlEncode(){
		try{
			String out = URLEncoder.encode("1+ ,{}[]#.txt", StandardCharsets.UTF_8.toString());
			System.out.println(out);
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void testa(S3KeyBO s3KeyBO){
		s3KeyBO.setBucket("33");
	}
	
	
	public void getRegin(){
		String path = "temp-002:/m/a-1";
		String fileName = "123.doc";
		String pathP = "^[0-9a-zA-Z\\-_]+:[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#]+$";
		String fileNameP = "[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#/\\\\]+$";
//		String content = "12.\\3.doc";
//		String pattern = "[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#/\\\\]+$";
		System.out.println("path正则校验结果： " + Pattern.matches(pathP, path));
		System.out.println("fileName正则校验结果： " + Pattern.matches(fileNameP, fileName));
	}
}
