package com.mrl.i_note.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import com.mrl.i_note.constants.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by apple on 2018/6/7.
 */

public class ImgDownload {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private List<KeyValue> urls;
    private Bitmap bitmap;
    private Handler outerHandler =null;

    public ImgDownload(){
    }

    public void setOuterHandler(Handler outerHandler) {
        this.outerHandler = outerHandler;
    }

    public void setUrls(List<KeyValue> urls) {
        this.urls = urls;
    }

    public List<KeyValue> getUrls() {
        return urls;
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execTask(){
        new Task().execute();
    }

    /**
     * 异步线程下载图片
     *
     */
    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            for(KeyValue keyValue : urls){
                if(keyValue.getValue()!=null&&!"".equals(keyValue.getValue())){
                    bitmap=GetImageInputStream(Constants.SERVER_HOST+keyValue.getValue());
                    String fileName = savaImage(bitmap, Environment.getExternalStorageDirectory().getPath()+"/inote", keyValue.getKey());
                    keyValue.setValue(fileName);
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message msg = new Message();
            msg.what=0x123;
            outerHandler.sendMessage(msg);
        }

    }

    /**
     * 获取网络图片
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl){
        URL url;
        HttpURLConnection connection=null;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream=connection.getInputStream();
            bitmap= BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     * @param bitmap
     * @param path 本地路径
     * @return String
     */
    public String savaImage(Bitmap bitmap, String path, String fileName){
        File file=new File(path);
        FileOutputStream fileOutputStream=null;
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
        String realPath = path+"/"+fileName+".jpg";
        file = new File(realPath);
        if (file.isFile()) {
            file.delete();
        }
        try {
            fileOutputStream=new FileOutputStream(realPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realPath;
    }

    public static class KeyValue{
        private String key;
        private String value;
        public KeyValue(String key, String value){
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
