package com.jk.data.process;

import com.jk.data.bean.JBean;
import com.jk.data.bean.ZBean;
import com.jk.data.bean.ZSBean;
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
 * 爬取菁优网每科知识点
 */
public class JyProcess implements PageProcessor {

    private String cat_url_tmp = "http://www.jyeoo.com/math2/ques/partialcategory?a=AA";
    private String ti_url_tmp = "http://www.jyeoo.com/physics/ques/partialques?q=AA";
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private static final  String  cat_tmp="http://www.jyeoo.com/AA/ques/partialcategory?a=undefined&q=1&f=1";

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

                System.out.println(name + "------" + href + "----" + value + "---" + term + "---" + value);

                String courseId = "A01" + myDic.get(term) + myDic.get(course);
                CourseRelateUrlValue courseRelateUrlValue = new CourseRelateUrlValue();
                courseRelateUrlValue.setCourseRelateId(courseId);
                courseRelateUrlValue.setUrlValue(value);
                courceRelateUrlValueDao.add(courseRelateUrlValue);


//                page.addTargetRequest(href);

                //只抓取网页结构
                String catUrl = cat_tmp.replace("AA", value);
                page.addTargetRequest(catUrl);

            }
        }   else if (url.matches("http://www.jyeoo.com/.*?/ques/partialcategory.*")) {
            //各个版本
            String rawText = page.getRawText();
            Document doc = Jsoup.parseBodyFragment(rawText);
            String urlValue = page.getUrl().regex("com/(.*?)/ques").get();
            String relateBookId = courceRelateUrlValueDao.getByUrlValue(urlValue).getUrlValue();
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
                            k++;
                        }
                        j++;
                    }
                    i++;
                }
            } else {
                Elements lis = doc.select("ul.treeview>li");

                List<ZBean> zBeans = new ArrayList<ZBean>();
                int i = 1;
                for (Element li : lis) {
                    String zhText = li.select("li>a").first().text();
                    String zpk = li.select("li>a[pk$=~]").first().attr("pk");
                    Elements lis2 = li.select("li>a[pk$=~]:contains(章)+ul>li");
                    System.out.println(zpk + "-------章节---------" + zhText);

                    ZBean zBean = new ZBean();
                    zBean.setName(zhText);
                    zBean.setPk(zpk);

                    String id = String.format("G%02d", i);

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
                        Elements lis3 = element.select("li>a:contains(节)+ul>li");
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

                                    if ("1Z：合数分解质因数".equals(name2)) {
                                        System.out.println("bbbbbbbbbbbbbb" + name2);
                                    }

                                    System.out.println(pk2 + "------知识点-------" + name2);

                                    ZSBean zsBean = new ZSBean();
                                    zsBean.setPk(pk2);
                                    zsBean.setName(name2);
                                    jBean2.addZsBean(zsBean);

                                    id = String.format("G%02d", i) + String.format(".%02d", j) + String.format(".%02d", k) + String.format(".%02d", n);

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
                                    n++;
//                               page.addTargetRequest(nUrl);
                                }
                                jBean.addJBeans2(jBean2);
                            } else {

                                if ("1Z：合数分解质因数".equals(zname)) {
                                    System.out.println("aaaaaaaaaaaaaaa" + zname);
                                }

                                System.out.println(pk1 + "------知识点------" + zname);
                                ZSBean zsBean = new ZSBean();
                                zsBean.setPk(pk1);
                                zsBean.setName(zname);

                                jBean.addZsBean(zsBean);

                                id = String.format("G%02d", i) + String.format(".%02d", j) + String.format(".%02d", k);
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

                            k++;
                        }
                        zBean.addJbean(jBean);
                        j++;
                    }
                    zBeans.add(zBean);

                    i++;
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

//        url="http://www.jyeoo.com/math3/ques/partialcategory?a=b9f9287f-8348-4b95-8c06-8bdcfc559928&q=b9f9287f-8348-4b95-8c06-8bdcfc559928~f1483a4d-66cd-4fb9-8ca4-68a0d7ddc7d7~&f=0&r=0.2692786993780121";
         url="http://www.jyeoo.com/english/ques/partialcategory?a=undefined&q=1&f=1";

        Spider
                .create(new JyProcess())
                .addUrl(url)
                .thread(5)
                .run();
//                .test(url);
    }


    /**
     * 读取编码配置
     *
     * @return
     */
    private static Map<String, String> readDic() {
        Map<String, String> dicMap = new HashMap<String, String>();

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
