import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 76204 on 2017/7/15.
 */
public class IOtest2 {
    public static void main(String[] args) {
        try {
            List<String> lines = FileUtils.readLines(new File("./ignore.txt"), "utf-8");
            Set<String> set1=new HashSet<>();
            Set<String> set2=new HashSet<>();
            for (String line : lines) {
                String  type=line.split("\t")[0];
                String  name=line.split("\t")[1];
                if("年级".equals(type)){
                    set1.add(name);
                }else{
                    set2.add(name);
                }

            }

            for (String each : set1) {
                System.out.println(each);

            }

            System.out.println("--------------------------------");
            for (String each : set2) {
                System.out.println(each);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        StringBuilder sb=new StringBuilder();
        sb.insert(0,"a");
        sb.insert(0,"b");
        System.out.println(sb.toString());

    }
}
