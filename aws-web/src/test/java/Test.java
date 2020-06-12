import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.utils.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {

  public static void  main(String[] args){
    Test test = new Test();
    String srcPath = "temp-002:/mmm/a";
    String destPath = "b/";
    try{
      
      System.out.println(DateUtils.getDateToString(new Date()));
    }catch (Exception e){
      e.printStackTrace();
    }
  }
  
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
  
  public String getDestKey(String srcPath, String destPath){
    //只能子目录复制，不能根目录复制
    if(StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(destPath)
        || srcPath.indexOf("/") <= 0 ){
      return  null;
    }
    
    String str = srcPath.split("/")[0];
    String destStr = destPath + srcPath.substring(str.length() + 1);
    return destStr;
  }
}
