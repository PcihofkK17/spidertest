package com.jk.data.util;

/**
 * Created by 76204 on 2017/7/10.
 */
public class ThreadUtil {
    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
