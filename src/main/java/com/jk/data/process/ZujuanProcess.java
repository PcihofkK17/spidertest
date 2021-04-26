package com.jk.data.process;

import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.QuestLabelWord;
import com.jk.data.mybatis.bean.RelateBook;
import com.jk.data.mybatis.dao.QuestLabelWordDao;
import com.jk.data.util.JsonUtils;
import com.jk.data.util.RedisUtils;
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
    private Map<String, String> myDic = new HashMap<String, String>();

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        if(url.matches("http://www.zujuan.com/")){
            //总页面
            List<Selectable> divs = page.getHtml().xpath("//div[@class='item-list']/div").nodes();
            for (Selectable div : divs) {
                String term = div.xpath("/div/h3/text()").get();
                if(myDic.get(term)==null){
                    System.out.println("---年段漏掉----"+term);
                }

                List<Selectable> as = div.xpath("/div/a").nodes();
                for (Selectable a : as) {
                    String name = a.xpath("/a/text()").get();

                    String href = a.links().all().get(0).replace("knowledge","category");
                    System.out.println(term+"-----"+name+"-----"+href);
                    page.addTargetRequest(href);


                    String chid=null;
                    String xd =null;
                    if(myDic.get(name)==null){
                        System.out.println("----科目漏掉----"+name);

                    }else{
                        chid = a.xpath("/a/@href").regex("chid=(\\d+)").get(); //科目编号
                        xd = a.xpath("/a/@href").regex("xd=(\\d+)").get(); //学段编号号

                        //kv缓存
                        RedisUtils.set("B"+xd,term);  //学段
                        RedisUtils.set("C"+chid,name); //学科
                    }
                }
            }
        }else if(url.matches("http://www.zujuan.com/question\\?chid=\\d+\\&xd=\\d+\\&tree_type=category")){
            //学科

            //大选项
            List<Selectable> divs1 = page.getHtml().xpath("//div[@class='search-type d-search-type g-container']//div[@class='type-items']").nodes();

            for (Selectable div : divs1) {
                String type = div.xpath("//div[@class='type-tit']/text()").get();
                List<Selectable> as = div.xpath("//div[@class='con-items']/a").nodes();
                for (Selectable a : as) {
                    String data_bcaid = a.xpath("/a/@data-bcaid").get();
                    String name = a.xpath("/a/text()").get();



                    String href = a.links().all().get(0);
                    if(type.contains("教材")){
                        page.addTargetRequest(href);
                    }
                    if(myDic.get(name)==null){
                        System.out.println(type+"---"+name+"---漏掉");
                    }else{
                        String code = myDic.get(name);

                        //kv缓存
                        RedisUtils.set(data_bcaid,name);
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
                        System.out.println(type+"----选项漏掉----"+name);
                    }
                }
            }

        }else if(url.matches("http://www.zujuan.com/question\\?bookversion=\\d+\\&chid=\\d+\\&xd=\\d+")){
            //教材
            List<Selectable> as = page.getHtml().xpath("//div[@class='con-items']/a").nodes();
            for (Selectable a : as) {
                String data_bcaid = a.xpath("/a/@data-bcaid").get();
                String name = a.xpath("/a/text()").get(); //年级
                String href = a.links().all().get(0);
                page.addTargetRequest(href);
            }



        }else if(url.matches("http://www.zujuan.com/question\\?categories=\\d+\\&bookversion=\\d+\\&nianji=\\d+\\&chid=\\d+\\&xd=\\d+")){
            //年级
            String xd = page.getUrl().regex("xd=(\\d+)").get(); //学段  B
            String bookversion = page.getUrl().regex("bookversion=(\\d+)").get(); //版本  D
            String nianji = page.getUrl().regex("nianji=(\\d+)").get(); //年级E 或者 课本 F
            String chid = page.getUrl().regex("chid=(\\d+)").get(); //学科  C

            String xdName = RedisUtils.get(xd);
            String bookversionName = RedisUtils.get(bookversion);
            String nianjiName = RedisUtils.get(nianji);
            String chidName = RedisUtils.get(chid);

            String xdCode = myDic.get(xdName);
            String bookversionCode = myDic.get(bookversionName);
            String nianjiNameCode = myDic.get(nianjiName);
            String chidCode = myDic.get(chidName);

            RelateBook relateBook =null;
             String  id=null;
             String  siteId=null; //A
             String  gradeId=null; //B
             String  courseId=null; //C
             String  branchId=null; //D
             String  termId=null; //E
             String  bookId=null; //F

             siteId=myDic.get("组卷网");
             gradeId=xdCode;
            courseId=chidCode;
            branchId=bookversionCode;



            if(nianji.startsWith("E")){
                bookId="F00";
                termId=nianjiNameCode;
            }else{
                bookId=nianjiNameCode;
                termId="E00";
            }

            id = siteId + gradeId + courseId + branchId + termId + bookId;

            System.out.println("id::::::"+id);


            List<Selectable> as = page.getHtml().xpath("//div[@id='J_Tree']/div/a").nodes();
            for (Selectable a : as) {
                String catid = a.xpath("/a/@href").regex("categories=(\\d+)").get();
                String name = a.xpath("/a/text()").get();
                System.out.println(catid+"----"+name);
                String href = cat_tmp.replace("AA", catid);
                page.addTargetRequest(href);

                RedisUtils.set(catid,id);  //kv 缓存
            }
        }else if(url.matches("http://www.zujuan.com/question/tree\\?id=\\d+&type=category")){
            String pId = page.getUrl().regex("id=(\\d+)").get();
            String relatebookId = RedisUtils.get(pId);
            String  jsonStr=page.getRawText();
            List<Map<String ,Object>> list=  (List<Map<String ,Object>>) JsonUtils.jsonStr2Obj(jsonStr,List.class);
            System.out.println(list);
            for (Map<String, Object> stringObjectMap : list) {
                Object id = stringObjectMap.get("id");

                Object title = stringObjectMap.get("title");
                Boolean hasChild =(Boolean) stringObjectMap.get("hasChild");

                if(hasChild){
                    String href = cat_tmp.replace("AA", id.toString());
                    RedisUtils.set(id.toString(),relatebookId);  //kv 缓存
                    page.addTargetRequest(href);
                }



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
//        url="http://www.zujuan.com/question?chid=3&xd=3&tree_type=category";
//        url="http://www.zujuan.com/question?categories=6137&bookversion=10892&nianji=6137&chid=2&xd=3";
//        url="http://www.zujuan.com/question/tree?id=8975&type=category";

        url="http://www.zujuan.com/question?categories=22688&bookversion=14773&nianji=22688&chid=7&xd=3";
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
