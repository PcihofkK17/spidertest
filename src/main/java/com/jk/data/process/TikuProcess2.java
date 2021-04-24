package com.jk.data.process;

import com.jk.data.com.jk.data.util.MyFileUtils;
import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.QuestLabelWord;
import com.jk.data.mybatis.dao.QuestLabelWordDao;
import com.jk.data.mybatis.dao.RelateBookDao;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 76204 on 2017/7/11.
 */
public class TikuProcess2  implements PageProcessor{
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private static Map<String, String> myDic = new HashMap<String, String>();
    private static RelateBookDao relateBookDao = AppUtils.daoFactory(RelateBookDao.class);
    private static QuestLabelWordDao questLabelWordDao = AppUtils.daoFactory(QuestLabelWordDao.class);
    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        if(url.matches("http://www.tiku.com/index.html")){
            //主页
            List<Selectable> tables = page.getHtml().xpath("//table[@class='stsj']").nodes();
            for (Selectable table : tables) {
                String termName = table.xpath("//tbody/tr/td[@rowspan='2']/text()").get();
                List<Selectable> lis = table.xpath("//tbody/tr/td/ul/li").nodes();
                for (Selectable li : lis) {
                    String courseName = li.xpath("//li/label/text()").get();
                    String href = li.xpath("//li/a/@href").get();
                    System.out.println(termName+"--------"+courseName+"---------"+href);
                    page.addTargetRequest(href);

                }
            }
        }else if(url.matches("http://www.tiku.com/testPaper.html\\?cid=\\d+\\&cn=.*?\\&st=\\d+")){
            List<Selectable> lis = page.getHtml().xpath("//div[@class='g-screen']/ul/li").nodes();
            for(int i=1;i<lis.size();i++){
                List<Selectable> dds = lis.get(i).xpath("//li/dl/div/dd").nodes();
                String type = lis.get(i).xpath("//li/dl/dt[@for]/text()").get();
                for (Selectable dd : dds) {
                    String word = dd.xpath("//dd/a/text()").get();
                    if("全部".equals(word)){
                        continue;
                    }
                    String href = dd.xpath("//dd/a/@href").get();
                    System.out.println(type+"-----"+word+"-----"+href);
                    String wordId = myDic.get(word);
                    if(wordId==null){
                        System.out.println("------漏掉----"+word);
                        MyFileUtils.writer("C:/cjh/word.txt",type+"---"+word+"\r\n",true);
                    }
                }
            }
        }
    }

    @Override
    public Site getSite() {
        myDic=readDic();

        return Site.me()
                .setTimeOut(30000)
                .setUserAgent(userAgent);
    }

      public static void main(String[] args) {
        String  url="http://www.tiku.com/index.html";
//        url="http://www.tiku.com/testPaper.html?cid=500004&cn=%E6%95%B0%E5%AD%A6&st=2";
          Spider.create(new TikuProcess2())
                  .addUrl(url)
                  .run();
        }

    /**
     * 读取编码配置
     *
     * @return
     */
    private static Map<String, String> readDic() {
        Map<String, String> dicMap = new HashMap<>();

        List<QuestLabelWord> questLabelWords = questLabelWordDao.getAll();
        for (QuestLabelWord questLabelWord : questLabelWords) {
            String name = questLabelWord.getName();
            String id = questLabelWord.getId();
            dicMap.put(name, id);
        }
        return dicMap;
    }
}
