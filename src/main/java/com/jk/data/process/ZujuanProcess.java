package com.jk.data.process;

import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.zjChapter;
import com.jk.data.mybatis.bean.QuestLabelWord;
import com.jk.data.mybatis.bean.RbookQuest;
import com.jk.data.mybatis.bean.RelateBook;
import com.jk.data.mybatis.dao.Chapter_zujianDao;
import com.jk.data.mybatis.dao.QuestLabelWordDao;
import com.jk.data.mybatis.dao.RbookQuestDao;
import com.jk.data.mybatis.dao.RelateBookDao;
import com.jk.data.util.JsonUtils;
import com.jk.data.util.MyFileUtils;
import com.jk.data.util.RedisUtils;
import org.apache.log4j.Logger;
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
public class ZujuanProcess implements PageProcessor {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    private static Map<String,String > course_term_map=new HashMap<>();  //年级 学科


    private static QuestLabelWordDao questLabelWordDao = AppUtils.daoFactory(QuestLabelWordDao.class);
    private static Chapter_zujianDao chapterDao = AppUtils.daoFactory(Chapter_zujianDao.class);
    private static Map<String, String> myDic = new HashMap<String, String>();

    private static RelateBookDao relateBookDao = AppUtils.daoFactory(RelateBookDao.class);
    private static RbookQuestDao rbookQuestDao = AppUtils.daoFactory(RbookQuestDao.class);

