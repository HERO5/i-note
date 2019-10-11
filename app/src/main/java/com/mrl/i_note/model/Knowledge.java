package com.mrl.i_note.model;

import com.mrl.i_note.utils.UuId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by apple on 2018/5/28.
 */

public class Knowledge implements Serializable {

    /* 知识点id */
    private String id;
    /* 知识点名称 */
    private String title;
    /* 知识点内容 */
    private String content;
    /* 知识点回顾次数 */
    private int viewTime;
    /* 知识点重要程度 */
    private int important;
    /* 知识点难度 1-5 难度递增 */
    private int difficult;
    /* 父节点id */
    private String parentId;
    /* 创建时间 */
    private Date createDate;
    /* 创建时间 */
    private Date updateDate;
    /* 创建者id */
    private String user;
    /* 知识点配图路径 */
    private String imgUrl;

    public Knowledge() {}

    public Knowledge(String id, String title, String content, int viewTime, int important,
                     int difficult, String parentId, Date createDate, Date updateDate, String user, String imgUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewTime = viewTime;
        this.important = important;
        this.difficult = difficult;
        this.parentId = parentId;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.user = user;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViewTime() {
        return viewTime;
    }

    public void setViewTime(int viewTime) {
        this.viewTime = viewTime;
    }

    public int getImportant() {
        return important;
    }

    public void setImportant(int important) {
        this.important = important;
    }

    public int getDifficult() {
        return difficult;
    }

    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String userId) {
        this.user = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public static Knowledge deepCopy(Knowledge knowledgeO){
        if(knowledgeO==null){
            return null;
        }else{
            Knowledge knowledgeN = new Knowledge();
            knowledgeN.setId(knowledgeO.getId());
            knowledgeN.setTitle(knowledgeO.getTitle());
            knowledgeN.setContent(knowledgeO.getContent());
            knowledgeN.setViewTime(knowledgeO.getViewTime());
            knowledgeN.setDifficult(knowledgeO.getDifficult());
            knowledgeN.setImportant(knowledgeO.getImportant());
            knowledgeN.setParentId(knowledgeO.getParentId());
            knowledgeN.setImgUrl(knowledgeO.getImgUrl());
            knowledgeN.setUser(knowledgeO.getUser());
            knowledgeN.setCreateDate(knowledgeO.getCreateDate());
            knowledgeN.setUpdateDate(knowledgeO.getUpdateDate());
            return knowledgeN;
        }
    }

    @Override
    public String toString() {
        return "Knowledge{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", viewTime=" + viewTime +
                ", important=" + important +
                ", difficult=" + difficult +
                ", parentId='" + parentId + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", userId='" + user + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
