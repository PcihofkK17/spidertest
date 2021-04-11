package com.jd.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 76204 on 2017/6/29.
 */
public class Process1 implements PageProcessor {

    private String  cat_url_tmp="http://www.jyeoo.com/math3/ques/partialcategory?a=AA";
    private String  ti_url_tmp="http://www.jyeoo.com/math3/ques/partialques?q=AA";
    public void process(Page page) {
        String url = page.getUrl().get();
        if(url.matches("http://www.jyeoo.com/math3/ques/search.*")){
            //科目
            Map<String , String > bMap=new HashMap<String, String>();
            List<Selectable> nodes = page.getHtml().xpath("//tr[@class='JYE_EDITION']//a[@data-id]").nodes();
            for (Selectable node : nodes) {
                String banbenId = node.xpath("//a/@data-id").get();
                String name = node.xpath("//a/text()").get();

                System.out.println(name+"------"+banbenId);
                bMap.put(banbenId,name);
            }
            List<Selectable> nodes2 = page.getHtml().xpath("//tr[@class='JYE_GRADE']//a[@data-id]").nodes();
            for (Selectable node : nodes2) {
                String bCode = node.xpath("//a/@onclick").regex("this,(\\d+),\\d+,\\d+").get();
                String uuid = node.xpath("//a/@onclick").regex("this,\\d+,\\d+,\\d+,\\'(.*?)\\'").get();
                String name = node.xpath("//a/text()").get();

                System.out.println(name+"------"+bMap.get(bCode)+"------------"+uuid);
                String catUrl = cat_url_tmp.replace("AA", uuid);
                page.addTargetRequest(catUrl);
            }
        }else if(url.matches("http://www.jyeoo.com/math3/ques/partialcategory.*")){
            //各个版本
            String rawText = page.getRawText();
            Document doc = Jsoup.parseBodyFragment(rawText);
            Elements lis = doc.select("ul.treeview>li");

            for (Element li : lis) {
                String zhText = li.select("li>a").first().text();
                Elements as = li.select("li>ul>li>a");
                if(as.size()==0){
                    continue;
                }
                System.out.println(zhText);

                for (Element a : as) {
                    String pk = a.attr("pk");
                    String name = a.text();
                    String tUrl = ti_url_tmp.replace("AA", pk);
                    System.out.println(pk+"-------------"+name+"------"+tUrl);
                    page.addTargetRequest(tUrl);
                }

            } if(url.matches("http://www.jyeoo.com/math3/ques/partialques.*")){
                //题目
                System.out.println(page.getUrl().get()+"--------------Turl");
            }

        }

        
    }

    public Site getSite() {
        return Site.me();
    }

    public static void main(String[] args) {
       String  url="http://www.jyeoo.com/math3/ques/partialcategory?a=024b8319-d250-46b6-84c6-10121cd6b770&q=024b8319-d250-46b6-84c6-10121cd6b770~3709c351-62c9-4d52-9678-028662a78fc0~";//从章节列表借口
//        String  url="http://www.jyeoo.com/math3/ques/search?f=0"; //科目
        Spider.create(new Process1())
                .test(url);
    }
}
