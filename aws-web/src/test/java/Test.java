import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {

  public static void  main(String[] args){
    Test test = new Test();
    String srcPath = "a";
    String destPath = "b/";
    try{
  
      System.out.println(test.getDestKey(srcPath,destPath));
    }catch (Exception e){
      e.printStackTrace();
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
