package com.jk.data.mybatis.dao;

import com.jk.data.mybatis.bean.QuestLabelWord;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by 76204 on 2017/7/4.
 */
public interface QuestLabelWordDao {
    @Select("select * from questLabelWord")
    public List<QuestLabelWord> getAll();
}
