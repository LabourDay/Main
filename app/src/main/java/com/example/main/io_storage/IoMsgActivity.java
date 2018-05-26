package com.example.main.io_storage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.main.R;
import com.example.main.entity.Storage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 作者： 菜鸟先吃
 * 时间：2017/12/5
 * 说明：
 */

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class IoMsgActivity extends Activity {
    private  String url=null;
    private ListView listview=null;
    private SimpleAdapter simpleAdapter = null; // 进行数据的转换操作
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io);
        listview = (ListView) super.findViewById(R.id.io_list);
        url="http://10.0.84.18:8080/Storage2.0/telIOAction!telIOMessage";
        //强行让4.0版本 不去检查主线程访问网络。
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        resultListJson();
    }

    public static String readParse(String urlPath) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        String result=new String(outStream.toByteArray());// 通过out.Stream.toByteArray获取到写的数据
		System.out.println(result);
        return result;
    }



    /**
     * 解析
     *
     * @throws JSONException
     */
    private static ArrayList<HashMap<String, Object>> Analysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        /* //android默认解析器解析
        JSONArray jsonArray = null;
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("_id", jsonObject.getString("_id"));
            map.put("_name", jsonObject.getString("_name"));
            list.add(map);
        }*/

        //Gson解析
        Type listType = new TypeToken<LinkedList<Storage>>(){}.getType();
        Gson gson = new Gson();
        LinkedList<Storage> io_list = gson.fromJson(jsonStr, listType);
        for (Iterator iterator = io_list.iterator(); iterator.hasNext();) {
            Storage io_msg = (Storage) iterator.next();
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("g_id", io_msg.getG_id());
            map.put("g_name", io_msg.getG_name());
            map.put("v_type", io_msg.getV_type());
            map.put("v_amount", io_msg.getV_amount());
        //    map.put("v_time", io_msg.getV_time());
            list.add(map);
        }
        return list;
    }

    /**
     * ListView 数据适配
     */
    private void resultListJson(){
        List<HashMap<String, Object>> lists = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        try {
            lists = Analysis(readParse(url));//解析出json数据
            for(HashMap<String, Object> io : lists){
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("g_id", io.get("g_id"));
                item.put("g_name", io.get("g_name"));
                item.put("v_type", io.get("v_type"));
                item.put("v_amount", io.get("v_amount"));
   //             item.put("v_time", io.get("v_time"));
//           	 System.out.println(user.get("_id"));
                data.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.simpleAdapter = new SimpleAdapter(this, data,
                R.layout.io_list, new String[] { "g_id", "g_name","v_type","v_amount"} // Map中的key的名称,"v_time"
                , new int[] { R.id.list_id, R.id.list_name, R.id.list_type, R.id.list_amount }); // 是data_list.xml中定义的组件的资源ID,R.id.list_time
        this.listview.setAdapter(this.simpleAdapter);

    }

}


