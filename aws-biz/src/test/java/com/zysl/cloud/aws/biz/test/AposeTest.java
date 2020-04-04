package com.zysl.cloud.aws.biz.test;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

public class AposeTest {
	
	public static void main(String[] args){
		System.out.println("---start---");
		AposeTest test = new AposeTest();
		test.testPPT();
		System.out.println("---end---");
	}
	
	
	
	
	public void testPPT(){
		String fileName = "/data/tmp/1.pptx";
		String outFileName = "/data/tmp/1.pdf";
		try{
			Document doc = new Document(fileName);
			//创建文件
			FileOutputStream fileOS = new FileOutputStream(new File(outFileName));
			// 保存转换的pdf文件
			doc.save(fileOS, SaveFormat.PDF);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void testWord(){
		String fileName = "/data/tmp/1.docx";
		String outFileName = "/data/tmp/1.pdf";
		try{
			Document doc = new Document(fileName);
			//创建文件
			FileOutputStream fileOS = new FileOutputStream(new File(outFileName));
			// 保存转换的pdf文件
			doc.save(fileOS, SaveFormat.PDF);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
