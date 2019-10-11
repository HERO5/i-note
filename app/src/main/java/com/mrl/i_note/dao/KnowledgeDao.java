package com.mrl.i_note.dao;

import com.mrl.i_note.model.Knowledge;

import java.util.List;

/**
 * Created by apple on 2018/5/28.
 */

public interface KnowledgeDao {

    public int execWriteSql(String sql);

    public List<Knowledge> getKnowledge(String selection, String[] selectionArgs, String orderBy);

    public int addKnowledge(Knowledge knowledge);

    public int updateKnowledge(Knowledge knowledge);

    public int deleteKnowledgeById(String[] selectionArgs);

}
