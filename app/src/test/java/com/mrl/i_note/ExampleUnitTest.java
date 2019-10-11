package com.mrl.i_note;

import android.util.Log;

import com.mrl.i_note.constants.Constants;
import com.mrl.i_note.model.Knowledge;
import com.mrl.i_note.utils.HttpGet;
import com.mrl.i_note.utils.JsonUtil;
import com.mrl.i_note.utils.UuId;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

//    AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
//        @Override
//        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//            String result = new String(responseBody);
//            System.out.print("result:"+result);
//            try {
//                JSONObject json = new JSONObject(result);
//                if (json.has("success")&&json.getBoolean("success")) {
//                    System.out.print("上传成功");
//                }else{
//                    System.out.print("上传失败");
//                }
//                //String token = json.getString("access_token");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        @Override
//        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            System.out.print("服务器错误");
//        }
//    };

    @Test
    public void testUpload(){
        List<Knowledge> knowledges = new ArrayList<Knowledge>();

        for(int i=0;i < 3;i ++){
            Date date = new Date();
            Knowledge math = new Knowledge();
            math.setUser("2c984196630187030163019ee71e0003");
            math.setId(UuId.getId());
            math.setTitle("高等数学"+i);
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
            english.setTitle("大学英语"+i);
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
            political.setTitle("大学政治"+i);
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
            professor.setTitle("专业课"+i);
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
            other.setTitle("其它"+i);
            other.setContent("其它分支");
            other.setDifficult(3);
            other.setImportant(3);
            other.setParentId(null);
            other.setCreateDate(date);
            other.setUpdateDate(date);
            knowledges.add(other);
        }
        String knowledgeStr = JsonUtil.objectToJson(knowledges);
        final Map<String,String> map = new HashMap<>();
        map.put("userName","mrl");
        map.put("userPassword", "123456");
        map.put("knowledgeStr", knowledgeStr);
        final List<Knowledge> finalKnowledges = knowledges;
        new Thread(new Runnable(){
            @Override
            public void run() {
                String result = null;
                try {
                    result = HttpGet.postJson(Constants.SERVER_HOST + Constants.UPLOADNOTE, map, JsonUtil.objectToJSONObject("knowledgeStr", finalKnowledges).getJSONObject("knowledgeStr"));
                    //result = HttpGet.post(Constants.SERVER_HOST_TEST + Constants.UPLOADNOTE, map, "json");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.print(result);
//                Message msg = new Message();
//                Bundle data = new Bundle();
//                data.putString("result", result);
//                msg.setData(data);
//                handler.sendMessage(msg);
            }
        }).start();

//        List<Knowledge> ks = new ArrayList<>();
//        final List<String> results = new ArrayList<>();
//        //分批上传，避免请求头过大
//        for(int i = 0;i < knowledges.size();i ++){
//            if(ks.size()==5||i==knowledges.size()){
//                String knowledgeStr = JsonUtil.objectToJson(ks);
//                ks.clear();
//                final Map<String,String> map = new HashMap<>();
//                map.put("userName","mrl");
//                map.put("userPassword", "123456");
//                map.put("knowledgeStr", knowledgeStr);
//                final List<Knowledge> finalKnowledges = knowledges;
//                new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        String result = null;
//                        try {
//                            result = HttpGet.postJson(Constants.SERVER_HOST_TEST + Constants.UPLOADNOTE, map, null);// JsonUtil.objectToJSONObject("knowledgeStr", finalKnowledges)
//                            results.add(result);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
////                        Message msg = new Message();
////                        Bundle data = new Bundle();
////                        data.putString("result", result);
////                        msg.setData(data);
////                        handler.sendMessage(msg);
//                    }
//                }).start();
//            }else{
//                ks.add(knowledges.get(i));
//            }
//        }
    }




}