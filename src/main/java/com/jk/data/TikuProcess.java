package com.jk.data;

import com.jk.data.bean.TObject;
import com.jk.data.bean.TikuKonwBean;
import com.jk.data.mybatis.AppUtils;
import com.jk.data.mybatis.bean.TiKuTermCourse;
import com.jk.data.mybatis.bean.TikuKnowNum;
import com.jk.data.mybatis.bean.TikuStructBean;
import com.jk.data.com.jk.data.util.JsonUtils;
import com.jk.data.mybatis.dao.TikuKnowNumDao;
import com.jk.data.mybatis.dao.TikuStructBeanDao;
import com.jk.data.mybatis.dao.TikuTermCourseDao;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 76204 on 2017/7/5.
 */
public class TikuProcess implements PageProcessor {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private  TikuKnowNumDao tikuKnowNumDao = AppUtils.daoFactory(TikuKnowNumDao.class);
    private TikuStructBeanDao tikuStructBeanDao = AppUtils.daoFactory(TikuStructBeanDao.class);
    private TikuTermCourseDao tikuTermCourseDao = AppUtils.daoFactory(TikuTermCourseDao.class);

    int count=1;
    public void process(Page page) {




        String url = page.getUrl().get();
        if(url.matches("http://www.tiku.com/testPaper.html\\?sct\\=1|http://www.tiku.com/testPaper.html\\?sct=\\d+\\&cid=\\d+")){
            //存储学科 年级关系
            if(count==1){
                List<Selectable> divs = page.getHtml().xpath("//div[@class='item-mn']").nodes();
                for (Selectable div : divs) {
                    String term = div.xpath("/div/a/text()").get();
                    List<Selectable> as = div.xpath("/div/p/a").nodes();
                    for (Selectable a : as) {
                        String dataId = a.xpath("/a/@data-id").get();
                        String course = a.xpath("/a/text()").get();

                        TiKuTermCourse tiKuTermCourse = new TiKuTermCourse();
                        tiKuTermCourse.setCourse(course);
                        tiKuTermCourse.setCourseId(dataId);
                        tiKuTermCourse.setTerm(term);
                        tikuTermCourseDao.add(tiKuTermCourse);

                        String termUrl = termTmp.replace("AA", dataId);
                        page.addTargetRequest(termUrl);
                        System.out.println(term+"---"+course+"----"+dataId+"-----"+termUrl);
                    }
                }
                count++;
            }

            List<Selectable> lis = page.getHtml().xpath("//ul[@class='m-tree']/li[@class='tree-node tree-node-0 z-open']").nodes();
            int i=1;
            for (Selectable li : lis) {
                String name1 = li.xpath("/li/a/@title").get();
                String cid1 = li.xpath("/li/a/regex(cid=(\\d+),1)").get();
                String ftid1 = li.xpath("/li/a/regex(ftid=(\\d+),1)").get();
                List<Selectable> dls = li.xpath("/li/dl").nodes();
                System.out.println("一级--------"+name1+"----"+cid1+"----"+ftid1);

                TikuStructBean tikuStructBean1= new TikuStructBean();
                tikuStructBean1.setCourseId(cid1);
                tikuStructBean1.setId(ftid1);
                tikuStructBean1.setKnowledgeName(name1);
                tikuStructBean1.setLevel(1);
                tikuStructBean1.setSortNo(i++);

                tikuStructBeanDao.add(tikuStructBean1);
                System.out.println(tikuStructBean1);

                int j=1;
                for (Selectable dl : dls) {
                    String name2 = dl.xpath("/dl/dt/a/@title").get();
                    String dataId1 = dl.xpath("/dl/dt/@data-id").get();
                    List<Selectable> dds = dl.xpath("/dl/dd").nodes();

                    String ftid2= dl.xpath("/dl/dt/a/regex(ftid=(\\d+),1)").get();
                    String sdid2= dl.xpath("/dl/dt/a/regex(sdid=(\\d+),1)").get();
                    System.out.println("二级-------"+name2+"-----"+ftid2+"----"+sdid2);

                    TikuStructBean tikuStructBean2= new TikuStructBean();
                    tikuStructBean2.setCourseId(cid1);
                    tikuStructBean2.setId(sdid2);
                    tikuStructBean2.setKnowledgeName(name2);
                    tikuStructBean2.setLevel(2);
                    tikuStructBean2.setSortNo(j++);
                    tikuStructBean2.setParentId(ftid2);

                    tikuStructBeanDao.add(tikuStructBean2);

                    System.out.println(tikuStructBean2);


                    //post 请求
                    Request request = new Request("http://www.tiku.com/service/knowledge/findKnowledgeByCourseAndParentId#"+ftid1);
                    request.setMethod(HttpConstant.Method.POST);

                    Map<String , Object> data=new HashMap<String, Object>();
                    data.put("courseId",cid1);
                    data.put("parentId",dataId1);

                    try {
                        request.setRequestBody(HttpRequestBody.form(data,"utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    page.addTargetRequest(request);
                }
            }
        }else if(url.matches("http://www.tiku.com/service/knowledge/findKnowledgeByCourseAndParentId.*")){
            TikuKonwBean tikuKonwBean = JsonUtils.jsonStr2Obj(page.getRawText(), TikuKonwBean.class);
            List<TObject> obs = tikuKonwBean.getObject();
            for (TObject ob : obs) {
                int courseId = ob.getCourseId();
                String knowledgeName = ob.getKnowledgeName();
                int parentId = ob.getParentId();
                int id = ob.getId();
                int level = ob.getLevel();
                int sortNo = ob.getSortNo();

                TikuStructBean tikuStructBean3= new TikuStructBean();
                tikuStructBean3.setCourseId(String .valueOf(courseId));
                tikuStructBean3.setId(String .valueOf(id));
                tikuStructBean3.setKnowledgeName(knowledgeName);
                tikuStructBean3.setLevel(level);
                tikuStructBean3.setSortNo(sortNo);
                tikuStructBean3.setParentId(String .valueOf(parentId));


                tikuStructBeanDao.add(tikuStructBean3);

                System.out.println(tikuStructBean3);

                System.out.println(courseId+"-----"+knowledgeName+"----"+parentId);


                String sdid = page.getUrl().regex("#(\\d+)").get();
                String tUrl = knowTmp.replace("AA", sdid).replace("BB", String.valueOf(parentId)).replace("CC", String.valueOf(id));
                System.out.println("tUrl:::::"+tUrl);
                page.addTargetRequest(tUrl);
            }
        }else  if(url.matches("http://www.tiku.com/testPaper.html\\?sct=\\d+\\&sdid=\\d+\\&tdid=\\d+\\&ftid=\\d+")){
            String numStr = page.getHtml().xpath("//li[@class='filter']/span/strong/text()").get();
            String tdid = page.getUrl().regex("tdid=(\\d+)").get();

            TikuKnowNum tikuKnowNum = new TikuKnowNum();
            tikuKnowNum.setId(tdid);
            tikuKnowNum.setNum(Integer.parseInt(numStr));
            tikuKnowNumDao.add(tikuKnowNum);
            System.out.println(tdid+"----"+numStr);
        }
    }
    private  static final String knowTmp ="http://www.tiku.com/testPaper.html?sct=1&sdid=BB&tdid=CC&ftid=AA"; //三级知识点
    private  static final String termTmp ="http://www.tiku.com/testPaper.html?sct=1&cid=AA"; //学科

    public Site getSite() {

        return Site
                .me()
                .setTimeOut(100000)
                .setUserAgent(userAgent)
                .addHeader("Cookie","JSESSIONID=72849452D2B4AF9344CDAA6961F359BD; zujuanToken=1cb031fdd7c443a489ef968f18a7390b; cookieIdName=a9b19a43bbe240c2823d79a8d25e6ec0; Hm_lvt_45be11361eb84bb96b873bd1f9ba501a=1499241152; Hm_lpvt_45be11361eb84bb96b873bd1f9ba501a=1499244484")
                ;
    }

      public static void main(String[] args) {
        String  url="http://www.tiku.com/testPaper.html?sct=1";
//        url="http://www.tiku.com/testPaper.html?sct=1&sdid=700123&tdid=700132&ftid=700122";

          url="http://www.tiku.com/testPaper.html?sct=1&cid=500018";
          Spider.create(new TikuProcess())
                 .addUrl(url)
                  .run();
        }
}
