package com.jk.data.process;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by 76204 on 2017/7/11.
 */
public class BasicProcess implements PageProcessor {
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
    }

    @Override
    public Site getSite() {
        return Site
                .me()
                .setUserAgent(userAgent)
                ;
    }
      public static void main(String[] args) {
        String  url="";
                  Spider
                  .create(new Process2())
                  .addUrl(url)
                  .thread(5)
                  .run();
//                .test(url);
        }
}
