package com.example.main.goods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.main.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindResultActivity extends Activity {
	private ListView f_list = null;
	private SimpleAdapter simpleAdapter;
	private String url;
	private FindResultThread f_thread;

	/**
	 * 通过handler更新界面
	 */
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x12) {
				ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String, Object>>();
				datalist = (ArrayList<HashMap<String, Object>>) msg.obj;
				FindResultActivity.this.simpleAdapter = new SimpleAdapter(
						FindResultActivity.this, datalist, R.layout.data_list,
                        new String[] { "_id", "_name", "_unit","_stoid", "_num", "_price" } // Map中的key的名称
						, new int[] { R.id._id, R.id._name, R.id._unit,R.id._stoid, R.id._num, R.id._price }); // 是data_list.xml中定义的组件的资源ID
				FindResultActivity.this.f_list
						.setAdapter(FindResultActivity.this.simpleAdapter);
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_findresult);
		this.f_list = (ListView)findViewById(R.id.f_datalist);

		Intent intent = getIntent();
		String key = intent.getStringExtra("key");
//		try {
//			url = "http://10.0.84.18:8080/Storage/goods!telGoodsSearch?strKey="+URLEncoder.encode(key,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		url = "http://10.0.84.18:8080/Storage2.0/goods!telGoodsSearch";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("strKey", key));
		f_thread = new FindResultThread(url,params);
		f_thread.start();

	}

	/**
	 * 查找结果线程类
	 */
	class FindResultThread extends Thread {
		public String url;						//访问地址
		public List<NameValuePair> params;		//访问参数
		public FindResultThread(String url, List<NameValuePair> params){
			this.url = url;
			this.params = params;
		}

		@Override
		public void run() {
			List<HashMap<String, Object>> lists = null;
			List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
			Message msg = new Message();
			msg.what = 0x12;
			try {
				lists = ConnectUtil.Analysis(ConnectUtil.readParse(url,params));//解析出json数据

				//将解析的数据放到map集合之后再放到list
				for(HashMap<String, Object> goods : lists){
					HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("_id", goods.get("_id"));
                    item.put("_name", goods.get("_name"));
                    item.put("_unit", goods.get("_unit"));
                    item.put("_stoid", goods.get("_stoid"));
                    item.put("_num", goods.get("_num"));
                    item.put("_price", goods.get("_price"));
					data.add(item);
				}

				msg.obj = data;
				myHandler.sendMessage(msg);

			}  catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
