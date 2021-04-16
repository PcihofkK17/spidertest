package com.jk.data.mybatis;

import com.jk.data.mybatis.bean.Chapter;
import com.jk.data.mybatis.bean.User;
import com.jk.data.mybatis.dao.ChapterDao;
import com.jk.data.mybatis.dao.UserDao;

/**
 * Created by 76204 on 2017/7/3.
 */
public class DaoTest {
    public static void main(String[] args) {
//        UserDao userDao = AppUtils.daoFactory(UserDao.class);
//        User user=new User();
//        user.setName("bob");
//        userDao.add(user);

        ChapterDao chapterDao = AppUtils.daoFactory(ChapterDao.class);
        Chapter chapter=new Chapter();
        chapter.setPk("a");
        chapter.setLevel(1);
        chapter.setName("bb");
        chapter.setRelateBookId("aaaaa");
        chapter.setId("aaaaaa");
        chapter.setKnowledgeTag(0);
        chapterDao.add(chapter);

//        System.out.println(AppUtils.class.getResource("/applicationContext-myBatis.xml").toString());
    }
}