    private static Logger logger = Logger.getLogger(ZujuanProcess.class);
    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        if(url.matches("http://www.zujuan.com/")){
            //总页面
            List<Selectable> divs = page.getHtml().xpath("//div[@class='item-list']/div").nodes();
            for (Selectable div : divs) {
                String term = div.xpath("/div/h3/text()").get();
                if(myDic.get(term)==null){
                    logger.info("---年段漏掉----"+term);
                }

                List<Selectable> as = div.xpath("/div/a").nodes();
                for (Selectable a : as) {
                    String name = a.xpath("/a/text()").get();

                    String href = a.links().all().get(0).replace("knowledge","category");
                    logger.info(term+"-----"+name+"-----"+href);
                    page.addTargetRequest(href);


                    String chid=null;
                    String xd =null;
                    if(myDic.get(name)==null){
                        logger.info("----科目漏掉----"+name);

                    }else{
                        chid = a.xpath("/a/@href").regex("chid=(\\d+)").get(); //科目编号
                        xd = a.xpath("/a/@href").regex("xd=(\\d+)").get(); //学段编号号

                        //kv缓存
                        RedisUtils.hmSet("B",xd,term);  //学段
                        RedisUtils.hmSet("C",chid,name); //学科
                    }
                }
            }
        }else if(url.matches("http://www.zujuan.com/question\\?chid=\\d+\\&xd=\\d+\\&tree_type=category")){
            //点击学科以后

            //大选项
            List<Selectable> divs1 = page.getHtml().xpath("//div[@class='search-type d-search-type g-container']//div[@class='type-items']").nodes();

            for (Selectable div : divs1) {
                String type = div.xpath("//div[@class='type-tit']/text()").get();
                List<Selectable> as = div.xpath("//div[@class='con-items']/a").nodes();
                for (Selectable a : as) {
                    String data_bcaid = a.xpath("/a/@data-bcaid").get();
                    String name = a.xpath("/a/text()").get();



                    String href = a.links().all().get(0);
                    String  rKey=null;
                    if(type.contains("教材")){
                        page.addTargetRequest(href);
                        rKey="jiaocai";
                    }else{
                        rKey="nianji";
                    }
                    if(myDic.get(name)==null){
                        logger.info(type+"---"+name+"---漏掉");

                        if(type.contains("教材")){
                            MyFileUtils.writer("c:/cjh/zj.txt","教材\t"+name+"\r\n",true);

                        }else{
                            MyFileUtils.writer("c:/cjh/zj.txt","年级\t"+name+"\r\n",true);
                        }

                    }else{
                        String code = myDic.get(name);

                        //kv缓存
                        RedisUtils.hmSet(rKey,data_bcaid,name);



                    }
                }
            }

            //小选项收集
            List<Selectable> divs = page.getHtml().xpath("//div[@class='tag-type']/div[@class='tag-items']").nodes();
            for (Selectable div : divs) {
                String type = div.xpath("//div[@class='tag-tit']/text()").get();
                if(type.contains("知识点个数")){
                    continue;
                }
                List<Selectable> as3 = div.xpath("//div[@class='con-items']/a").nodes();
                for (Selectable a : as3) {
                    String data_value = a.xpath("/a/@data-value").get();
                    String name = a.xpath("/a/text()").get();


                    if("全部".equals(name)){
                        continue;
                    }

                    if(myDic.get(name)==null){
                        logger.info(type+"----选项漏掉----"+name);

                    }else{
                        //relatebookquest 入库
                        String  xd = page.getUrl().regex("xd=(\\d+)").get(); //年段编号
                        String chid = page.getUrl().regex("chid=(\\d+)").get(); //科目编号

                        String  aValue=myDic.get("组卷网");
                        String bValue = myDic.get(RedisUtils.hmGet("B", xd));
                        String cValue = myDic.get(RedisUtils.hmGet("C", chid));

                        String  rbid=aValue+bValue+cValue;
                        String qid = myDic.get(name);

                        RbookQuest rbookQuest = new RbookQuest();
                        rbookQuest.setRelateBookId(rbid);
                        rbookQuest.setQid(qid);
                        rbookQuest.setValue(data_value);
                        if(rbid.contains("null")){
                            logger.error(url+"----rbid漏掉-----"+rbid);
                        }else{
                            rbookQuestDao.add(rbookQuest);
                        }
                    }
                }
            }


        }else if(url.matches("http://www.zujuan.com/question\\?bookversion=\\d+\\&chid=\\d+\\&xd=\\d+")){
            //点击教材以后
            //大选项
            List<Selectable> divs1 = page.getHtml().xpath("//div[@class='search-type d-search-type g-container']//div[@class='type-items']").nodes();

            for (Selectable div : divs1) {
                String type = div.xpath("//div[@class='type-tit']/text()").get();
                List<Selectable> as = div.xpath("//div[@class='con-items']/a").nodes();
                for (Selectable a : as) {
                    String data_bcaid = a.xpath("/a/@data-bcaid").get();
                    String name = a.xpath("/a/text()").get();



                    String href = a.links().all().get(0);
                    String  rKey=null;
                    if(type.contains("教材")){
                        rKey="jiaocai";
                    }else{
                        page.addTargetRequest(href);
                        rKey="nianji";
                    }
                    if(myDic.get(name)==null){
                        logger.info(type+"---"+name+"---漏掉");

                        if(type.contains("教材")){
                            MyFileUtils.writer("c:/cjh/zj.txt","教材\t"+name+"\r\n",true);

                        }else{
                            MyFileUtils.writer("c:/cjh/zj.txt","年级\t"+name+"\r\n",true);
                        }

                    }else{
                        String code = myDic.get(name);

                        //kv缓存
                        RedisUtils.hmSet(rKey,data_bcaid,name);

                    }
                }
            }



        }else if(url.matches("http://www.zujuan.com/question\\?categories=\\d+\\&bookversion=\\d+\\&nianji=\\d+\\&chid=\\d+\\&xd=\\d+")){
            //点击年级以后
            String xd = page.getUrl().regex("xd=(\\d+)").get();
            String bookversion = page.getUrl().regex("bookversion=(\\d+)").get();
            String nianji = page.getUrl().regex("nianji=(\\d+)").get(); //年级E 或者 课本 F
            String chid = page.getUrl().regex("chid=(\\d+)").get();

            String xdName = RedisUtils.hmGet("B",xd);
            String bookversionName = RedisUtils.hmGet("jiaocai",bookversion);
            String nianjiName = RedisUtils.hmGet("nianji",nianji);
            String chidName = RedisUtils.hmGet("C",chid);

            String xdCode = myDic.get(xdName); //学段  B
            String chidCode = myDic.get(chidName); //学科  C
            String bookversionCode = myDic.get(bookversionName); //版本  D
            String nianjiNameCode = myDic.get(nianjiName);  // E 或者 F

             String  id=null;
             String  siteId=null; //A
             String  gradeId=null; //B
             String  courseId=null; //C
             String  branchId=null; //D
             String  termId=null; //E
             String  bookId="F00"; //F

             siteId=myDic.get("组卷网");
             gradeId=xdCode;  //学段
            courseId=chidCode; //学科
            branchId=bookversionCode; //版本
            termId=nianjiNameCode;


//            if(nianji.startsWith("E")){
//                bookId="F00";
//                termId=nianjiNameCode;
//            }else{
//                bookId=nianjiNameCode;
//                termId="E00";
//            }

            id = siteId + gradeId + courseId + branchId + termId + bookId;
            if(id.contains("null")){
                logger.error(url+"---relatebookid error:::::"+id);
                return;
            }



            //relatebook 入库
            RelateBook relateBook = new RelateBook();
            relateBook.setId(id);
            relateBook.setGradeId(gradeId);
            relateBook.setSiteId(siteId);
            relateBook.setCourseId(courseId);
            relateBook.setBranchId(branchId);
            relateBook.setTermId(termId);
            relateBook.setBookId(bookId);
            relateBookDao.add(relateBook);


            List<Selectable> as = page.getHtml().xpath("//div[@id='J_Tree']/div/a").nodes();
            int i=1;
            for (Selectable a : as) {
                String catid = a.xpath("/a/@href").regex("categories=(\\d+)").get();
                String name = a.xpath("/a/text()").get();
                logger.info(catid+"----"+name+"-----"+id);
                String href = cat_tmp.replace("AA", catid);
                page.addTargetRequest(href);

                RedisUtils.hmSet("chap_rbook",catid,id); //kv 缓存


                zjChapter chapter=new zjChapter();
                chapter.setId(catid);
                chapter.setKnowledgeTag(0);
                chapter.setName(name);
                chapter.setRelateBookId(id);
                chapter.setPid(null);
                chapter.setSortNum(i++);


                chapterDao.add(chapter);
            }
        }else if(url.matches("http://www.zujuan.com/question/tree\\?id=\\d+&type=category")){
            String pId = page.getUrl().regex("id=(\\d+)").get();
            String relatebookId = RedisUtils.hmGet("chap_rbook",pId);
            String  jsonStr=page.getRawText();
            List<Map<String ,Object>> list=  (List<Map<String ,Object>>) JsonUtils.jsonStr2Obj(jsonStr,List.class);
            logger.info(list);
            int i=1;
            for (Map<String, Object> stringObjectMap : list) {
                Object id = stringObjectMap.get("id");

                Object title = stringObjectMap.get("title");
                Boolean hasChild =(Boolean) stringObjectMap.get("hasChild");

                if(hasChild){
                    String href = cat_tmp.replace("AA", id.toString());
                    RedisUtils.hmSet("chap_rbook",id.toString(),relatebookId);//kv 缓存
                    page.addTargetRequest(href);
                }

                //章节入库
                zjChapter chapter=new zjChapter();
                chapter.setId(id.toString());
                chapter.setKnowledgeTag(hasChild==false ? 1:0);
                chapter.setName(title.toString());
                chapter.setRelateBookId(relatebookId);
                chapter.setPid(pId);
                chapter.setSortNum(i++);

                chapterDao.add(chapter);
            }
        }
    }

    private static final String cat_tmp="http://www.zujuan.com/question/tree?id=AA&type=category";

    @Override
    public Site getSite() {

        myDic=readDic();
        return Site
                .me()
                .setUserAgent(userAgent)
                ;
    }

    public static void main(String[] args) {
        String url = "http://www.zujuan.com/";

//        url="http://www.zujuan.com/question?chid=11&xd=2&tree_type=category"; //科目
//        url="http://www.zujuan.com/question?bookversion=11357&chid=4&xd=3"; //教材
//        url="http://www.zujuan.com/question?categories=1993&bookversion=11356&nianji=1993&chid=4&xd=3"; //年级
//        url="http://www.zujuan.com/question/tree?id=8975&type=category";  //章节
//        url="http://www.zujuan.com/question?categories=22688&bookversion=14773&nianji=22688&chid=7&xd=3";
//        url="http://www.zujuan.com/question?categories=9893&bookversion=10431&nianji=9893&chid=3&xd=2";

//        url="http://www.zujuan.com/question?categories=1893&bookversion=11358&nianji=1893&chid=4&xd=3";

        Spider
                .create(new ZujuanProcess())
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
}
