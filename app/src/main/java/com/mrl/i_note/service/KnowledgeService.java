package com.mrl.i_note.service;

import com.mrl.i_note.model.Knowledge;

import java.util.List;

/**
 * Created by apple on 2018/5/28.
 */

public interface KnowledgeService {

    public int initBaseData(List<Knowledge> knowledges);

    public Knowledge getKnowledge(String selection, String[] selectionArgs, String orderBy);

    public List<Knowledge> getKnowledges(String selection, String[] selectionArgs, String orderBy);

    public int collectKnowledge(Knowledge knowledge);

    public int updateKnowledge(Knowledge knowledge);

    public int deleteKnowledgeById(String id);

}
