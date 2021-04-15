package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.RelateBook;
import com.jk.data.mybatis.bean.User;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by 76204 on 2017/7/3.
 */
public interface RelateBookDao {
    @Insert("insert IGNORE  into relateBook (`id`,`siteId`,`gradeId`,`courseId`,`branchId`,`termId`,`bookId`) values (#{id},#{siteId},#{gradeId},#{courseId},#{branchId},#{termId},#{bookId})")
    public int add(RelateBook relateBook);
}
