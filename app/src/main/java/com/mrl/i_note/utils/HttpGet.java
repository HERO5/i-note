package com.mrl.i_note.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpGet {
    private static final int SOCKET_TIMEOUT = 10000; // 10S
    private static final String GET = "GET";
    private static final String POST = "POST";

    public static String get(String host, Map<String, String> params) {
        try {
            // 设置SSLContext
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);

            String sendUrl = getUrlWithQueryString(host, params);

            // System.out.println("URL:" + sendUrl);

            URL uri = new URL(sendUrl); // 创建URL对象
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
            }

            conn.setConnectTimeout(SOCKET_TIMEOUT); // 设置相应超时
            conn.setRequestMethod(GET);
            int statusCode = conn.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Http错误码：" + statusCode);
            }

            // 读取服务器的数据
            InputStream is = conn.getInputStream();
            String text = getInputStreamContent(is);
            close(is); // 关闭数据流
            conn.disconnect(); // 断开连接

            return text;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String post(String host, Map<String, String> params, String format) throws IOException {
        String sendUrl = getUrlWithQueryString(host, params);
        HttpURLConnection conn = (HttpURLConnection) new URL(sendUrl).openConnection();
        conn.setConnectTimeout(5000);

        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        //conn.setRequestProperty("contentType", "UTF-8");
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
//        conn.setRequestProperty("Content-type", "application/"+format+";charset=UTF-8");
        conn.setRequestProperty("ContentType", "text/" + format);
        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        conn.getOutputStream().write(content);
//        conn.getOutputStream().close();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.err.println("http 请求返回的状态码错误，期望200， 当前是 " + responseCode);
            if (responseCode == 401) {
                System.err.println("可能是appkey appSecret 填错");
            }
        }

        InputStream is = conn.getInputStream();
        String result = getInputStreamContent(is);
        close(is); // 关闭数据流
        conn.disconnect(); // 断开连接

        return result;
    }

    /**
     * 向指定URL发送post方法的请求，请求内容为json格式的字符串
     * @param host
     * @param params
     * @return String 直接返回json字符串
     */
    public static String postJson(String host, Map<String, String> params, JSONObject jsonObject) throws IOException {
        String sendUrl = getUrlWithQueryString(host, params);
        HttpURLConnection conn = (HttpURLConnection) new URL(sendUrl).openConnection();
        conn.setConnectTimeout(10000);

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");//**注意点1**，需要此格式，后边这个字符集可以不设置
        conn.setRequestProperty("Charset", "utf-8");
//        long size = jsonObject.toString().getBytes().length+sendUrl.getBytes().length;
//        conn.setRequestProperty("Content-length", "5567");
        conn.connect();
//        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//        out.write(jsonObject.toString().getBytes("UTF-8"));//**注意点2**，需要此格式
//        out.flush();
//        out.close();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            System.err.println("http 请求返回的状态码错误，期望200， 当前是 " + responseCode);
            if (responseCode == 401) {
                System.err.println("可能是appkey appSecret 填错");
            }
        }

        InputStream is = conn.getInputStream();
        String result = getInputStreamContent(is);
        close(is); // 关闭数据流
        conn.disconnect(); // 断开连接

        return result;
    }

    private static String getInputStreamContent(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }

        String text = builder.toString();

        close(br); // 关闭数据流

        return text;
    }

    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(encode(value)));
            //builder.append(value);

            i++;
        }

        return builder.toString();
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     * 
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    };

}
