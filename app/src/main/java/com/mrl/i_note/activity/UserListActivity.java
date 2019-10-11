package com.mrl.i_note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mrl.i_note.R;
import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.model.Knowledge;
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
 * Created by apple on 2018/5/31.
 */

public class UserListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AbsListView.OnScrollListener  {

    private ListView listView;
    private SimpleAdapter sim_Adapter;
    private List<Map<String, Object>> dataList;

    private List<String> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processExtraData();
        setContentView(R.layout.user_list_root);
        listView = (ListView) findViewById(R.id.list_root);
        //新建一个适配器
        /*sim_Adapter=new SimpleAdapter(Context,data,resource,from,to);
          context：上下文
          data：数据源-一个Map组成的List集合
                   每一个Map都会去对应ListView中的一行
                   每一个Map（键-值对）中的键必须包含在from中所指定的键
          resource：列表项的布局文件ID
          from：Map中的键名
          to：绑定数据视图中的ID，与from对应
         */
        sim_Adapter = new SimpleAdapter(this, dataList, R.layout.user_list_item, new String[]{"pic", "userName"}, new int[]{R.id.pic, R.id.textView});
        //视图(ListView)加载适配器
        listView.setAdapter(sim_Adapter);

        //监听器
        listView.setOnItemClickListener(this);

        Toast.makeText(this, " 请选择社区成员", Toast.LENGTH_LONG).show();
    }

    private void processExtraData(){
        Intent intent = getIntent();
        userNames = intent.getStringArrayListExtra("userNames");
        //适配器加载数据源
        dataList = new ArrayList<Map<String, Object>>();//数据源
        for(String userName:userNames){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pic", R.mipmap.ic_launcher);
            map.put("userName", userName);
            dataList.add(map);
        }

    }

    //点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String text=listView.getItemAtPosition(position)+"";
        if (HttpUtils.isNetworkConnected(this)) {
            final RequestParams requestParams=new RequestParams();
            Map<String, Object> map = JsonUtil.jsonToMap(text);
            requestParams.put("userName",map.get("userName"));
            HttpUtils.post(Constants.GETUSERNOTE, requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("success")&&json.getBoolean("success")) {
                            List<Knowledge> knowledges = JsonUtil.jsonsToObjects(json.getString("data"),Knowledge.class);
                            if(knowledges!=null&&knowledges.size()>0){
                                MainActivity.getMainActivity().setFriendKnowledges(knowledges);
                                MainActivity.getMainActivity().setBefforAction(MainActivity.AFTER_USERLIST);
                                finish();
                            }else{
                                Toast.makeText(UserListActivity.this, "该用户笔记空空如也", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(UserListActivity.this, "获取资源失败", Toast.LENGTH_LONG).show();
                        }
                        //String token = json.getString("access_token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(UserListActivity.this, "服务器出现了问题", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "没有网络连接!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.getMainActivity().setBefforAction(MainActivity.AFTER_EDIT);
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
