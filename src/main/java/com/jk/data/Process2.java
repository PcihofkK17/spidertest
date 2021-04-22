package com.jk.data;

import com.jk.data.com.jk.data.util.JsonUtils;
import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.*;
import com.jk.data.mybatis.dao.*;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by 76204 on 2017/6/29.
 */
public class Process2 implements PageProcessor {

    private String cat_url_tmp = "http://www.jyeoo.com/math2/ques/partialcategory?a=AA";
    private String ti_url_tmp = "http://www.jyeoo.com/physics/ques/partialques?q=AA";
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    private Map<String, String> myDic = new HashMap<String, String>();
    private Map<String, String> grade_course_map = new HashMap<String, String>(); //url-年级科目
    private Map<String, String> relateMap = new HashMap<String, String>();
    private Map<String, String> bookMap = new HashMap<String, String>(); //课本

    private static RelateBookDao relateBookDao = AppUtils.daoFactory(RelateBookDao.class);
    private static ChapterDao chapterDao = AppUtils.daoFactory(ChapterDao.class);
    private static QuestLabelWordDao questLabelWordDao = AppUtils.daoFactory(QuestLabelWordDao.class);
    private static RbookQuestDao rbookQuestDao = AppUtils.daoFactory(RbookQuestDao.class);
    private static CourceRelateUrlValueDao courceRelateUrlValueDao = AppUtils.daoFactory(CourceRelateUrlValueDao.class);


