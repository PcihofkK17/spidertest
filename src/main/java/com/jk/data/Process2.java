package com.jk.data;

import com.jk.data.com.jk.data.util.JsonUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 76204 on 2017/6/29.
 */
public class Process2 implements PageProcessor {

    private String cat_url_tmp = "http://www.jyeoo.com/math2/ques/partialcategory?a=AA";
    private String ti_url_tmp = "http://www.jyeoo.com/physics/ques/partialques?q=AA";
    private static final String  userAgent="Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    public void process(Page page) {
        String url = page.getUrl().get();
        if(url.matches("http://www.jyeoo.com/")){
            List<Selectable> nodes = page.getHtml().xpath("//a[contains(@href,'ques/search')]").nodes();
            for (Selectable node : nodes) {
                String name = node.xpath("/a/@title").get();
                String href = node.xpath("/a/@href").get();
                System.out.println(name+"------"+href);
                page.addTargetRequest(href);
            }
        }else  if (url.matches("http://www.jyeoo.com/.*?/ques/search.*")) {
            //科目
//            System.out.println(page.getRawText());
            Map<String, String> bMap = new HashMap<String, String>();
            List<Selectable> nodes = page.getHtml().xpath("//tr[@class='JYE_EDITION']//a[@data-id]").nodes();
            for (Selectable node : nodes) {
                String banbenId = node.xpath("//a/@data-id").get();
                String name = node.xpath("//a/text()").get();

                System.out.println(name + "------" + banbenId);
                bMap.put(banbenId, name);
            }
            List<Selectable> nodes2 = page.getHtml().xpath("//tr[@class='JYE_GRADE']//a[@data-id]").nodes();
            for (Selectable node : nodes2) {
                String bCode = node.xpath("//a/@onclick").regex("this,(\\d+),\\d+,\\d+").get();
                String uuid = node.xpath("//a/@onclick").regex("this,\\d+,\\d+,\\d+,\\'(.*?)\\'").get();
                String name = node.xpath("//a/text()").get();


                String uri = page.getUrl().regex("http://www.jyeoo.com/.*?/ques/").get();
               String   catUrl=uri+"/partialcategory?a="+uuid;
//                String catUrl = cat_url_tmp.replace("AA", uuid);
                System.out.println(name + "------" + bMap.get(bCode) + "------------" + uuid + "----------" + catUrl);
                page.addTargetRequest(catUrl);
            }
        } else if (url.matches("http://www.jyeoo.com/.*?/ques/partialcategory.*")) {
            //各个版本
            String rawText = page.getRawText();
            Document doc = Jsoup.parseBodyFragment(rawText);
            Elements lis = doc.select("ul.treeview>li");

            List<ZBean> zBeans = new ArrayList<ZBean>();
            for (Element li : lis) {
                String zhText = li.select("li>a").first().text();
                String zpk = li.select("li>a[pk$=~]").first().attr("pk").split("~")[0];
                Elements lis2 = li.select("li>a[pk$=~]:contains(章)+ul>li");
                System.out.println(zpk + "-------章节---------" + zhText);

                ZBean zBean = new ZBean();
                zBean.setName(zhText);
                zBean.setPk(zpk);


                for (Element element : lis2) {
                    String pk = element.select("li>a").attr("pk");
                    String name = element.select("li>a").first().text();

                    JBean jBean = new JBean();
                    jBean.setPk(pk);
                    jBean.setName(name);


                    System.out.println(pk + "------节点------" + name);
                    Elements lis3 = element.select("li>ul>li");
                    for (Element element1 : lis3) {
                        String pk1 = element1.select("li>a").attr("pk");
                        String zname = element1.select("li>a").first().text();
                        if (pk1.endsWith("~")) {


                            Elements as3 = element1.select("li>ul>li>a");
                            System.out.println(pk1 + "------二级节点------" + zname);

                            JBean jBean2 = new JBean();
                            jBean2.setPk(pk1);
                            jBean2.setName(zname);
                            for (Element element2 : as3) {
                                String pk2 = element2.attr("pk");
                                String name2 = element2.text();
                                System.out.println(pk2 + "------知识点-------" + name2);

                                ZSBean zsBean = new ZSBean();
                                zsBean.setPk(pk2);
                                zsBean.setName(zname);
                                jBean2.addZsBean(zsBean);

                                String uri = page.getUrl().regex("http://www.jyeoo.com/.*?/ques/").get();
                               String  nUrl= uri+"partialques?q="+pk2;
                               page.addTargetRequest(nUrl);
                            }
                            jBean.addJBeans2(jBean2);
                        } else {
                            System.out.println(pk1 + "------知识点------" + zname);
                            ZSBean zsBean = new ZSBean();
                            zsBean.setPk(pk1);
                            zsBean.setName(zname);

                            jBean.addZsBean(zsBean);

                            String uri = page.getUrl().regex("http://www.jyeoo.com/.*?/ques/").get();
                            String  nUrl= uri+"partialques?q="+pk1;
                            page.addTargetRequest(nUrl);
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
        if (url.matches("http://www.jyeoo.com/.*?/ques/partialques.*")) {
            //题目
            System.out.println(page.getUrl().get() + "--------------Turl");
        }


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Site getSite() {
        return Site
                .me()
                .setUserAgent(userAgent)
                ;

    }

    public static void main(String[] args) {
        String url = "http://www.jyeoo.com/math2/ques/search"; //科目
        url = "http://www.jyeoo.com/physics/ques/partialcategory?a=79fb5dfa-9ea4-4476-a8e9-e56db096a949"; //版本年级
//        url="http://www.jyeoo.com/";
//        url="http://www.jyeoo.com/math0/ques/search";
        Spider
                .create(new Process2())
                .addUrl(url)

                .run();
//                .test(url);
    }
}
