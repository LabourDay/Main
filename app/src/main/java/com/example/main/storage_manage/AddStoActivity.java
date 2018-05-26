package com.example.main.storage_manage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.util.ArrayList;
import java.util.List;

public class AddStoActivity extends Activity {
	private EditText id;
	private EditText name;
	private EditText type;
	private EditText money;
	private EditText addr;
	private Button submit;

	
	private String url="http://10.0.84.18:8080/Storage2.0/TelStoAction!telStoAdd";
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.sto_add);
			
			this.id = (EditText) findViewById(R.id.sto_add_id);
			this.name = (EditText) findViewById(R.id.sto_add_name);
			this.type = (EditText) findViewById(R.id.sto_add_type);
			this.money = (EditText) findViewById(R.id.sto_add_money);
			this.addr = (EditText) findViewById(R.id.sto_add_addr);
			this.submit = (Button) findViewById(R.id.sto_add_submit);
			
			this.submit.setOnClickListener(new SubmitAddOnClickLIstenerImpl());
		}
		
		@SuppressLint("NewApi") private class SubmitAddOnClickLIstenerImpl implements OnClickListener{

			@SuppressLint("NewApi") @Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
			    StrictMode.setThreadPolicy(policy);
			    
			    
			    String subid = id.getText().toString();
			    String subname = name.getText().toString();
			    String subtype = type.getText().toString();
			    String submoney = money.getText().toString();
			    String subaddr = addr.getText().toString();
			    if(TextUtils.isEmpty(subid) || TextUtils.isEmpty(subname) || TextUtils.isEmpty(subtype)
						|| TextUtils.isEmpty(submoney) || TextUtils.isEmpty(subaddr) ){
					String str = "输入不能为空！";
					tips(str);
				}
			    
				addSto(subid,subname,subtype,submoney,subaddr);
			}
			
		}
		
		private void addSto(String id,String name,String type,String money,String addr){
			String result = null;
			try{
				HttpClient httpClient = new DefaultHttpClient();
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("sto_id", id));
				params.add(new BasicNameValuePair("sto_name", name));
				params.add(new BasicNameValuePair("sto_type", type));
				params.add(new BasicNameValuePair("sto_money", money));
				params.add(new BasicNameValuePair("sto_addr", addr));
				
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
					tips("仓库信息添加失败！");
				}
				
				if(result.equals("1")){
					tips(name+"仓库信息添加成功！");
					Intent intent = new Intent(AddStoActivity.this,InfoStoActivity.class);
					startActivity(intent);
					AddStoActivity.this.finish();
				}
				if(result.equals("0")){
					tips(name+"仓库信息添加失败！");
				}
			} catch(ClientProtocolException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			} catch(JSONException e){
				e.printStackTrace();
			}
		}
		
		private void tips(String str){
			Toast.makeText(AddStoActivity.this, str, Toast.LENGTH_LONG).show();
		}
}
