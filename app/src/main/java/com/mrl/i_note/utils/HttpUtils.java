package com.mrl.i_note.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.mrl.i_note.constants.Constants;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by apple on 2017/11/1.
 */

public class HttpUtils {

    public static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    /*params 设置失败*/
    public static void post(String url, RequestParams params, String contentType, AsyncHttpResponseHandler responseHandler){
        HttpEntity entity = null;
        try {
            if (params != null) {
                entity = params.getEntity(responseHandler);
            }
        } catch (IOException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, e);
            } else {
                e.printStackTrace();
            }
        }
        client.post(null, getAbsoluteUrl(url), entity, contentType, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        //client.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void testPost(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        //client.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
        client.post(Constants.SERVER_HOST_TEST + url, params, responseHandler);
    }
    public static void post(String url, AsyncHttpResponseHandler responseHandler) {
        //client.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Constants.SERVER_HOST + relativeUrl;
    }

    public static void getImage(String url, ResponseHandlerInterface responseHandler) {
        client.get(url, responseHandler);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public static void postWithAuth(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        String JSESSIONID=PrefUtils.get("user","session",context);
        Log.i("session","提交"+JSESSIONID);
        //client.setBasicAuth("18813073333","123456");
        client.addHeader("Cookie", "JSESSIONID="+JSESSIONID);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postWithAuth(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        String JSESSIONID = PrefUtils.get("user", "session", context);
        Log.i("session", "提交" + JSESSIONID);
        //client.setBasicAuth("18813073333","123456");
        client.addHeader("Cookie", "JSESSIONID=" + JSESSIONID);
        client.post(getAbsoluteUrl(url), responseHandler);
    }

    public static void getWithAuth(Context context, String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String JSESSIONID = PrefUtils.get("user", "session", context);
        Log.i("session", "提交" + JSESSIONID);
        //client.setBasicAuth("18813073333","123456");
        client.addHeader("Cookie", "JSESSIONID=" + JSESSIONID);
        client.get(getAbsoluteUrl(url), asyncHttpResponseHandler);
    }
}
