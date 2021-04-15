package com.jk.data.mybatis;

import com.jk.data.mybatis.bean.User;
import com.jk.data.mybatis.dao.UserDao;

/**
 * Created by 76204 on 2017/7/3.
 */
public class DaoTest {
    public static void main(String[] args) {
        UserDao userDao = AppUtils.daoFactory(UserDao.class);
        User user=new User();
        user.setName("bob");
        userDao.add(user);

//        System.out.println(AppUtils.class.getResource("/applicationContext-myBatis.xml").toString());
    }
}
