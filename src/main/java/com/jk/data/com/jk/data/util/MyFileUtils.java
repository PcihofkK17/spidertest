package com.jk.data.com.jk.data.util;

import com.google.common.io.FileBackedOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by 76204 on 2017/7/11.
 */
public class MyFileUtils {
    public static void writer(String  path,String  value,boolean append){
        File file = new File(path);
        try {
            OutputStream outputStream=new FileOutputStream(file,append);
            IOUtils.write(value,outputStream,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

      public static void main(String[] args) {
              MyFileUtils.writer("c:/cjh/test.txt","aa",true);
        }
}
