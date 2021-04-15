package com.jk.data.mybatis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppUtils {
    	private static final  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/applicationContext*.xml");
//    private static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext(AppUtils.class.getResource("/applicationContext-myBatis.xml").toString());

    public static <T> T daoFactory(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}