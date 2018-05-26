package com.example.main.goods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.main.R;

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
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends Activity {
	private EditText id;
	private EditText name;
	private EditText unit;
	private EditText sto_id;
	private EditText num;
	private EditText price;
	private Button submit;
	private AddThread add_thread;
	private String url;

	/**
	 * 通过handler提示相关信息
	 */
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x11) {
				String message = (String)msg.obj;
				if(message.equals("物品添加失败")){
					Toast.makeText(AddActivity.this, message, Toast.LENGTH_LONG).show();
				}

				if(message.equals("物品添加成功")){
					Toast.makeText(AddActivity.this, message, Toast.LENGTH_LONG).show();
					Intent intent = new Intent(AddActivity.this,GoodsMsgActivity.class);
					startActivity(intent);
					AddActivity.this.finish();
				}
			}

			if(msg.what == 0x12) {
				String message = (String)msg.obj;
				if(message.equals("该物品编号已存在")){
					Toast.makeText(AddActivity.this, message, Toast.LENGTH_LONG).show();
					AddActivity.this.id.setText("");
					AddActivity.this.id.requestFocus();
					AddActivity.this.id.setHint("请输入数字字符");
				}
			}

			if(msg.what == 0x13) {
				String message = (String)msg.obj;
				if(message.equals("该物品已存在")){
					Toast.makeText(AddActivity.this, message, Toast.LENGTH_LONG).show();
					AddActivity.this.name.setText("");
					AddActivity.this.name.requestFocus();
				}
			}

			if(msg.what == 0x14) {
				String message = (String)msg.obj;
				if(message.equals("该仓库不存在")){
					Toast.makeText(AddActivity.this, message, Toast.LENGTH_LONG).show();
					AddActivity.this.sto_id.setText("");
					AddActivity.this.sto_id.requestFocus();
					AddActivity.this.sto_id.setHint("请输入数字字符");
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_add);
		initView();

	}

	/**
	 * 初始化view
	 */
	private void initView(){
		this.id = (EditText)findViewById(R.id.g_add_id);
		this.name = (EditText)findViewById(R.id.g_add_name);
		this.unit = (EditText)findViewById(R.id.g_add_unit);
		this.sto_id = (EditText)findViewById(R.id.g_add_stoid);
		this.num = (EditText)findViewById(R.id.g_add_num);
		this.price = (EditText)findViewById(R.id.g_add_price);

		this.id.setOnFocusChangeListener(new OnFocusChangeListenerImpl());
		this.name.setOnFocusChangeListener(new OnFocusChangeListenerImpl());
		this.unit.setOnFocusChangeListener(new OnFocusChangeListenerImpl());
		this.sto_id.setOnFocusChangeListener(new OnFocusChangeListenerImpl());
		this.num.setOnFocusChangeListener(new OnFocusChangeListenerImpl());
		this.price.setOnFocusChangeListener(new OnFocusChangeListenerImpl());

		this.submit = (Button)findViewById(R.id.submit);
		this.submit.setOnClickListener(new SubmitOnClickLIstenerImpl());

		url="http://10.0.84.18:8080/Storage2.0/goods!telGoodsInsert";
	}

	/**
	 * 提交事件
	 */
	private class SubmitOnClickLIstenerImpl implements OnClickListener {
		@Override
		public void onClick(View arg0) {

			String g_id = id.getText().toString();
			String g_name = name.getText().toString();
			String g_unit = unit.getText().toString();
			String g_sto_id = sto_id.getText().toString();
			String g_num = num.getText().toString();
			String g_price = price.getText().toString();
			if(TextUtils.isEmpty(g_id) || TextUtils.isEmpty(g_name) || TextUtils.isEmpty(g_unit)
					|| TextUtils.isEmpty(g_sto_id) || TextUtils.isEmpty(g_num) || TextUtils.isEmpty(g_price)){
				String str = "请输入完整信息！！！";
				Toast.makeText(AddActivity.this, str, Toast.LENGTH_SHORT).show();
			}
//			addGoods(g_id,g_name,g_unit,g_stoid,g_num,g_price);
			add_thread = new AddThread(url, g_id, g_name, g_unit, g_sto_id, g_num, g_price);
			add_thread.start();

		}
	}

	/**
	 * 增加物品线程类
	 */
	private class AddThread extends Thread {
		public String url;
		public String id;
		public String name;
		public String unit;
		public String sto_id;
		public String num;
		public String price;

		public AddThread(String url, String id, String name, String unit,
                         String sto_id, String num, String price) {
			super();
			this.url = url;
			this.id = id;
			this.name = name;
			this.unit = unit;
			this.sto_id = sto_id;
			this.num = num;
			this.price = price;
		}

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 0x11;
			try{
				String result = null;
				HttpClient httpClient = new DefaultHttpClient();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("g_id", id));
				params.add(new BasicNameValuePair("g_name", name));
				params.add(new BasicNameValuePair("g_unit", unit));
				params.add(new BasicNameValuePair("sto_id", sto_id));
				params.add(new BasicNameValuePair("g_num", num));
				params.add(new BasicNameValuePair("g_price", price));

				HttpPost request = new HttpPost(url);

				if(params != null){
					HttpEntity entity = new UrlEncodedFormEntity(params,"utf-8");
					request.setEntity(entity);
				}

				HttpResponse response = httpClient.execute(request);
				HttpEntity entity = response.getEntity();
				String json = EntityUtils.toString(entity, "utf-8");

				if(json != null){
					JSONObject jsonObject = new JSONObject(json);
					result = jsonObject.get("message").toString();

				}

				if(json == null){
					msg.obj = "物品添加失败";
				}

				if(result.equals("1")){
//					tips(name+"添加成功");
//					Intent intent = new Intent(AddActivity.this,GoodsMsgActivity.class);
//					startActivity(intent);
//					AddActivity.this.finish();
					msg.obj = "物品添加成功";
				}
				if(result.equals("0")){
//					tips(name+"物品添加失败");
					msg.obj = "物品添加失败";
				}

				myHandler.sendMessage(msg);

			} catch(ClientProtocolException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			} catch(JSONException e){
				e.printStackTrace();
			}
		}

	}

	/**
	 * 焦点事件
	 */
	private class OnFocusChangeListenerImpl implements View.OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(v.getId() == R.id.g_add_id){
				if(hasFocus){
					AddActivity.this.id.setHint("请输入数字字符");
				}else{
					if(id.getText().toString()==null||("").equals(id.getText().toString())){
						Toast.makeText(AddActivity.this,"物品编号不能为空",Toast.LENGTH_LONG).show();
					}else {
						CheckThread checkId = new CheckThread("telCheckId", "g_id", AddActivity.this.id.getText().toString(), 0x12);
						checkId.start();
					}
				}
			}

			if(v.getId() == R.id.g_add_name){
				if(hasFocus){

				}else{
					if(name.getText().toString()==null||("").equals(name.getText().toString())){
						Toast.makeText(AddActivity.this,"物品名称不能为空",Toast.LENGTH_LONG).show();
					}else {
						CheckThread checkName = new CheckThread("telCheckGoodsName", "g_name", AddActivity.this.name.getText().toString(), 0x13);
						checkName.start();
					}
				}
			}

			if(v.getId() == R.id.g_add_unit){
				if(hasFocus){

				}else{
					if(unit.getText().toString()==null||("").equals(name.getText().toString())){
						Toast.makeText(AddActivity.this,"计量单位不能为空",Toast.LENGTH_LONG).show();
					}
				}
			}

			if(v.getId() == R.id.g_add_stoid){
				if(hasFocus){

				}else{
					if(sto_id.getText().toString()==null||("").equals(name.getText().toString())){
						Toast.makeText(AddActivity.this,"仓库编号不能为空",Toast.LENGTH_LONG).show();
					}else {
						CheckThread checkSId = new CheckThread("telCheckStorageId", "sto_id", AddActivity.this.sto_id.getText().toString(), 0x14);
						checkSId.start();
					}
				}
			}

			if(v.getId() == R.id.g_add_num){
				if(hasFocus){

				}else{
					if(num.getText().toString()==null||("").equals(num.getText().toString())){
						Toast.makeText(AddActivity.this,"物品数量不能为空",Toast.LENGTH_LONG).show();
					}else{
						String n = num.getText().toString();
						String regex = "[0-9]*";
						if(!n.matches(regex)){
							Toast.makeText(AddActivity.this,"物品数量只能用数字", Toast.LENGTH_SHORT).show();
							AddActivity.this.num.setText("");
							AddActivity.this.num.setHint("物品数量只能用数字");
						}
					}
				}
			}

			if(v.getId() == R.id.g_add_price){
				if(hasFocus){

				}else{
					if(price.getText().toString()==null||("").equals(price.getText().toString())){
						Toast.makeText(AddActivity.this,"价格不能为空",Toast.LENGTH_LONG).show();
					}else{
						String p = price.getText().toString();
						String regex = "[0-9]*[.]?[0-9]*";
						if(!p.matches(regex)){
							Toast.makeText(AddActivity.this,"计划单价只能为数字", Toast.LENGTH_SHORT).show();
							AddActivity.this.price.setText("");
							AddActivity.this.price.setHint("计划单价只能用数字");
						}
					}
				}
			}
		}
	}

	/**
	 * 检查线程类
	 * 可以直接用外面的CheckThread
	 */
	public class CheckThread extends Thread {
		private static final String url = "http://10.0.84.18:8080/Storage2.0/goods!";
		private String method;
		private String type;
		private String key;
		private int set;
		public  CheckThread(String method, String type,  String key,int set ){
			this.method = method;
			this.type = type;
			this.key = key;
			this.set = set;
		}

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = set;
			try{
				String result = "";
				String flag = "";
				result = ConnectUtil.readParse(url + method + "?" + type + "=" + URLEncoder.encode(key,"UTF-8"));
				if(result != null){
					JSONObject jsonObject = new JSONObject(result);
					flag = jsonObject.getString("message").toString();
				}
				msg.obj = flag;
				myHandler.sendMessage(msg);
			} catch (Exception e){
				e.printStackTrace();
			}

		}
	}

}
