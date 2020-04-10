package com.zysl.cloud.aws.biz.test;

import com.zysl.cloud.utils.StringUtils;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		System.out.println("---start---");
		Test test = new Test();
		test.getRegin();
		System.out.println("---end---used:" + (System.currentTimeMillis() - start));
	}
	
	public void getRegin(){
//		String content = "mmm:/水电费/sdf.09/";
//		String pattern = "^[0-9a-zA-Z]+:[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#]+$";
		String content = "12.\\3.doc";
		String pattern = "[^\\s\\$\\*\\{\\}\\[\\]\\^\\|\\?&@=;:+,%`\">~<#/\\\\]+$";
		boolean isMatch = Pattern.matches(pattern, content);
		System.out.println("正则校验结果： " + isMatch);
	}
}
