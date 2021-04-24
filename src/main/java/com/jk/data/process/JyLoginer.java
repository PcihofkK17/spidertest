package com.jk.data.process;

import com.jk.data.com.jk.data.util.RedisUtils;
import com.jk.data.com.jk.data.util.ThreadUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by 76204 on 2017/7/10.
 */
public class JyLoginer {
    public static void main(String[] args) {
        // 加载驱动
        System.setProperty("webdriver.firefox.bin", "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
        // 设置useragent
        String useragent = "(Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0";
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("general.useragent.override", useragent);

        WebDriver driver = new FirefoxDriver(profile);

        driver.get("http://www.jyeoo.com/account/login");
        Scanner scanner=new Scanner(System.in);
        System.out.println("begin::::按 enter 开始");
        String next = scanner.nextLine();
        System.out.println("开始自动刷新。。。");


        while (true){
            if(System.currentTimeMillis()%(1000*60*30)==0L){
                driver.navigate().refresh();
                Set<Cookie> allCookies = driver.manage().getCookies();

                Map<String, String> cookies = new HashMap<String, String>();
                for (Cookie loadedCookie : allCookies) {
                    System.out.println(String.format("%s -> %s", loadedCookie.getName(), loadedCookie.getValue()));
                    cookies.put(loadedCookie.getName(), loadedCookie.getValue());
                }
                String cookieStr = cookies.toString();
                System.out.println("刷新....."+cookieStr);

                RedisUtils.lpush("jyw_cookies", cookieStr.substring(1, cookieStr.length() - 1).replace(",", ";"));
                ThreadUtil.sleep(1000);

            }
        }



//        WebElement ele_user = driver.findElement(By.id("Email"));
//        ele_user.sendKeys("18611434755");
//        WebElement ele_pwd = driver.findElement(By.id("Password"));
//        ele_pwd.sendKeys("jycjh123");


//
//        WebElement rem = driver.findElement(By.id("Remember"));
//        rem.click();

    }
}
