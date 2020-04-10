package com.zysl.cloud.aws.biz.test;

import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AposeTest {
	
	public static void main(String[] args){
		long start = System.currentTimeMillis();
		System.out.println("---start---");
		AposeTest test = new AposeTest();
		String a="11";
		String b=null;
		System.out.println(StringUtils.join(a,b));
		System.out.println("---end---used:" + (System.currentTimeMillis() - start));
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
		String fileName = "D:/data/tmp/1.pptx";
		String outFileName = "D:/data/tmp/1.pdf";
		try{
			getLicense();
			
			Presentation pres = new Presentation(fileName);
			
			//创建文件
			FileOutputStream out = new FileOutputStream(new File(outFileName));
			// 保存转换的pdf文件
			pres.save(out, SaveFormat.Pdf);
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
//	public void testWord(){
//		String fileName = "/data/tmp/1.docx";
//		String outFileName = "/data/tmp/1.pdf";
//		try{
//			Document doc = new Document(fileName);
//			//创建文件
//			FileOutputStream fileOS = new FileOutputStream(new File(outFileName));
//			// 保存转换的pdf文件
//			doc.save(fileOS, SaveFormat.PDF);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
	
	private boolean getLicense() {
		boolean result = false;
		try {
//      String licenseFile = "D:\\data\\libs\\java\\ppt\\Aspose\\aspose-slides-19.3\\license.xml";
			String licenseFile = "D:\\data\\git\\zysl-cloud-aws\\aws-web\\src\\main\\resources\\license.xml";
			InputStream is = new FileInputStream(new File(licenseFile));
			License aposeLic = new License();
			aposeLic.setLicense(is);
			result = true;
		} catch (Exception e) {
			throw new AppLogicException(ErrCodeEnum.APOSE_SIGN_CHECK_ERROR.getCode());
		}
		return result;
	}
}
