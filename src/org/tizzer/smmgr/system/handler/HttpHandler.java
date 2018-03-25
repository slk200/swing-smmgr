package org.tizzer.smmgr.system.handler;

import com.alibaba.fastjson.JSONObject;
import org.tizzer.smmgr.system.utils.TextUtil;

import java.io.*;
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
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        String result = "";
        String tempLine;
        if (httpURLConnection.getResponseCode() != 200) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            while ((tempLine = reader.readLine()) != null) {
                result += tempLine;
            }
        } finally {
            TextUtil.close(reader, inputStreamReader, inputStream);
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
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        String result = "";
        String tempLine;
        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(param);
            outputStreamWriter.flush();
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            while ((tempLine = reader.readLine()) != null) {
                result += tempLine;
            }
        } finally {
            TextUtil.close(outputStreamWriter, outputStream, reader, inputStreamReader, inputStream);
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