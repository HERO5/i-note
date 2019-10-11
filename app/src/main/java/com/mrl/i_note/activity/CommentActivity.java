package com.mrl.i_note.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mrl.i_note.R;
import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.customView.CustomAlertDiglog;
import com.mrl.i_note.model.Comment;
import com.mrl.i_note.utils.HttpUtils;
import com.mrl.i_note.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by apple on 2018/6/7.
 */

public class CommentActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private EditText content;
    private Button submit;
    private ListView listView;
    private SimpleAdapter sim_Adapter;
    private List<Map<String, Object>> dataList;
    private String knowledgeId;
    private List<Comment> comments;

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String commentContent =  content.getText().toString();
            if(commentContent==null||"".equals(commentContent)){
                Toast.makeText(CommentActivity.this, "评论内容不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (!HttpUtils.isNetworkConnected(CommentActivity.this)) {
                Toast.makeText(CommentActivity.this, "没有网络连接!", Toast.LENGTH_LONG).show();
                return;
            }
            //上传note对话框的信息回调方法
            CustomAlertDiglog.DataCallback uploadCallback = new CustomAlertDiglog.DataCallback() {
                @Override
                public void callback(Map<String,Object> datas) {
                    final String userName = (String) datas.get("userName");
                    final String userPassword = (String) datas.get("userPassword");
                    //当前业务逻辑为：如果用户没输入任何信息，直接点击"执行按钮"，那么弹出对话框，进入创建用户流程，否则进入note上传流程
                    if(!(userName!=null&&!"".equals(userName)&&userPassword!=null&&!"".equals(userPassword))){
                        //用户信息为空，就创建用户对话框的信息回调方法
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
                                                Toast.makeText(CommentActivity.this, "创建用户成功", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(CommentActivity.this, json.getString("msg")==null?"创建用户失败":json.getString("msg"), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Toast.makeText(CommentActivity.this, "服务器出现了问题", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        };
                        CustomAlertDiglog.initUserInfoInputDialog(CommentActivity.this, MainActivity.getMainActivity().getDayNightTheme(), CustomAlertDiglog.CREATEUSER, "输入信息以创建账号", createAccountCallback);
                        return;
                    }
                    final RequestParams requestParams=new RequestParams();
                    requestParams.put("userName",userName);
                    requestParams.put("userPassword",userPassword);
                    requestParams.put("content", commentContent);
                    requestParams.put("knowledgeId", knowledgeId);
                    HttpUtils.post(Constants.COMMENTSUBMIT, requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String result = new String(responseBody);
                            try {
                                JSONObject json = new JSONObject(result);
                                if (json.has("success")&&json.getBoolean("success")) {
                                    //评论提交成功，重新加载数据
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("pic", R.mipmap.ic_launcher);
                                    map.put("userName", userName);
                                    map.put("content", commentContent);
                                    dataList.add(map);
                                    sim_Adapter.notifyDataSetChanged();
                                    Toast.makeText(CommentActivity.this, "评论成功", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(CommentActivity.this, json.getString("msg")==null?"评论失败":json.getString("msg"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(CommentActivity.this, "服务器出现了问题", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            };
            CustomAlertDiglog.initUserInfoInputDialog(CommentActivity.this, MainActivity.getMainActivity().getDayNightTheme(), CustomAlertDiglog.UPLOAD, "若无账号，请直接点击'执行'", uploadCallback);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_list_root);

        listView = (ListView) findViewById(R.id.list_root);

        content = findViewById(R.id.c_e_content);
        submit = findViewById(R.id.submit);

        if(MainActivity.getMainActivity().getDayNightTheme() == Constants.THEME_DAY){
            content.setTextColor(Color.parseColor("#787878"));
            content.setBackgroundResource(R.drawable.edittext_selector_day);
            submit.setBackgroundResource(R.drawable.rounded_rectangle_day);
            submit.setTextColor(Color.parseColor("#787878"));
        }else{
            content.setTextColor(Color.parseColor("#e9e9e9"));
            content.setBackgroundResource(R.drawable.edittext_selector_night);
            submit.setBackgroundResource(R.drawable.rounded_rectangle_night);
            submit.setTextColor(Color.parseColor("#e9e9e9"));
        }

        Intent intent = getIntent();
        knowledgeId = intent.getStringExtra("knowledgeId");
        dataList = new ArrayList<Map<String, Object>>();//数据源
        initData();

        sim_Adapter = new SimpleAdapter(this, dataList, R.layout.comment_list_item, new String[]{"pic", "userName", "content"}, new int[]{R.id.pic, R.id.textView, R.id.content});
        listView.setAdapter(sim_Adapter);
    }

    private void initData(){
        if(knowledgeId!=null&&!"".equals(knowledgeId)){
            if (HttpUtils.isNetworkConnected(this)) {
                final RequestParams requestParams=new RequestParams();
                requestParams.put("knowledgeId", knowledgeId);
                HttpUtils.post(Constants.COMMENTLIST, requestParams, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String result = new String(responseBody);
                        String err = null;
                        try {
                            JSONObject json = new JSONObject(result);
                            if (json.has("success")&&json.getBoolean("success")) {
                                comments = JsonUtil.jsonsToObjects(json.getString("data"),Comment.class);
                                if(comments!=null&&comments.size()>0){
                                    //适配器加载数据源
                                    for(Comment comment:comments){
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("pic", R.mipmap.ic_launcher);
                                        map.put("userName", comment.getUserName());
                                        map.put("content", comment.getContent());
                                        dataList.add(map);
                                    }
                                    sim_Adapter.notifyDataSetChanged();
                                }else{
                                    err = "暂无相关评论";
                                }
                            }else{
                                err = "暂无相关评论";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            err = "数据解析异常";
                        }
                        if(err!=null){
                            Toast.makeText(CommentActivity.this, err, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(CommentActivity.this, "服务器出现了问题", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(this, "没有网络连接!", Toast.LENGTH_LONG).show();
            }
            submit.setOnClickListener(submitListener);
        }else{
            Toast.makeText(this, "获取评论失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.getMainActivity().setBefforAction(MainActivity.AFTER_USERLIST);
        super.onBackPressed();
    }

    //滚动事件
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                //Toast.makeText(this, "用户在手指离开屏幕前，由于用力滑了一下，视图仍以靠惯性滑动", Toast.LENGTH_SHORT).show();
//                Map<String,Object>map=new HashMap<String, Object>();
//                map.put("pic",R.mipmap.ic_launcher);
//                map.put("textView","新增项");
//                dataList.add(map);
                //更新适配器里的数据
//                sim_Adapter.notifyDataSetChanged();
                break;
            case SCROLL_STATE_IDLE:
                //Toast.makeText(this, "视图已经停止滑动", Toast.LENGTH_SHORT).show();
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                //Toast.makeText(this, "手指没有离开屏幕，视图正在滑动", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

}