    public void process(Page page) {

        String url = page.getUrl().get();
        if (url.matches("http://www.jyeoo.com/")) {
            List<Selectable> nodes = page.getHtml().xpath("//a[contains(@href,'ques/search')]").nodes();
            for (Selectable node : nodes) {
                String name = node.xpath("/a/@title").get();
                String href = node.xpath("/a/@href").get();
                String value = node.xpath("/a/@href").regex("/([a-z0-9]+)/ques/").get();

                String term = name.substring(0, 2);
                String course = name.substring(2, 4);

                System.out.println(name + "------" + href+"----"+value+"---"+term+"---"+value);

                String  courseId="A01"+myDic.get(term)+myDic.get(course);
                CourseRelateUrlValue courseRelateUrlValue = new CourseRelateUrlValue();
                courseRelateUrlValue.setCourseId(courseId);
                courseRelateUrlValue.setUrlValue(value);
               courceRelateUrlValueDao.add(courseRelateUrlValue);


                page.addTargetRequest(href);
            }
        } else if (url.matches("http://www.jyeoo.com/.*?/ques/search.*")) {
            //科目

            Set<String > qwIdSet=new HashSet<String>(); //存储 QuestLabelWord的id
            Set<String > rbIdSet=new HashSet<String>(); //存储 relateBook的id
            //题型 难度 来源
            List<Selectable> trs = page.getHtml().xpath("//table[@class='degree']/tbody/tr").nodes();
            for (Selectable tr : trs) {
                String type = tr.xpath("/tr/th/text()").get();
                List<Selectable> as = tr.xpath("/tr/td/ul/li/a").nodes();
                for (Selectable a : as) {
                    String code = a.regex("set.*?\\(this,(\\d+)\\)", 1).get();
                    String name = a.xpath("/a/text()").get();
                    if ("0".equals(code)) {
                        continue;
                    }

                    String qwid = myDic.get(name.replaceAll(" ", ""));
                    QuestLabelWord questLabelWord = new QuestLabelWord();
                    questLabelWord.setId(qwid);
                    questLabelWord.setValue(code);
                    if (qwid != null && code != null) {
                        System.out.println("--更新----" + qwid + "---" + name + "------" + type + "-------" + code + "----" + url);
                        qwIdSet.add(qwid);
//                        questLabelWordDao.updateWord(questLabelWord);
                    } else {
                        System.out.println("--没有更新----" + name + "------" + type + "-------" + code + "---" + url);
                    }
//                    System.out.println("*----*"+qwid+"\t"+type+"\t"+name+"\t"+code);

                }

            }

            String gcInfo = grade_course_map.get(url);
            Map<String, String> bMap = new HashMap<String, String>(); //版本集合
            String uri = page.getUrl().regex("http://www.jyeoo.com/.*?/ques/").get();
            if (url.matches("http://www.jyeoo.com/english/ques/search|http://www.jyeoo.com/chinese/ques/search|http://www.jyeoo.com/english2/ques/search|http://www.jyeoo.com/chinese2/ques/search")) {
                //初中英语 、初中语文、 高中英语 、高中语文
                String grandId = myDic.get(gcInfo.split(" ")[0]);
                String siteId = myDic.get("菁优网");
                String courseId = myDic.get(gcInfo.split(" ")[1]);
                String branchId = "D00";
                String termId = "E00";
                String bookId = "F00";

                String id = siteId + grandId + courseId + branchId + termId + bookId;
                rbIdSet.add(id);
                RelateBook relateBook = new RelateBook();
                relateBook.setId(id);
                relateBook.setGradeId(grandId);
                relateBook.setSiteId(siteId);
                relateBook.setCourseId(courseId);
                relateBook.setBranchId(branchId);
                relateBook.setTermId(termId);
                relateBook.setBookId(bookId);
                String catUrl = uri + "partialcategory?a=undefined&q=1&f=1";
                System.out.println(id+"-----"+siteId + "---" + grandId + "----" + courseId + "----" + branchId + "----" + termId + "----" + bookId + "----" + catUrl);
                if (grandId != null && siteId != null && courseId != null && branchId != null && termId != null && bookId != null) {
                    relateBookDao.add(relateBook);
                }
                relateMap.put(catUrl, id);
//                page.addTargetRequest(catUrl);

            } else if ("http://www.jyeoo.com/geography2/ques/search".equals(url)) {
                //高中地理
                List<Selectable> nodes = page.getHtml().xpath("//tr[@class='JYE_EDITION']//a[@data-id]").nodes();
                for (Selectable node : nodes) {
                    String banbenId = node.xpath("//a/@data-id").get();
                    String name = node.xpath("//a/text()").get();

                    System.out.println(name + "------" + banbenId);
                    bMap.put(banbenId, name);
                }
                List<Selectable> knodes = page.getHtml().xpath("//tr[@class='JYE_TYPE']").nodes();
                for (Selectable knode : knodes) {
                    String bCode = knode.xpath("/regex(tr_(\\d+),1)").get();
                    String brandId = myDic.get(bMap.get(bCode));
                    List<Selectable> bnodes = knode.xpath("/tr/td/ul/li/a").nodes();
                    for (Selectable bnode : bnodes) {
                        String pk = bnode.xpath("//a/regex('([a-zA-Z0-9]+\\-[a-zA-Z0-9]+\\-[a-zA-Z0-9]+\\-[a-zA-Z0-9]+\\-[a-zA-Z0-9]+)',1)").get();
                        String bName = bnode.xpath("/a/text()").get();
                        String catUrl = uri + "partialcategory?a=" + pk;

                        //id拼接
                        String grandId = myDic.get(gcInfo.split(" ")[0]);
                        String siteId = myDic.get("菁优网");
                        String courseId = myDic.get(gcInfo.split(" ")[1]);
                        String termId = "E00";
                        String bookId = myDic.get(bName);


                        String id = siteId + grandId + courseId + brandId + termId + bookId;
                        rbIdSet.add(id);
                        System.out.println(id+"-----"+pk + "------" + bName + "----" + bookId + "----" + brandId + "----" + catUrl + "----" + id);
                        RelateBook relateBook = new RelateBook();
                        relateBook.setId(id);
                        relateBook.setGradeId(grandId);
                        relateBook.setSiteId(siteId);
                        relateBook.setCourseId(courseId);
                        relateBook.setBranchId(brandId);
                        relateBook.setTermId(termId);
                        relateBook.setBookId(bookId);
                        if (grandId != null && siteId != null && courseId != null && brandId != null && termId != null && bookId != null) {
                            relateBookDao.add(relateBook);
                        }

                        relateMap.put(catUrl, id);
//                        page.addTargetRequest(catUrl);
                    }
                }

            } else {

                List<Selectable> nodes = page.getHtml().xpath("//tr[@class='JYE_EDITION']//a[@data-id]").nodes();
                for (Selectable node : nodes) {
                    String banbenId = node.xpath("//a/@data-id").get();
                    String name = node.xpath("//a/text()").get();

                    System.out.println(name + "------" + banbenId);
                    bMap.put(banbenId, name);
                }
                List<Selectable> bas = page.getHtml().xpath("//tr[@id='tr_1']//a").nodes();
                for (Selectable ba : bas) {
                    String bkCode = ba.xpath("/a/@data-id").get();
                    String bName = ba.xpath("/a/text()").get();
                    bookMap.put(bkCode, bName);
                    System.out.println(bkCode + "************" + bName);
                }


                List<Selectable> nodes2 = page.getHtml().xpath("//tr[@class='JYE_GRADE']//a[@data-id]").nodes();
                for (Selectable node : nodes2) {
                    String bCode = node.xpath("//a/@onclick").regex("this,(\\d+),\\d+,\\d+").get();
                    String uuid = node.xpath("//a/@onclick").regex("this,\\d+,\\d+,\\d+,\\'(.*?)\\'").get();
                    String name = node.xpath("//a/text()").get();


                    String catUrl = uri + "partialcategory?a=" + uuid;
                    System.out.println(name + "------" + bMap.get(bCode) + "------------" + uuid + "----------" + catUrl);

                    //id拼接
                    String grandId = myDic.get(gcInfo.split(" ")[0]);
                    String siteId = myDic.get("菁优网");
                    String courseId = myDic.get(gcInfo.split(" ")[1]);
                    String branchId = myDic.get(bMap.get(bCode));
                    String termId = myDic.get(name);
                    String bookId = null;
                    if (gcInfo.split(" ")[0].equals("高中")) {
                        bookId = myDic.get(bookMap.get(bCode));
                    } else {
                        bookId = "F00";
                    }

                    String id = siteId + grandId + courseId + branchId + termId + bookId;
                    rbIdSet.add(id);
                    if (branchId == null || termId == null) {
                        System.out.println(bMap.get(bCode) + "---漏掉----" + termId);
                    }
                    if (grandId != null && siteId != null && courseId != null && branchId != null && termId != null && bookId != null) {

                        RelateBook relateBook = new RelateBook();
                        relateBook.setId(id);
                        relateBook.setGradeId(grandId);
                        relateBook.setSiteId(siteId);
                        relateBook.setCourseId(courseId);
                        relateBook.setBranchId(branchId);
                        relateBook.setTermId(termId);
                        relateBook.setBookId(bookId);
                        relateBookDao.add(relateBook);

                        relateMap.put(catUrl, id);
//                        page.addTargetRequest(catUrl);
                    }
                    System.out.println(id+"-----"+siteId + "--**-" + grandId + "----" + courseId + "----" + branchId + "----" + termId + "----" + bookId);
                }
            }

            //存储该页大选项和小选项之间的关系
            for (String rbid : rbIdSet) {
                for (String qwid : qwIdSet) {
                    RbookQuest rbookQuest = new RbookQuest();
                    rbookQuest.setRelateBookId(rbid.split("D")[0]);
                    rbookQuest.setQid(qwid);
                    rbookQuestDao.add(rbookQuest);
                }
            }


        } else if (url.matches("http://www.jyeoo.com/.*?/ques/partialcategory.*")) {
            //各个版本
            String rawText = page.getRawText();
            Document doc = Jsoup.parseBodyFragment(rawText);
            String relateBookId = relateMap.get(url);
            if (url.matches("http://www.jyeoo.com/.*?/ques/partialcategory\\?a\\=undefined\\&q\\=1\\&f\\=1")) {
                Elements lis = doc.select("ul.treeview>li");
                int i = 1;
                for (Element li : lis) {
                    //一级
                    String name1 = li.select("li>a").first().text();
                    String pk1 = "_";
                    Elements lis2 = li.select("li>a[onclick^=openThisTreeNode]+ul>li");
                    System.out.println(name1 + "--一级----" + pk1);

                    String id = String.format("G%02d", i);
                    i++;
                    int level = 1;
                    int knowledgeTag = 0;

                    Chapter chapter = new Chapter();
                    chapter.setId(id);
                    chapter.setRelateBookId(relateBookId);
                    chapter.setName(name1);
                    chapter.setLevel(level);
                    chapter.setPk(pk1);
                    chapter.setKnowledgeTag(knowledgeTag);
                    System.out.println("*****" + chapter.toString());
                    chapterDao.add(chapter);

                    int j = 1;
                    for (Element li2 : lis2) {
                        //二级
                        String name2 = li2.select("li>a").first().text();
                        String pk2 = li2.select("li>a").attr("pk");
                        System.out.println(name2 + "---二级--" + pk2);


                        id = String.format("G%02d", i) + String.format(".%02d", j);
                        j++;
                        level = 2;
                        knowledgeTag = 0;

                        chapter = new Chapter();
                        chapter.setId(id);
                        chapter.setRelateBookId(relateBookId);
                        chapter.setName(name2);
                        chapter.setLevel(level);
                        chapter.setPk(pk2);
                        chapter.setKnowledgeTag(knowledgeTag);
                        chapterDao.add(chapter);

                        Elements lis3 = li2.select("li>a[onclick^=nodeClick]+ul>li");
                        int k = 1;
                        for (Element li3 : lis3) {
                            String name3 = li3.select("li>a").first().text();
                            String pk3 = li3.select("li>a").attr("pk");
                            System.out.println(name3 + "---三级----" + pk3);

                            id = String.format("G%02d", i) + String.format(".%02d", j) + String.format(".%02d", k);
                            k++;
                            level = 3;
                            knowledgeTag = 1;

                            chapter = new Chapter();
                            chapter.setId(id);
                            chapter.setRelateBookId(relateBookId);
                            chapter.setName(name3);
                            chapter.setLevel(level);
                            chapter.setPk(pk3);
                            chapter.setKnowledgeTag(knowledgeTag);
                            chapterDao.add(chapter);
                        }
                    }
                }
            } else {
                Elements lis = doc.select("ul.treeview>li");

                List<ZBean> zBeans = new ArrayList<ZBean>();
                int i = 1;
                for (Element li : lis) {
                    String zhText = li.select("li>a").first().text();
                    String zpk = li.select("li>a[pk$=~]").first().attr("pk").split("~")[0];
                    Elements lis2 = li.select("li>a[pk$=~]:contains(章)+ul>li");
                    System.out.println(zpk + "-------章节---------" + zhText);

                    ZBean zBean = new ZBean();
                    zBean.setName(zhText);
                    zBean.setPk(zpk);

                    String id = String.format("G%02d", i);
                    i++;
                    int level = 1;
                    int knowledgeTag = 0;

                    Chapter chapter = new Chapter();
                    chapter.setId(id);
                    chapter.setRelateBookId(relateBookId);
                    chapter.setName(zhText);
                    chapter.setLevel(level);
                    chapter.setPk(zpk);
                    chapter.setKnowledgeTag(knowledgeTag);
                    chapterDao.add(chapter);


                    int j = 1;
                    for (Element element : lis2) {
                        String pk = element.select("li>a").attr("pk");
                        String name = element.select("li>a").first().text();

                        JBean jBean = new JBean();
                        jBean.setPk(pk);
                        jBean.setName(name);

                        id = String.format("G%02d", i) + String.format(".%02d", j);
                        j++;
                        level = 2;
                        knowledgeTag = 0;

                        chapter = new Chapter();
                        chapter.setId(id);
                        chapter.setRelateBookId(relateBookId);
                        chapter.setName(name);
                        chapter.setLevel(level);
                        chapter.setPk(pk);
                        chapter.setKnowledgeTag(knowledgeTag);
                        chapterDao.add(chapter);

                        System.out.println(pk + "------节点------" + name);
                        Elements lis3 = element.select("li>ul>li");
                        int k = 1;
                        for (Element element1 : lis3) {
                            String pk1 = element1.select("li>a").attr("pk");
                            String zname = element1.select("li>a").first().text();
                            if (pk1.endsWith("~")) {


                                Elements as3 = element1.select("li>ul>li>a");
                                System.out.println(pk1 + "------二级节点------" + zname);

                                JBean jBean2 = new JBean();
                                jBean2.setPk(pk1);
                                jBean2.setName(zname);

                                id = String.format("G%02d", i) + String.format(".%02d", j) + String.format(".%02d", k);
                                k++;
                                level = 3;
                                knowledgeTag = 0;

                                chapter = new Chapter();
                                chapter.setId(id);
                                chapter.setRelateBookId(relateBookId);
                                chapter.setName(zname);
                                chapter.setLevel(level);
                                chapter.setPk(pk1);
                                chapter.setKnowledgeTag(knowledgeTag);
                                chapterDao.add(chapter);

                                int n = 1;
                                for (Element element2 : as3) {
                                    String pk2 = element2.attr("pk");
                                    String name2 = element2.text();
                                    System.out.println(pk2 + "------知识点-------" + name2);

                                    ZSBean zsBean = new ZSBean();
                                    zsBean.setPk(pk2);
                                    zsBean.setName(name2);
                                    jBean2.addZsBean(zsBean);

                                    id = String.format("G%02d", i) + String.format(".%02d", j) + String.format(".%02d", k) + String.format(".%02d", n);
                                    n++;
                                    level = 4;
                                    knowledgeTag = 1;

                                    chapter = new Chapter();
                                    chapter.setId(id);
                                    chapter.setRelateBookId(relateBookId);
                                    chapter.setName(name2);
                                    chapter.setLevel(level);
                                    chapter.setPk(pk2);
                                    chapter.setKnowledgeTag(knowledgeTag);
                                    chapterDao.add(chapter);

                                    String uri = page.getUrl().regex("http://www.jyeoo.com/.*?/ques/").get();
                                    String nUrl = uri + "partialques?q=" + pk2;
//                               page.addTargetRequest(nUrl);
                                }
                                jBean.addJBeans2(jBean2);
                            } else {
                                System.out.println(pk1 + "------知识点------" + zname);
                                ZSBean zsBean = new ZSBean();
                                zsBean.setPk(pk1);
                                zsBean.setName(zname);

                                jBean.addZsBean(zsBean);

                                id = String.format("G%02d", i) + String.format(".%02d", j) + String.format(".%02d", k);
                                k++;
                                level = 3;
                                knowledgeTag = 1;

                                chapter = new Chapter();
                                chapter.setId(id);
                                chapter.setRelateBookId(relateBookId);
                                chapter.setName(zname);
                                chapter.setLevel(level);
                                chapter.setPk(pk1);
                                chapter.setKnowledgeTag(knowledgeTag);
                                chapterDao.add(chapter);

                                String uri = page.getUrl().regex("http://www.jyeoo.com/.*?/ques/").get();
                                String nUrl = uri + "partialques?q=" + pk1;
//                            page.addTargetRequest(nUrl);
                            }

                        }
                        zBean.addJbean(jBean);
                    }
                    zBeans.add(zBean);

                }
                System.out.println(zBeans);

                String jsonStr = JsonUtils.obj2Str(zBeans);
                System.out.println(jsonStr);
            }


        }
        if (url.matches("http://www.jyeoo.com/.*?/ques/partialques.*")) {
            //题目
            System.out.println(page.getUrl().get() + "--------------Turl");
        }


//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }


    public Site getSite() {

        myDic = readDic();
        grade_course_map = readGradecourse();
        return Site
                .me()
                .setUserAgent(userAgent)
                ;

    }

    public static void main(String[] args) {
        String url = "http://www.jyeoo.com/math/ques/search"; //科目
        url = "http://www.jyeoo.com/";
//        url = "http://www.jyeoo.com/physics/ques/partialcategory?a=79fb5dfa-9ea4-4476-a8e9-e56db096a949"; //版本年级
//        url = "http://www.jyeoo.com/math/ques/search"; //初中数学
//        url="http://www.jyeoo.com/physics/ques/search"; //初中物理
//        url="http://www.jyeoo.com/chemistry/ques/search"; //初中化学
//        ur="http://www.jyeoo.com/bio/ques/searc"; //初中生物
//        url="http://www.jyeoo.com/geography/ques/search"; //初中地理
//        url="http://www.jyeoo.com/english/ques/search"; //初中英语  结构不一样
//        url = "http://www.jyeoo.com/chinese/ques/search"; //初中语文  结构不一样
//        url="http://www.jyeoo.com/politics/ques/search"; //初中政治
//        url="http://www.jyeoo.com/history/ques/search"; //初中历史
//        url="http://www.jyeoo.com/math2/ques/search"; //高中数学
//        url="http://www.jyeoo.com/physics2/ques/search"; //高中物理
//        url="http://www.jyeoo.com/chemistry2/ques/search"; //高中化学
//        url="http://www.jyeoo.com/bio2/ques/search";  //高中生物
//        url="http://www.jyeoo.com/geography2/ques/search"; //高中地理   没有年级
//        url="http://www.jyeoo.com/english2/ques/search"; //高中英语 结构不一样
//        url="http://www.jyeoo.com/chinese2/ques/search"; //高中语文 结构不一样
//        url="http://www.jyeoo.com/politics2/ques/search"; //高中政治
//        url="http://www.jyeoo.com/history2/ques/search"; //高中历史
//        url="http://www.jyeoo.com/math3/ques/search"; //小学数学

//        url="http://www.jyeoo.com/science/ques/search"; //初中科学  需要加入校园号
//        url="http://www.jyeoo.com/math0/ques/search"; //小学奥数  需要权限
//        url="http://www.jyeoo.com/";
//        url="http://www.jyeoo.com/math0/ques/search";

//        url = "http://www.jyeoo.com/english/ques/partialcategory?a=undefined&q=1&f=1";
        Spider
                .create(new Process2())
                .addUrl(url)
                .thread(5)
//                .run();
                .test(url);
    }


    /**
     * 读取编码配置
     *
     * @return
     */
    private static Map<String, String> readDic() {
        Map<String, String> dicMap = new HashMap<String, String>();
//        try {
//            List<String> list = FileUtils.readLines(new File("./Mydic.txt"), "utf-8");
//            for (String each : list) {
//                String code = each.split("\t")[0];
//                String name = each.split("\t")[1];
//                dicMap.put(name, code);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<QuestLabelWord> questLabelWords = questLabelWordDao.getAll();
        for (QuestLabelWord questLabelWord : questLabelWords) {
            String name = questLabelWord.getName();
            String id = questLabelWord.getId();
            dicMap.put(name, id);
        }
        return dicMap;
    }

    private static Map<String, String> readGradecourse() {
        Map<String, String> dicMap = new HashMap<String, String>();
        try {
            List<String> list = FileUtils.readLines(new File("./grade_course.txt"), "utf-8");
            for (String each : list) {
                String url = each.split(" ")[0];
                String grade = each.split(" ")[1];
                String course = each.split(" ")[2];
                dicMap.put(url, grade + " " + course);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dicMap;
    }
}
