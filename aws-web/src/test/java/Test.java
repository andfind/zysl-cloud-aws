import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {

  public static void  main(String[] args){
    Test test = new Test();
    String filePath = "temp-001:/1";
    try{
      if(filePath.indexOf(":") > -1 && filePath.length() >= filePath.indexOf(":")+2){
        System.out.println(filePath.substring(0,filePath.indexOf(":")));
        System.out.println(filePath.substring(filePath.indexOf(":")+2));
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
