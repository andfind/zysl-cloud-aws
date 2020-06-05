import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {

  public static void  main(String[] args){
    Test test = new Test();
    String copySourceUrl = "temp-001/mmm/a/1.txt";
    try{
  
      copySourceUrl = java.net.URLEncoder.encode(copySourceUrl, "utf-8");
    }catch (Exception e){
    
    }
    System.out.println(copySourceUrl);
  }
}
