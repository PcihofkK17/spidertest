package com.jk.data.process;

import com.jk.data.util.MyFileUtils;
import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.QuestLabelWord;
import com.jk.data.mybatis.bean.RbookQuest;
import com.jk.data.mybatis.bean.RelateBook;
import com.jk.data.mybatis.dao.QuestLabelWordDao;
import com.jk.data.mybatis.dao.RbookQuestDao;
import com.jk.data.mybatis.dao.RelateBookDao;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 76204 on 2017/7/11.
 */
public class TikuProcess2 implements PageProcessor {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private static Map<String, String> myDic = new HashMap<String, String>();
    private static RelateBookDao relateBookDao = AppUtils.daoFactory(RelateBookDao.class);
    private static QuestLabelWordDao questLabelWordDao = AppUtils.daoFactory(QuestLabelWordDao.class);
    private static RbookQuestDao rbookQuestDao = AppUtils.daoFactory(RbookQuestDao.class);

    private static Map<String, String> termMap = new HashMap<>();

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        if (url.matches("http://www.tiku.com/index.html")) {
            //主页
            List<Selectable> tables = page.getHtml().xpath("//table[@class='stsj']").nodes();
            for (Selectable table : tables) {
                String termName = table.xpath("//tbody/tr/td[@rowspan='2']/text()").get();
                List<Selectable> lis = table.xpath("//tbody/tr/td/ul/li").nodes();
                for (Selectable li : lis) {
                    String courseName = li.xpath("//li/label/text()").get();
                    String href = li.xpath("//li/a/@href").get();
                    System.out.println(termName + "--------" + courseName + "---------" + href);
                    page.addTargetRequest(href);

                }
            }
        } else if (url.matches("http://www.tiku.com/testPaper.html\\?cid=\\d+\\&cn=.*?\\&st=\\d+")) {
            //科目

            String course = null;
            try {
                course = URLDecoder.decode(page.getUrl().regex("cn=(.*?)\\&").get(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String st = page.getUrl().regex("st=(\\d+)$").get();
            String term = termMap.get(st);
            String siteId = myDic.get("题库网");
            String gradeId = myDic.get(term);
            String courseId = myDic.get(course);
            System.out.println(gradeId + "--------" + courseId);
            List<Selectable> lis = page.getHtml().xpath("//div[@class='g-screen']/ul/li").nodes();

            Map<String, List<String>> finalMap = new HashMap<>();
            for (int i = 1; i < lis.size(); i++) {
                List<Selectable> dds = lis.get(i).xpath("//li/dl/div/dd").nodes();
                String type = lis.get(i).xpath("//li/dl/dt[@for]/text()").get();
                List<String> codes = new ArrayList<>();
                for (Selectable dd : dds) {
                    String word = dd.xpath("//dd/a/text()").get();
                    if ("全部".equals(word)) {
                        continue;
                    }
                    String href = dd.xpath("//dd/a/@href").get();
                    System.out.println(type + "-----" + word + "-----" + href);
                    String wordId = myDic.get(word);

                    if (wordId == null) {
                        System.out.println("------漏掉----" + word);
                        MyFileUtils.writer("C:/cjh/word.txt", type + "---" + word + "\r\n", true);

                    } else {
                        codes.add(wordId);


                        if(type.contains("教材版本")){
                            String vid = dd.xpath("//dd/a/@href").regex("vid=(\\d+)").get();
                        }else if(type.contains("课本")){
                            String bid = dd.xpath("//dd/a/@href").regex("bid=(\\d+)").get();
                        }
                    }
                }
                finalMap.put(codes.get(0).substring(0, 1), codes);
            }

            System.out.println(finalMap);

            List<String> dList = finalMap.get("D");
            List<String> eList = finalMap.get("E");
            List<String> fList = finalMap.get("F");
            List<String> hList = finalMap.get("H");
            List<String> iList = finalMap.get("I");

            hList.addAll(iList);
            for (String h : hList) {
                RbookQuest rbookQuest = new RbookQuest();
                String  rbId=siteId + gradeId + courseId;
                String  qid=h;
                rbookQuest.setQid(qid);
                rbookQuest.setRelateBookId(rbId);
                rbookQuestDao.add(rbookQuest);
            }

            RelateBook relateBook =null;
            String branchId = null;
            String termId = null;
            String bookId = null;
            String id = null;

            if (eList != null) {
                for (String d : dList) {
                    for (String e : eList) {
                         relateBook = new RelateBook();
                         branchId = d;
                         termId = e;
                         bookId = "F00";
                         id = siteId + gradeId + courseId + branchId + termId + bookId;

                    }
                }
            } else if(fList!=null) {
                for (String d : dList) {
                    for (String f : fList) {
                         relateBook = new RelateBook();
                         branchId = d;
                         termId = "E00";
                         bookId = f;
                         id = siteId + gradeId + courseId + branchId + termId + bookId;

                    }
                }
            }else{
                for (String d : iList) {
                     relateBook = new RelateBook();
                     branchId = d;
                     termId = "E00";
                     bookId = "F00";
                     id = siteId + gradeId + courseId + branchId + termId + bookId;
                }
            }

            relateBook.setId(id);
            relateBook.setSiteId(siteId);
            relateBook.setGradeId(gradeId);
            relateBook.setCourseId(courseId);
            relateBook.setBranchId(branchId);
            relateBook.setTermId(termId);
            relateBook.setBookId(bookId);
            relateBookDao.add(relateBook);


            String cid = page.getUrl().regex("cid=(\\d+)").get();
            List<String> vids = page.getHtml().regex("vid\\=(\\d+)").all();
            List<String> bids = page.getHtml().regex("bid\\=(\\d+)").all();

            for (String bid : bids) {
                for (String vid : bids) {
                    String cUrl = course_tmp.replace("AA", st).replace("BB", cid).replace("CC", bid).replace("DD", vid);
                    System.out.println("curl::::"+cUrl);

                }
            }

        }
    }


    private static final String  course_tmp="http://www.tiku.com/testPaper.html?hdSearch=&key=&sct=0&st=AA&cid=BB&bid=CC&vid=DD&sort=0";

    @Override
    public Site getSite() {
        myDic = readDic();

        return Site.me()
                .setTimeOut(100000)
                .setRetryTimes(3)
                .setUserAgent(userAgent);
    }

    public static void main(String[] args) {
        String url = "http://www.tiku.com/index.html";
//        url = "http://www.tiku.com/testPaper.html?cid=500014&cn=%E6%95%B0%E5%AD%A6&st=0";
//        url = "http://www.tiku.com/testPaper.html?cid=500004&cn=%E6%95%B0%E5%AD%A6&st=2";
        url="http://www.tiku.com/testPaper.html?cid=500004&cn=%E6%95%B0%E5%AD%A6&st=2";
        Spider.create(new TikuProcess2())
                .thread(5)
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


    static {
        termMap.put("0", "小学");
        termMap.put("1", "初中");
        termMap.put("2", "高中");
    }
}
