package com.mrl.i_note.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mrl.i_note.R;
import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.customView.CustomAlertDiglog;
import com.mrl.i_note.fragment.FolderStructureFragment;
import com.mrl.i_note.model.Knowledge;
import com.mrl.i_note.service.KnowledgeService;
import com.mrl.i_note.service.serviceImpl.KnowledgeServiceImpl;
import com.mrl.i_note.utils.HttpGet;
import com.mrl.i_note.utils.HttpUtils;
import com.mrl.i_note.utils.ImgDownload;
import com.mrl.i_note.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public static final int AFTER_EDIT = 1;
    public static final int AFTER_USERLIST = 2;
    public static final int AFTER_COMMENTLIST = 3;
    public static final int AFTER_OTHER = 0;
    /**
     * 前一个Activity的标志位，即当前Activity由哪个Activity转跳而来，默认为OTHER
     */
    private static int BEFFOR_ACTION = AFTER_OTHER;

    /**
     * 当前为日间/夜间模式的标志位
     */
    protected static int dayNightTheme;
    private FragmentManager fragmentManager;
    private FolderStructureFragment folderStructureFragment;

    private FloatingActionMenu fabMenu;
    private FloatingActionButton reflash;
    private FloatingActionButton upload;
    private FloatingActionButton download;
    private FloatingActionButton friends;
    private FloatingActionButton changeTheme;

    private ProgressBar progressBar;
    /**
     * 保存社区其他成员的note，
     * 入口：UserListActivity的itme点击事件
     * 出口：FolderStructureFragment的initTree()方法
     */
    private static List<Knowledge> friendKnowledges;
    /**
     * 全局唯一的KnowledgeService（除了EditActivity）
     */
    private static KnowledgeService knowledgeService;

    protected static MainActivity mainActivity;

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static List<Knowledge> getFriendKnowledges() {
        return friendKnowledges;
    }

    public static void setFriendKnowledges(List<Knowledge> friendKnowledges) {
        MainActivity.friendKnowledges = friendKnowledges;
    }

    public static KnowledgeService getKnowledgeService() {
        return knowledgeService;
    }

    public static int getDayNightTheme() {
        return dayNightTheme;
    }

    public static int getBefforAction() {
        return BEFFOR_ACTION;
    }

    public static void setBefforAction(int befforAction) {
        BEFFOR_ACTION = befforAction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processExtraData();
        getDelegate().setLocalNightMode(dayNightTheme);
        setContentView(R.layout.activity_single_fragment);
        fabMenu = findViewById(R.id.floating_action_menu);
        reflash = findViewById(R.id.reflash);
        upload = findViewById(R.id.upload);
        download = findViewById(R.id.download);
        friends = findViewById(R.id.friends);
        changeTheme = findViewById(R.id.change_theme);
        progressBar = findViewById(R.id.progress_bar);
        fragmentManager = this.getSupportFragmentManager();
        mainActivity = this;
        knowledgeService = new KnowledgeServiceImpl(this);
        setFabButtonListener();

        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        //hideFragments(transaction);
    }

    /**
     * oResume方法有两个入口：其他Activity.finish()之后；app重新获取焦点
     * UserListActivity.finish()之前会保存friendKnowledges，因此do nothing
     * 其他情况下需要把friendKnowledges重置为null
     */
    @Override
    protected void onResume() {
        super.onResume();
        switch (BEFFOR_ACTION){
            case AFTER_USERLIST:
            case AFTER_COMMENTLIST:
                break;
            case AFTER_OTHER:
            case AFTER_EDIT:
            default:
                friendKnowledges = null;
                break;
        }
        reflash(friendKnowledges, BEFFOR_ACTION);
    }

    /**
     * onSaveInstanceState在当前Activity失去焦点之前被调用，用来保存当前Activity状态，
     * 在当前业务逻辑中不希望folderStructureFragment被保存，因此要将它移除，否则会导致Fragment界面残留
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(folderStructureFragment!=null){
            transaction.remove(folderStructureFragment);
        }
        try {
            transaction.commitAllowingStateLoss();
        }catch (Exception e){
            e.printStackTrace();
        }
        folderStructureFragment = null;
        super.onSaveInstanceState(outState);
    }

    /**
     * 在当前Activity创建时调用(不包括重新获取焦点)
     * 主要用来获取日间/夜间主题类型，默认值为THEME_DAY
     */
    private void processExtraData(){
        Intent intent = getIntent();
        dayNightTheme = intent.getIntExtra(Constants.THEMENAME, Constants.THEME_DAY);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.reflash:
                    reflash(null, AFTER_OTHER);
                    break;
                case R.id.upload:
                    //上传note对话框的信息回调方法
                    if (!HttpUtils.isNetworkConnected(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "没有网络连接!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    CustomAlertDiglog.DataCallback uploadCallback = new CustomAlertDiglog.DataCallback() {
                        @Override
                        public void callback(Map<String,Object> datas) {
                            final String userName = (String) datas.get("userName");
                            final String userPassword = (String) datas.get("userPassword");
                            List<Knowledge> knowledges = null;
                            //当前业务逻辑为：如果用户没输入任何信息，直接点击"执行按钮"，那么弹出对话框，进入创建用户流程，否则进入note上传流程
                            if(userName!=null&&!"".equals(userName)&&userPassword!=null&&!"".equals(userPassword)){
                                //用户信息不为空，就赋值要上传的note信息
                                knowledges = knowledgeService.getKnowledges("view_time>?", new String[]{"0"}, null);
                            }else{
                                //创建用户对话框的信息回调方法
                                CustomAlertDiglog.DataCallback createAccountCallback = new CustomAlertDiglog.DataCallback() {
                                    @Override
                                    public void callback(Map<String, Object> datas) {
                                        String userName = (String) datas.get("userName");
                                        String userPassword = (String) datas.get("userPassword");
                                        final RequestParams requestParams=new RequestParams();
                                        requestParams.put("userName",userName);
                                        requestParams.put("userPassword",userPassword);
                                        HttpUtils.post(Constants.CREATEUSER, requestParams, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                String result = new String(responseBody);
                                                try {
                                                    JSONObject json = new JSONObject(result);
                                                    if (json.has("success")&&json.getBoolean("success")) {
                                                        Toast.makeText(MainActivity.this, "创建用户成功", Toast.LENGTH_LONG).show();
                                                    }else{
                                                        Toast.makeText(MainActivity.this, json.getString("msg")==null?"创建用户失败":json.getString("msg"), Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Toast.makeText(MainActivity.this, "服务器出现了问题", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                };
                                CustomAlertDiglog.initUserInfoInputDialog(MainActivity.this, dayNightTheme, CustomAlertDiglog.CREATEUSER, "输入信息以创建账号", createAccountCallback);
                                return;
                            }
                            //note信息不为空，则继续执行note上传流程
                            if(knowledges!=null&&knowledges.size()>0){
                                    final List<Knowledge> finalKnowledges = knowledges;
                                    final Handler handler = new Handler(){
                                        @Override
                                        public void handleMessage(Message msg) {
                                            super.handleMessage(msg);
                                            Bundle data = msg.getData();
                                            String result = data.getString("result");
                                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                                            //note上传出口
                                            if("上传成功".equals(result)){
                                                Toast.makeText(MainActivity.this, "现在开始上传配图", Toast.LENGTH_LONG).show();
                                                //配图上传入口
                                                uploadImg(finalKnowledges);
                                            }else{
                                                progressBar.setVisibility(View.GONE);
                                                setFabEnable(true);
                                            }
                                        }
                                    };
                                    //note上传入口，此时设置按钮不能点击
                                    setFabEnable(false);
                                    progressBar.setVisibility(View.VISIBLE);
                                    new Thread(new Runnable(){
                                        @Override
                                        public void run() {
                                            //progressBar.setVisibility(View.VISIBLE);
                                            int pageSize = 5;
                                            int pageCount = 0;
                                            String result = null;
                                            List<Knowledge> ks = new ArrayList<>();
                                            //分批上传，避免请求头过大
                                            for(int i = 0; i < finalKnowledges.size(); i ++){
                                                Knowledge k = Knowledge.deepCopy(finalKnowledges.get(i));
                                                String imgUrl = k.getImgUrl();
                                                if(imgUrl!=null&&!"".equals(imgUrl)){
                                                    String extentName = imgUrl.substring(imgUrl.lastIndexOf(".")+1);
                                                    k.setImgUrl(k.getId()+"."+extentName);
                                                }
                                                ks.add(k);
                                                if(ks.size()==pageSize||(i==finalKnowledges.size()-1)){
                                                    String knowledgeStr = JsonUtil.objectToJson(ks);
                                                    ks.clear();
                                                    final Map<String,String> map = new HashMap<>();
                                                    map.put("userName",userName);
                                                    map.put("userPassword", userPassword);
                                                    //通知服务器在第一次分批时先执行删除数据库操作
                                                    map.put("deleteFlag",(pageCount<1)?"true":"false");
                                                    map.put("knowledgeStr", knowledgeStr);
                                                    try {
                                                        result = HttpGet.postJson(Constants.SERVER_HOST + Constants.UPLOADNOTE, map, null);// JsonUtil.objectToJSONObject("knowledgeStr", finalKnowledges)
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if(result!=null&&!"".equals(result)){
                                                        try {
                                                            JSONObject json = new JSONObject(result);
                                                            if (json.has("success")&&json.getBoolean("success")) {
                                                                result = "上传成功";
                                                                pageCount++;
                                                                continue;
                                                            }else{
                                                                result = json.getString("msg");
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else{
                                                        result = "上传失败";
                                                    }
                                                    break;
                                                }
                                            }
                                            Message msg = new Message();
                                            Bundle data = new Bundle();
                                            data.putString("result", result);
                                            msg.setData(data);
                                            handler.sendMessage(msg);
                                            //progressBar.setVisibility(View.GONE);
                                        }
                                    }).start();
                            }
                        }
                    };
                    CustomAlertDiglog.initUserInfoInputDialog(MainActivity.this, dayNightTheme, CustomAlertDiglog.UPLOAD, "若无账号，请直接点击'执行'", uploadCallback);
                    break;
                case R.id.download:
                    //下载note
                    if(BEFFOR_ACTION==AFTER_USERLIST&&friendKnowledges!=null&&friendKnowledges.size()>0){
                        //下载图片
                        List<ImgDownload.KeyValue> keyValues = new ArrayList<>();
                        for(int i=0;i<friendKnowledges.size();i++){
                            keyValues.add(new ImgDownload.KeyValue(friendKnowledges.get(i).getId(), friendKnowledges.get(i).getImgUrl()));
                        }
                        final ImgDownload imgDownload = new ImgDownload();
                        Handler handler = new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                //下载图片出口
                                progressBar.setVisibility(View.GONE);
                                setFabEnable(true);
                                Toast.makeText(MainActivity.this, "配图加载完成", Toast.LENGTH_LONG).show();
                                List<ImgDownload.KeyValue> keyValues = imgDownload.getUrls();
                                for(int i=0;i<friendKnowledges.size();i++){
                                    friendKnowledges.get(i).setImgUrl(keyValues.get(i).getValue());
                                }
                                CustomAlertDiglog.initExecDialog(MainActivity.getMainActivity(), friendKnowledges,
                                        MainActivity.getMainActivity().getKnowledgeService(), MainActivity.getMainActivity().getDayNightTheme(), CustomAlertDiglog.DOWNLOAD);
                            }
                        };
                        imgDownload.setUrls(keyValues);
                        imgDownload.setOuterHandler(handler);
                        imgDownload.verifyStoragePermissions(MainActivity.this);
                        setFabEnable(false);
                        Toast.makeText(MainActivity.this, "配图加载中，请稍等", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.VISIBLE);
                        //下载图片入口
                        imgDownload.execTask();
                    }
                    break;
                case R.id.friends:
                    //获取社区所有用户
                    if (HttpUtils.isNetworkConnected(MainActivity.this)) {
                        HttpUtils.post(Constants.GETUSERLIST, null, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String result = new String(responseBody);
                                try {
                                    JSONObject json = new JSONObject(result);
                                    if (json.has("success")&&json.getBoolean("success")) {
                                        List<String> userNames = JsonUtil.jsonsToObjects(json.getString("data"),String.class);
                                        Intent intent = new Intent(MainActivity.this, UserListActivity.class);
                                        intent.putStringArrayListExtra("userNames", (ArrayList<String>) userNames);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    }else{
                                        Toast.makeText(MainActivity.this, "获取资源失败", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(MainActivity.this, "服务器出现了问题", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "没有网络连接!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.change_theme:
                    //设置主题对话框
                    CustomAlertDiglog.initChangeThemeDialog(MainActivity.this, MainActivity.class, dayNightTheme);
                    break;
                default:
                    break;
            }
        }
    };

    private void setFabButtonListener(){
        reflash.setOnClickListener(listener);
        download.setOnClickListener(listener);
        upload.setOnClickListener(listener);
        friends.setOnClickListener(listener);
        changeTheme.setOnClickListener(listener);
        if(BEFFOR_ACTION==AFTER_USERLIST){
            download.setVisibility(View.VISIBLE);
            upload.setVisibility(View.GONE);
        }else {
            upload.setVisibility(View.VISIBLE);
            download.setVisibility(View.GONE);
        }
        fabMenu.setClosedOnTouchOutside(true);
    }

    private void setFabVisible(){
        if(BEFFOR_ACTION==AFTER_USERLIST){
            download.setVisibility(View.VISIBLE);
            upload.setVisibility(View.GONE);
        }else {
            upload.setVisibility(View.VISIBLE);
            download.setVisibility(View.GONE);
        }
        fabMenu.open(true);
    }

    private void setFabEnable(boolean enable){
        reflash.setEnabled(enable);
        download.setEnabled(enable);
        upload.setEnabled(enable);
        friends.setEnabled(enable);
        changeTheme.setEnabled(enable);
    }

    public void reflash(List<Knowledge> knowledges, int befforAction){
        this.friendKnowledges = knowledges;
        this.BEFFOR_ACTION = befforAction;
        setFabVisible();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(folderStructureFragment!=null){
            //刷新前将当前folderStructureFragment移除，否则会导致Fragment界面残留
            transaction.remove(folderStructureFragment);
        }
        folderStructureFragment = new FolderStructureFragment();
        transaction.add(R.id.fragment, folderStructureFragment);
        try {
            transaction.commitAllowingStateLoss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //递归逐个上传note配图
    private void uploadImg(final List<Knowledge> knowledges){
        if(knowledges.size()>0){
            final Knowledge k = knowledges.get(0);
            knowledges.remove(0);
            //上传图片
            if(k.getImgUrl()!=null&&!"".equals(k.getImgUrl())){
                File file = new File(k.getImgUrl());
                //更改知识点配图的路径名，以便服务器保存
                if(file.length()>(1024*1024*10)){
                    Log.i("upload img failed:", "文件'"+k.getImgUrl()+"'过大");
                    uploadImg(knowledges);
                    return;
                }
                RequestParams requestParams = new RequestParams();
                try {
                    requestParams.put("file", file, "image/*");
                    requestParams.put("knowledgeId", k.getId());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("upload img failed:", "'"+k.getTitle()+"'的配图有些问题");
                    uploadImg(knowledges);
                    return;
                }
                HttpUtils.post(Constants.UPLOADIMG, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if(responseBody!=null&&!"".equals(new String(responseBody))){
                            Log.i("upload img succeed:", "'"+k.getTitle()+"'配图上传成功");
                        }else{
                            Log.i("upload img failed:", "'"+k.getTitle()+"'的配图有些问题");
                        }
                        uploadImg(knowledges);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i("upload img failed:", "'"+k.getTitle()+"'的配图有些问题;"+statusCode);
                        uploadImg(knowledges);
                    }
                });
            }else{
                uploadImg(knowledges);
            }
        }else{
            //配图上传出口
            Toast.makeText(MainActivity.this, "配图上传完成", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            setFabEnable(true);
        }
    }

    //    /**
//     * 将所有的Fragment都置为隐藏状态。
//     * 用于对Fragment执行操作的事务
//     */
//    private void hideFragments(FragmentTransaction transaction) {
//        if (folderStructureFragment != null) {
//            transaction.hide(folderStructureFragment);
//        }
//        transaction.commitAllowingStateLoss();
//    }
}
