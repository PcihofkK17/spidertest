package com.jk.data;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            List<String> list = FileUtils.readLines(new File("./test.txt"), "utf-8");
            System.out.println(list);
            Set<String> set = new HashSet<String>();
            set.addAll(list);
            System.out.println(list.size() + "-----" + set.size());
            for (String each : set) {
                System.out.println(each);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
