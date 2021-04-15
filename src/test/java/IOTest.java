import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 76204 on 2017/7/3.
 */
public class IOTest {
      public static void main(String[] args) {
          readDic();
      }

    private static  Map<String,String> readDic() {
        Map<String,String> dicMap=new HashMap<String, String>();
        try {
            List<String> list = FileUtils.readLines(new File("./Mydic.txt"), "utf-8");
            for (String each : list) {
                String code = each.split("\t")[0];
                String name = each.split("\t")[1];
                dicMap.put(name,code);
            }
//            System.out.println(list);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dicMap;
    }
}
