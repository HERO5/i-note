package com.mrl.i_note.dao.daoImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrl.i_note.dao.KnowledgeDao;
import com.mrl.i_note.helper.SQLiteHelper;
import com.mrl.i_note.model.Knowledge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apple on 2018/5/28.
 */

public class KnowledgeDaoImpl implements KnowledgeDao {

    private final String CREATE_TABLE = "create table knowledge(" +
            "id TEXT PRIMARY KEY," +
            "title TEXT NULL," +
            "content TEXT NULL," +
            "view_time INTEGER NOT NULL DEFAULT 0," +
            "important INTEGER NOT NULL DEFAULT 0," +
            "difficult INTEGER NOT NULL DEFAULT 0," +
            "parent_id TEXT NULL," +
            "create_date TEXT NOT NULL," +
            "update_date TEXT NOT NULL," +
            "img_url TEXT null)";
    private final String DATABASE_NAME = "note_repository.db";
    private final int DATABASE_VERSION = 2;
    private SQLiteHelper sqLiteHelper;
    public KnowledgeDaoImpl(Context context){
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION, CREATE_TABLE);
        sqLiteHelper.getWritableDatabase();
    }

    @Override
    public int execWriteSql(String sql) {
        int result = -1;
        //得到一个可写的数据库
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        try{
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            db.endTransaction();
            db.close();
        }
        return result;
    }

    @Override
    public List<Knowledge> getKnowledge(String selection, String[] selectionArgs, String orderBy) {
        List<Knowledge> knowledgeList = new ArrayList<Knowledge>();
        //得到一个可写的数据库
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        //参数1：表名
        //参数2：要想显示的列
        //参数3：where子句
        //参数4：where子句对应的条件值
        //参数5：分组方式
        //参数6：having条件
        //参数7：排序方式
        try {
            Cursor cursor = db.query("knowledge", new String[]{"id","title","content","view_time","important","difficult","parent_id","create_date","update_date","img_url"},
                    selection, selectionArgs, null, null, orderBy);
            while(cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                Integer viewTime = cursor.getInt(cursor.getColumnIndex("view_time"));
                Integer important = cursor.getInt(cursor.getColumnIndex("important"));
                Integer difficult = cursor.getInt(cursor.getColumnIndex("difficult"));
                String parentId = cursor.getString(cursor.getColumnIndex("parent_id"));
                String createDate = cursor.getString(cursor.getColumnIndex("create_date"));
                String updateDate = cursor.getString(cursor.getColumnIndex("update_date"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String imgUrl = cursor.getString(cursor.getColumnIndex("img_url"));
                Knowledge knowledge = new Knowledge(id, title, content, viewTime, important, difficult, parentId, sdf.parse(createDate), sdf.parse(updateDate), null, imgUrl);
                knowledgeList.add(knowledge);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //关闭数据库
        db.close();
        return knowledgeList;
    }

    @Override
    public int addKnowledge(Knowledge knowledge) {
        long result = -1;
        if(knowledge==null){
            return (int) result;
        }
        //得到一个可写的数据库
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        //生成ContentValues对象 //key:列名，value:想插入的值
        ContentValues cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("id", knowledge.getId());
        cv.put("title", knowledge.getTitle());
        cv.put("content", knowledge.getContent());
        cv.put("view_time", knowledge.getViewTime());
        cv.put("important", knowledge.getImportant());
        cv.put("difficult", knowledge.getDifficult());
        cv.put("parent_id", knowledge.getParentId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cv.put("create_date", sdf.format(knowledge.getCreateDate()));
        cv.put("update_date", sdf.format(knowledge.getUpdateDate()));
        cv.put("img_url", knowledge.getImgUrl());
        try{
            db.beginTransaction();
            result = db.insert("knowledge", null, cv);
            if(result==-1){
                throw new Exception();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            db.endTransaction();
            db.close();
        }

        return (int) result;
    }

    @Override
    public int updateKnowledge(Knowledge knowledge) {
        int result = -1;
        //得到一个可写的数据库
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        try{
            db.beginTransaction();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            db.execSQL("update knowledge set title='"+knowledge.getTitle()+ "', content='"+knowledge.getContent()
                    +"', important="+knowledge.getImportant()+", difficult="+knowledge.getDifficult()
                    +", view_time="+knowledge.getViewTime()+",update_date='"+sdf.format(knowledge.getUpdateDate())
                    +"', img_url='"+knowledge.getImgUrl()+"' where id='"+knowledge.getId()+"'");
            db.setTransactionSuccessful();
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            db.endTransaction();
            db.close();
        }
        return result;
    }

//    @Override
//    public int updateKnowledge(Knowledge knowledge) {
//        long result = -1;
//        if(knowledge==null){
//            return (int) result;
//        }
//        //得到一个可写的数据库
//        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
//        //生成ContentValues对象 //key:列名，value:想插入的值
//        ContentValues cv = new ContentValues();
//        //往ContentValues对象存放数据，键-值对模式
//        cv.put("id", knowledge.getId());
//        cv.put("title", knowledge.getTitle());
//        cv.put("content", knowledge.getContent());
//        cv.put("view_time", knowledge.getViewTime());
//        cv.put("important", knowledge.getImportant());
//        cv.put("difficult", knowledge.getDifficult());
//        cv.put("parent_id", knowledge.getParentId());
//        cv.put("create_date", knowledge.getCreateDate().getTime());
//        //where 子句 "?"是占位符号，对应后面的"1",
//        String whereClause="id=?";
//        String [] whereArgs = {String.valueOf(knowledge.getId())};
//
//        try{
//            db.beginTransaction();
//            //参数1 是要更新的表名
//            //参数2 是一个ContentValeus对象
//            //参数3 是where子句
//            result = db.update("knowledge", cv, whereClause, whereArgs);
//            if(result<=0){
//                throw new Exception();
//            }
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally{
//            db.endTransaction();
//            db.close();
//        }
//
//        return (int) result;
//    }

    @Override
    public int deleteKnowledgeById(String[] selectionArgs) {
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        //db.execSQL("delete from student where name=?",new Object[]{name});
        int result = db.delete("knowledge", "id=?", selectionArgs);
        return result;
    }
}
