package com.example.main.yuangong;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.main.R;
import com.example.main.entity.yuangong;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class FindResultActitivity extends Activity {
	private ListView f_list = null;
	private SimpleAdapter simpleAdapter;
	private String url;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		//��ת��layout.activity_yuangong_findresult
		setContentView(R.layout.activity_yuangong_findresult);
//		��ֵ��ǰ̨չʾ
		this.f_list = (ListView)findViewById(R.id.f_datalist);
	
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Intent intent = getIntent();
//		��ȡ�ոմ�������keyֵ<!--�����ֶβ�ѯ���-->
		String key = intent.getStringExtra("key");
//		����yuangong action��telyuangongSearch����
		url = "http://10.0.84.18:8080/Storage2.0/yuangong!telyuangongSearch";
		resultListJson(key); 
	}
	
	/*public static String readParse(String urlPath) throws Exception {
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
//		System.out.println(result);
		return result;
	}*/
	public static String readParse(String urlPath,String key) throws Exception {
		String result = null;
		try{
			HttpClient httpClient = new DefaultHttpClient();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("strKey", key));
			
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
    private static ArrayList<HashMap<String, Object>> Analysis(String jsonStr)
            throws JSONException {
        /******************* ���� ***********************/
        // ��ʼ��list�������
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        
        //Gson����  ȡ������
        Type listType = new TypeToken<LinkedList<yuangong>>(){}.getType();
        Gson gson = new Gson();
        LinkedList<yuangong> goodses = gson.fromJson(jsonStr, listType);
        for (Iterator iterator = goodses.iterator(); iterator.hasNext();) {
			yuangong goods = (yuangong) iterator.next();
       	 // ��ʼ��map�������
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("_id", goods.getW_id());
            map.put("_username", goods.getW_username());
            map.put("_password", goods.getW_password());
            map.put("_truename", goods.getW_truename());
            map.put("_tel", goods.getW_tel());
            map.put("_mail", goods.getW_mail());
			map.put("_num", goods.getL_num());
            list.add(map);
        }
        return list;
    }
    
    /**
     ListView ��������  �������ʼ��map�������󣬴�json������ȡ�������ݽ���ListView��������  Ϊ�������õ�ǰ̨ʹ��
     */
    private void resultListJson(String key){ 
        List<HashMap<String, Object>> lists = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        try {
            lists = Analysis(readParse(url,key));//������json����
            for(HashMap<String, Object> goods : lists){
           	 HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("_id", goods.get("_id"));
				item.put("_username", goods.get("_username"));
				item.put("_password", goods.get("_password"));
				item.put("_truename", goods.get("_truename"));
				item.put("_tel", goods.get("_tel"));
				item.put("_mail", goods.get("_mail"));
				item.put("_num", goods.get("_num"));
//           	 System.out.println(user.get("_id"));
           	 data.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//		Ϊ�������õ�ǰ̨ʹ��     �������������json���� ���͵�  data_list.xml��ǰ̨��������ʾ  (��������)
        this.simpleAdapter = new SimpleAdapter(this, data,
				R.layout.w_data_list, new String[] {"_id", "_username", "_password","_truename", "_tel", "_mail" , "_num" } // Map�е�key������
				, new int[] {  R.id._id, R.id._username, R.id._password, R.id._truename, R.id._tel, R.id._mail, R.id._num  }); // ��data_list.xml�ж�����������ԴID
		 this.f_list.setAdapter(this.simpleAdapter);

    }
	
	
}
