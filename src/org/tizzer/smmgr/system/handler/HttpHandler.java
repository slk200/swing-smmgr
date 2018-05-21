package org.tizzer.smmgr.system.handler;

import com.alibaba.fastjson.JSONObject;
import org.tizzer.smmgr.system.utils.IOUtil;
import org.tizzer.smmgr.system.utils.LafUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author tizzer
 * @version 1.0
 */
public class HttpHandler {

    private static final String charset = "utf-8";
    private static final String header = "http://localhost:8080/smmgr";

    /**
     * get请求
     *
     * @param api
     * @return
     * @throws Exception
     */
    private static String doGet(String api) throws Exception {
        URL localURL = new URL(header + api);
        System.out.println("doGet: " + localURL.toString());
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String result = "";
        try {
            inputStream = httpURLConnection.getInputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            result = new String(byteArrayOutputStream.toByteArray(), charset);
            inputStream.close();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            LafUtil.showNotification("访问服务器异常，" + e.getMessage());
        } finally {
            IOUtil.close(byteArrayOutputStream, inputStream);
        }
        return result;
    }

    /**
     * post请求
     *
     * @param api
     * @param param
     * @return
     * @throws Exception
     */
    private static String doPost(String api, String param) throws Exception {
        URL localURL = new URL(header + api);
        System.out.println("doPost: " + localURL.toString());
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        String result = "";
        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(param);
            outputStreamWriter.flush();
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
            inputStream = httpURLConnection.getInputStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            result = new String(byteArrayOutputStream.toByteArray(), charset);
        } catch (Exception e) {
            LafUtil.showNotification("访问服务器异常，" + e.getMessage());
        } finally {
            IOUtil.close(outputStreamWriter, outputStream, byteArrayOutputStream, inputStream);
        }
        return result;
    }

    /**
     * post请求结果转obj
     *
     * @param api
     * @param param
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T post(String api, String param, Class<T> clazz) throws Exception {
        String response = doPost(api, param);
        return JSONObject.parseObject(response, clazz);
    }

    /**
     * get请求结果转obj
     *
     * @param api
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T get(String api, Class<T> clazz) throws Exception {
        String response = doGet(api);
        return JSONObject.parseObject(response, clazz);
    }

}
