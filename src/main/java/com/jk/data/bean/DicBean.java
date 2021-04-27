package com.jk.data.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 76204 on 2017/7/15.
 */
public class DicBean {

    private Map<String , Map<String,String>> totalMap=new HashMap();
    private Map<String ,String > dataMap=new HashMap<>();

    public void set(String tag,String  key,String value){
        dataMap.clear();
        dataMap.put(key,value);
        totalMap.put(tag,dataMap);
    }

    public String  get(String tag,String key){
        return totalMap.get(tag).get(key);
    }
}
