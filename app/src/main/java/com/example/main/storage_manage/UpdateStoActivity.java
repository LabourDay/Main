package com.example.main.storage_manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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



import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.main.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class UpdateStoActivity extends Activity {
	private EditText ud_id;
	private EditText ud_name;
	private EditText ud_type;
	private EditText ud_money;
	private EditText ud_addr;
	private Button ud_submit;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.sto_update);
		this.ud_id = (EditText) findViewById(R.id.sto_update_id);
		this.ud_name = (EditText) findViewById(R.id.sto_update_name);
		this.ud_type = (EditText) findViewById(R.id.sto_update_type);
		this.ud_money = (EditText) findViewById(R.id.sto_update_money);
		this.ud_addr = (EditText) findViewById(R.id.sto_update_addr);
		this.ud_submit = (Button) findViewById(R.id.sto_update_submit);
		this.ud_submit.setOnClickListener(new SubmitStoOnClickLIstenerImpl());
		Intent intent = getIntent();
		String id = intent.getStringExtra("id");
		this.ud_id.setText(""+id);
		url="http://10.0.84.18:8080/Storage2.0/TelStoAction!telStoSelectById?id="+id;

		/*StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);*/

		resultTextJson();
	}

	private void resultTextJson(){
		try{
			List<HashMap<String, Object>> allData = StoConnectUtil.Analysis(StoConnectUtil.readParse(url));
			Iterator<HashMap<String, Object>> it = allData.iterator();

			while(it.hasNext()){
				Map<String, Object> ma = it.next();
				UpdateStoActivity.this.ud_name.setText(""+ma.get("sto_name"));
				UpdateStoActivity.this.ud_type.setText(""+ma.get("sto_type"));
				UpdateStoActivity.this.ud_money.setText(""+ma.get("sto_money"));
				UpdateStoActivity.this.ud_addr.setText(""+ma.get("sto_addr"));
			}
		}catch (JSONException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private class SubmitStoOnClickLIstenerImpl implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String id = ud_id.getText().toString();
					String name = ud_name.getText().toString();
					String type = ud_type.getText().toString();
					String money = ud_money.getText().toString();
					String addr = ud_addr.getText().toString();
					if(TextUtils.isEmpty(id) || TextUtils.isEmpty(name) || TextUtils.isEmpty(type)
							|| TextUtils.isEmpty(money) || TextUtils.isEmpty(addr)){
						String str = "请输入完整信息！！！";
						Toast.makeText(UpdateStoActivity.this, str, Toast.LENGTH_LONG).show();
					}else{
						updateSto(id,name,type,money,addr);
					}
				}
			}).start();

		}

	}

	private void updateSto(String id,String name,String type,String money,String addr){
		String result = null;
		url="http://10.0.84.18:8080/Storage2.0/TelStoAction!telStoUpdate";
		Looper.prepare();//子线程用Toast必须用
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
				Toast.makeText(UpdateStoActivity.this, "修改失败", Toast.LENGTH_LONG).show();
			}

			if(result.equals("1")){
				Toast.makeText(UpdateStoActivity.this, "修改成功", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(UpdateStoActivity.this,InfoStoActivity.class);
				startActivity(intent);
				UpdateStoActivity.this.finish();
			}
			if(result.equals("0")){
				Toast.makeText(UpdateStoActivity.this, "修改失败", Toast.LENGTH_LONG).show();
			}
		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		} catch(JSONException e){
			e.printStackTrace();
		}
		Looper.loop();//子线程用Toast必须用
	}

}
