package com.mrl.i_note.constants;

import android.support.v7.app.AppCompatDelegate;

/**
 * Created by apple on 2018/5/7.
 */

public interface Constants {

    //params name
    public static final String THEMENAME = "theme_name";

    //params values
    public static final int THEME_DAY = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int THEME_NIGHT = AppCompatDelegate.MODE_NIGHT_YES;
    public static final int THEME_AUTO = AppCompatDelegate.MODE_NIGHT_AUTO;

    //public static final String SERVER_HOST = "http://115.28.79.206:10000/inote-server";
    //public static final String SERVER_HOST = "http://39.106.136.75:8080/inote-server";
    public static final String SERVER_HOST = "http://192.168.43.192:8080/inote";
    public static final String SERVER_HOST_TEST = "http://localhost:8080/inote";
    //创建账户
    public static final String CREATEUSER = "/inote/user/create";
    //获取好友列表
    public static final String GETUSERLIST = "/inote/user/list";
    //获取选中好友的笔记
    public static final String GETUSERNOTE = "/inote/knowledge/list";
    //上传自己的笔记
    public static final String UPLOADNOTE = "/inote/knowledge/upload";
    //上传知识点的配图
    public static final String UPLOADIMG = "/upload/knowledge/img";
    //添加评论
    public static final String COMMENTSUBMIT = "/inote/comment/submit";
    //获取评论
    public static final String COMMENTLIST = "/inote/comment/list";
}
