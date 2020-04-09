package com.zysl.cloud.aws.biz.service;

public interface IPPTService {
	
	/**
	 * ppt/pptx转pdf
	 * @description
	 * @author miaomingming
	 * @date 15:18 2020/4/9
	 * @param inBuff
	 * @return byte[]
	 **/
	byte[] changePPTToPDF(byte[] inBuff);
}
