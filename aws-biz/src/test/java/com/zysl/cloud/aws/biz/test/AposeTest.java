package com.zysl.cloud.aws.biz.test;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AposeTest {
	
	public static void main(String[] args){
		System.out.println("---start---");
		AposeTest test = new AposeTest();
		test.testRenameDir();
		System.out.println("---end---");
	}
	
	public void testRenameDir(){
    String path = "D:\\KwDownload\\song";
		List<String> prefixs = new ArrayList<>();
		for(int i=1;i<=9;i++){
			prefixs.add(i+"-");
		}
		
		File file = new File(path);
		File[] files = file.listFiles();
		if(files != null){
			for(File subFile:files){
				if(subFile.isDirectory()){
					for(String prefix:prefixs){
						if(subFile.getName().startsWith(prefix)){
							testRename(subFile,prefix);
							continue;
						}
					}
				}
			}
		}
	}
	
	public void testRename(File file,String prefix){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files != null){
				for(File subFile:files){
//					System.out.println("parent:"+subFile.getParent());
//					System.out.println("path:"+subFile.getPath());
					if(subFile.isDirectory()){
						testRename(subFile,prefix);
					}else{
						if(!subFile.getName().startsWith(prefix)){
							String newName = subFile.getParent() +"\\"+ prefix + subFile.getName();
//							System.out.println("parent:"+subFile.getParent());
//							System.out.println("path:"+subFile.getPath());
							System.out.println("rename:"+subFile.getPath()  + " --> " + newName);
							subFile.renameTo(new File(newName));
						}
					}
				}
			}
		}
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
