package com.example.main.goods;


import android.util.Log;

import com.example.main.entity.Goods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * Created by WZH on 2017/12/1
 */

public class ConnectUtil {
	/**
	 * 只带地址访问服务器并接收数据的方法
	 * @param urlPath  访问地址
	 * @return	返回从服务器端获取的结果
	 * @throws Exception
	 */
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
//		System.out.println(result);
		return result;
	}

	/**
	 * 带访问地址和参数并接收数据的方法
	 * @param urlPath 访问地址
	 * @param params  访问参数
	 * @return
	 * @throws Exception
	 */
	public static String readParse(String urlPath, List<NameValuePair> params) throws Exception {
		String result = null;
		try{
			HttpClient httpClient = new DefaultHttpClient();

//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("strKey", key));

			HttpPost request = new HttpPost(urlPath);

			if(params != null){
				HttpEntity entity = new UrlEncodedFormEntity(params,"utf-8");
				request.setEntity(entity);
			}

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "utf-8");
			Log.e("message",result);
		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 解析
	 *
	 * @throws JSONException
	 */
	public static ArrayList<HashMap<String, Object>> Analysis(String jsonStr)
			throws JSONException {
		/******************* 解析 ***********************/
		// 初始化list数组对象
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		//Gson解析
		Type listType = new TypeToken<LinkedList<Goods>>(){}.getType();
		Gson gson = new Gson();
		LinkedList<Goods> goodses = gson.fromJson(jsonStr, listType);
		for (Iterator iterator = goodses.iterator(); iterator.hasNext();) {
			Goods goods = (Goods) iterator.next();
			// 初始化map数组对象
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("_id", goods.getG_id());
			map.put("_name", goods.getG_name());
			map.put("_unit", goods.getG_unit());
			map.put("_stoid", goods.getSto_id());
			map.put("_num", goods.getG_num());
			map.put("_price", goods.getG_price());
			list.add(map);
		}
		return list;
	}
}
