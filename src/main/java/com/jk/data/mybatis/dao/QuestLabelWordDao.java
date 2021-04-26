package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.QuestLabelWord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by 76204 on 2017/7/4.
 */
public interface QuestLabelWordDao {
    @Select("select * from questLabelWord")
    public List<QuestLabelWord> getAll();

    @Update("update ignore questLabelWord set value =#{value}  where id = #{id}")
    public int  updateWord(QuestLabelWord questLabelWord);

    @Insert("insert ignore  into questLabelWord (`id`,`name`,`type`,`value`) values (#{id},#{name},#{type},#{value})")
    public int add(QuestLabelWord questLabelWord);
}
