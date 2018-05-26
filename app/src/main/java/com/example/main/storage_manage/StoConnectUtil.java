package com.example.main.storage_manage;

import com.example.main.entity.WareHouse;
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

public class StoConnectUtil {
	public static String readParse(String urlPath) throws Exception {
		System.out.println(urlPath);
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
		String result=new String(outStream.toByteArray());// ͨ��out.Stream.toByteArray��ȡ��д������
		System.out.println(result);
		return result;
	}
	
	public static String readParse(String urlPath,List<NameValuePair> params) throws Exception {
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
		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
     * ����
     * 
     * @throws JSONException
     */
    public static ArrayList<HashMap<String, Object>> Analysis(String jsonStr)
            throws JSONException {
        /******************* ���� ***********************/
        // ��ʼ��list�������
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        //Gson����
        Type listType = new TypeToken<LinkedList<WareHouse>>(){}.getType();
        Gson gson = new Gson();
        LinkedList<WareHouse> goodses = gson.fromJson(jsonStr, listType);
        for (Iterator<WareHouse> iterator = goodses.iterator(); iterator.hasNext();) {
        	WareHouse sto = (WareHouse) iterator.next();
       	 // ��ʼ��map�������
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("sto_id", sto.getSto_id());
            map.put("sto_name", sto.getSto_name());
            map.put("sto_type", sto.getSto_type());
            map.put("sto_money", sto.getSto_money());
            map.put("sto_addr", sto.getSto_addr());
            list.add(map);
        }
        return list;
    }
}
