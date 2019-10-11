package com.mrl.i_note.service.serviceImpl;

import android.content.Context;

import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.dao.KnowledgeDao;
import com.mrl.i_note.dao.daoImpl.KnowledgeDaoImpl;
import com.mrl.i_note.model.Knowledge;
import com.mrl.i_note.service.KnowledgeService;
import com.mrl.i_note.utils.UuId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apple on 2018/5/28.
 */

public class KnowledgeServiceImpl implements KnowledgeService {

    protected KnowledgeDao knowledgeDao;

    public KnowledgeServiceImpl(Context context){
        this.knowledgeDao = new KnowledgeDaoImpl(context);
    }

    @Override
    public int initBaseData(List<Knowledge> knowledges) {
        int result = 0;
        if(knowledges==null||knowledges.size()<=0){
            knowledges = new ArrayList<>();
            Date date = new Date();
            Knowledge math = new Knowledge();
            math.setId(UuId.getId());
            math.setTitle("高等数学");
            math.setContent("高等数学分支");
            math.setDifficult(3);
            math.setImportant(3);
            math.setParentId(null);
            math.setCreateDate(date);
            math.setUpdateDate(date);
            knowledges.add(math);
            Knowledge english = new Knowledge();
            english.setId(UuId.getId());
            english.setTitle("大学英语");
            english.setContent("大学英语分支");
            english.setDifficult(3);
            english.setImportant(3);
            english.setParentId(null);
            english.setCreateDate(date);
            english.setUpdateDate(date);
            knowledges.add(english);
            Knowledge political = new Knowledge();
            political.setId(UuId.getId());
            political.setTitle("大学政治");
            political.setContent("大学政治分支");
            political.setDifficult(3);
            political.setImportant(3);
            political.setParentId(null);
            political.setCreateDate(date);
            political.setUpdateDate(date);
            knowledges.add(political);
            Knowledge professor = new Knowledge();
            professor.setId(UuId.getId());
            professor.setTitle("专业课");
            professor.setContent("专业课分支");
            professor.setDifficult(3);
            professor.setImportant(3);
            professor.setParentId(null);
            professor.setCreateDate(date);
            professor.setUpdateDate(date);
            knowledges.add(professor);
            Knowledge other = new Knowledge();
            other.setId(UuId.getId());
            other.setTitle("其它");
            other.setContent("其它分支");
            other.setDifficult(3);
            other.setImportant(3);
            other.setParentId(null);
            other.setCreateDate(date);
            other.setUpdateDate(date);
            knowledges.add(other);
        }else{
            List<Knowledge> knowledgesO = getKnowledges("view_time>=?", new String[]{"0"}, null);
            for(Knowledge k:knowledgesO){
                deleteKnowledgeById(k.getId());
            }
        }
        for(Knowledge k:knowledges) {
            k.setUser(null);
            if(k.getImgUrl()!=null&&(k.getImgUrl().indexOf("/uploads/uploadImg/")==0)){
                k.setImgUrl(k.getImgUrl());
            }
            result = collectKnowledge(k);
            if(result<=0){
                break;
            }
        }
        return result;
    }

    @Override
    public Knowledge getKnowledge(String selection, String[] selectionArgs, String orderBy) {
         List<Knowledge> knowledges =  knowledgeDao.getKnowledge(selection, selectionArgs, orderBy);
         if(knowledges!=null&&knowledges.size()>0){
             return knowledges.get(0);
         }else {
             return null;
         }
    }

    @Override
    public List<Knowledge> getKnowledges(String selection, String[] selectionArgs, String orderBy) {
        return knowledgeDao.getKnowledge(selection, selectionArgs, orderBy);
    }

    @Override
    public int collectKnowledge(Knowledge knowledge) {
        if(knowledge.getId()==null||"".equals(knowledge.getId())){
            knowledge.setId(UuId.getId());
        }
        knowledge.setViewTime(1);
        knowledge.setCreateDate(new Date());
        return knowledgeDao.addKnowledge(knowledge);
    }

    @Override
    public int updateKnowledge(Knowledge knowledge) {
        if(knowledge.getId()==null||"".equals(knowledge.getId())){
            return collectKnowledge(knowledge);
        }else {
            return knowledgeDao.updateKnowledge(knowledge);
        }
    }

    @Override
    public int deleteKnowledgeById(String id) {
        List<Knowledge> knowledges = knowledgeDao.getKnowledge("parent_id=?", new String[]{id}, null);
        if(knowledges!=null && knowledges.size()>0) {
            for(Knowledge knowledge : knowledges){
                deleteKnowledgeById(knowledge.getId());
            }
        }
        return knowledgeDao.deleteKnowledgeById(new String[]{id});
    }
}
