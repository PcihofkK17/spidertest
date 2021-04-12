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

    private String cat_url_tmp = "http://www.jyeoo.com/physics/ques/partialcategory?a=AA";
    private String ti_url_tmp = "http://www.jyeoo.com/physics/ques/partialques?q=AA";

    public void process(Page page) {
        String url = page.getUrl().get();
        if (url.matches("http://www.jyeoo.com/.*?/ques/search.*")) {
            //科目
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

                String catUrl = cat_url_tmp.replace("AA", uuid);
                System.out.println(name + "------" + bMap.get(bCode) + "------------" + uuid + "----------" + catUrl);
                page.addTargetRequest(catUrl);
            }
        } else if (url.matches("http://www.jyeoo.com/.*?/ques/partialcategory.*")) {
            //各个版本
            String rawText = page.getRawText();
            Document doc = Jsoup.parseBodyFragment(rawText);
            Elements lis = doc.select("ul.treeview>li");

            List<ZBean> zBeans=new ArrayList<ZBean>();
            for (Element li : lis) {
                String zhText = li.select("li>a").first().text();
                String zpk = li.select("li>a[pk$=~]").first().attr("pk").split("~")[0];
                Elements lis2 = li.select("li>a[pk$=~]:contains(章)+ul>li");
                System.out.println(zpk + "-------章节---------" + zhText);

                ZBean zBean=new ZBean();
                zBean.setName(zhText);
                zBean.setPk(zpk);

                for (Element element : lis2) {
                    String pk = element.select("li>a").attr("pk");
                    String name = element.select("li>a").first().text();

                    JBean jBean=new JBean();
                    jBean.setPk(pk);
                    jBean.setName(name);

                    Elements lis3 = element.select("li>ul>li>a");
                    for (Element element1 : lis3) {
                        String pk1 = element1.attr("pk");
                        String zname = element1.text();
                        System.out.println(pk1+"------知识点------"+zname);

                        ZSBean zsBean=new ZSBean();
                        zsBean.setPk(pk1);
                        zsBean.setName(zname);

                        jBean.addZsBean(zsBean);
                    }
                    zBean.addJbean(jBean);
                }
                zBeans.add(zBean);

            }
            System.out.println(zBeans);

            String jsonStr = JsonUtils.obj2Str(zBeans);
            System.out.println(jsonStr);
        }
        if (url.matches("http://www.jyeoo.com/math3/ques/partialques.*")) {
            //题目
            System.out.println(page.getUrl().get() + "--------------Turl");
        }


    }


    public Site getSite() {
        return Site.me();
    }

    public static void main(String[] args) {
//       String  url="http://www.jyeoo.com/math3/ques/partialcategory?a=024b8319-d250-46b6-84c6-10121cd6b770&q=024b8319-d250-46b6-84c6-10121cd6b770~3709c351-62c9-4d52-9678-028662a78fc0~";//从章节列表借口
        String url = "http://www.jyeoo.com/physics/ques/partialcategory?a=79fb5dfa-9ea4-4476-a8e9-e56db096a949"; //二级章节
//       String url="http://www.jyeoo.com/physics/ques/search"; //物理
//        String  url="http://www.jyeoo.com/math3/ques/search?f=0"; //科目
        Spider.create(new Process2())
                .test(url);
    }
}
