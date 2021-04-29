import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.QuestLabelWord;
import com.jk.data.mybatis.dao.QuestLabelWordDao;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by 76204 on 2017/7/3.
 */
public class QuestLoabelwordInput {
    public static void main(String[] args) throws IOException {
        List<String> lines = FileUtils.readLines(new File("./questlabelword.txt"), "utf-8");

        QuestLabelWordDao questLabelWordDao = AppUtils.daoFactory(QuestLabelWordDao.class);


        for (String line : lines) {
            String[] datas = line.split("\t");
            String id = datas[0];
            String name = datas[1];
            String type = datas[2];
            QuestLabelWord questLabelWord = new QuestLabelWord();
            questLabelWord.setType(type);
            questLabelWord.setId(id);
            questLabelWord.setName(name);

            questLabelWordDao.add(questLabelWord);
        }

    }

    private static Map<String, String> readDic() {
        Map<String, String> dicMap = new HashMap<String, String>();
        try {
            List<String> list = FileUtils.readLines(new File("./Mydic.txt"), "utf-8");
            for (String each : list) {
                String code = each.split("\t")[0];
                String name = each.split("\t")[1];
                dicMap.put(name, code);
            }
            System.out.println(list);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(dicMap);

        return dicMap;
    }
}
