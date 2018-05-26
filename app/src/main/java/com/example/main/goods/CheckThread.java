package com.example.main.goods;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by WZH on 2017/12/26.
 * 检查线程类
 */

public class CheckThread extends Thread {
    private static final String url = "http://10.0.84.18:8080/Storage2.0/goods!";
    private String method;                              //访问的服务器端的方法
    private String type;                                //物品属性
    private String key;                                 //物品属性对应的值
    private int set;                                    //标识符
    private Handler handler;
    public  CheckThread(String method, String type,  String key,int set, Handler handler ){
        this.method = method;
        this.type = type;
        this.key = key;
        this.set = set;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message msg = new Message();
        msg.what = set;
        try{
            String result = "";
            String flag = "";
            result = ConnectUtil.readParse(url + method + "?" + type + "=" + URLEncoder.encode(key,"utf-8"));
            if(result != null){
                JSONObject jsonObject = new JSONObject(result);
                flag = jsonObject.getString("message").toString();
            }
            msg.obj = flag;
            handler.sendMessage(msg);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
