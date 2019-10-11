package com.mrl.i_note;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.model.Knowledge;
import com.mrl.i_note.utils.HttpGet;
import com.mrl.i_note.utils.JsonUtil;
import com.mrl.i_note.utils.UuId;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void testUpload(){
        List<Knowledge> knowledges = new ArrayList<Knowledge>();
        Date date = new Date();
        Knowledge math = new Knowledge();
        math.setUser("2c984196630187030163019ee71e0003");
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
        english.setUser("2c984196630187030163019ee71e0003");
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
        political.setUser("2c984196630187030163019ee71e0003");
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
        professor.setUser("2c984196630187030163019ee71e0003");
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
        other.setUser("2c984196630187030163019ee71e0003");
        other.setId(UuId.getId());
        other.setTitle("其它");
        other.setContent("其它分支");
        other.setDifficult(3);
        other.setImportant(3);
        other.setParentId(null);
        other.setCreateDate(date);
        other.setUpdateDate(date);
        knowledges.add(other);
        if(knowledges!=null&&knowledges.size()>0){
            String knowledgeStr = JsonUtil.objectToJson(knowledges);
            String userName = "mrl2";
            String userPassword = "123456";

//            RequestParams requestParams=new RequestParams();
//            requestParams.put("knowledgeStr",knowledgeStr);
//            requestParams.put("userName",userName);
//            requestParams.put("userPassword",userPassword);
//            HttpUtils.testPost(Constants.UPLOADNOTE, requestParams, asyncHttpResponseHandler);

            final Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle data = msg.getData();
                    String result = data.getString("result");
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("success")&&json.getBoolean("success")) {
                        }else{
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            final Map<String,String> map = new HashMap<>();
            map.put("userName",userName);
            map.put("userPassword", userPassword);
            map.put("knowledgeStr", knowledgeStr);
            new Thread(new Runnable(){
                @Override
                public void run() {
                    String result = null;
                    try {
                        result = HttpGet.post(Constants.SERVER_HOST + Constants.UPLOADNOTE, map, "json");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("result", result);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }
}
