package com.example.main.storage_manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.main.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class InfoStoActivity extends Activity {
	private  String url=null;
	private EditText txtfind = null;
	private Button btnfind = null;
	private Button btnadd = null;
	private ListView listview=null;
	private SimpleAdapter simpleAdapter = null; // 进行数据的转换操作
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sto_msg);
		listview = (ListView) super.findViewById(R.id.sto_datalist);
		url="http://10.0.84.18:8080/Storage2.0/TelStoAction!telStoSelect";

		this.txtfind = (EditText)findViewById(R.id.sto_key);
		this.btnfind = (Button)findViewById(R.id.sto_find);
		this.btnadd = (Button)findViewById(R.id.sto_add);
		this.btnfind.setOnClickListener(new FindStoOnClickListenr());

		this.btnadd.setOnClickListener(new AddStoListenerImpl());
		this.listview.setOnItemClickListener(new UpdateStoOnItemClickListener());

		this.listview.setOnItemLongClickListener(new DeleteStoItemLongClicListener());

		//强行让4.0版本 不去检查主线程访问网络。
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		resultListJson();

	}

	/**
	 * ListView 数据适配
	 */
	private void resultListJson(){
		List<HashMap<String, Object>> lists = null;
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
		try {
			lists = StoConnectUtil.Analysis(StoConnectUtil.readParse(url));//解析出json数据
			for(HashMap<String, Object> sto : lists){
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("sto_id", sto.get("sto_id"));
				item.put("sto_name", sto.get("sto_name"));
				item.put("sto_type", sto.get("sto_type"));
				item.put("sto_money", sto.get("sto_money"));
				item.put("sto_addr", sto.get("sto_addr"));
//	           	 System.out.println(user.get("_id"));
				data.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.simpleAdapter = new SimpleAdapter(this, data,
				R.layout.sto_date_list, new String[] { "sto_id", "sto_name", "sto_type","sto_money", "sto_addr"} // Map中的key的名称
				, new int[] { R.id._stoid, R.id._stoname, R.id._stotype, R.id._stomoney, R.id._stoaddr}); // 是data_list.xml中定义的组件的资源ID
		this.listview.setAdapter(this.simpleAdapter);

	}

	private class FindStoOnClickListenr implements OnClickListener{
		@Override
		public void onClick(View view) {
			String key = InfoStoActivity.this.txtfind.getText().toString();
			if(TextUtils.isEmpty(key)){
				Toast.makeText(InfoStoActivity.this, "请输入关键字", Toast.LENGTH_LONG).show();
			}else{
				Intent intent = new Intent(InfoStoActivity.this, FindStoActivity.class);
				intent.putExtra("key", key);
				startActivity(intent);
			}
		}
	}

	private class AddStoListenerImpl implements OnClickListener{

		@Override
		public void onClick(View v) {

			Intent intent = new Intent(InfoStoActivity.this,AddStoActivity.class);
			startActivity(intent);

		}
	}

	private class UpdateStoOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
			// TODO Auto-generated method stub
			Map<String , Integer> map=(Map<String, Integer>)InfoStoActivity.this								//取得列表项
					.simpleAdapter.getItem(position);
			String upid = map.get("sto_id").toString();
			Intent intent = new Intent(InfoStoActivity.this, UpdateStoActivity.class);
			intent.putExtra("id", upid);
			startActivity(intent);
		}

	}

	private class DeleteStoItemLongClicListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
			Map<String , Integer> map=(Map<String, Integer>)InfoStoActivity.this.simpleAdapter.getItem(position);//取得列表项
			final String stoid = map.get("sto_id").toString();
				/*Dialog dialog = */
			new AlertDialog.Builder(InfoStoActivity.this)
					.setTitle("删除仓库信息")
					.setMessage("您确定删除该条信息吗？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							InfoStoActivity.this.deleteSto(stoid);
							Intent intent = new Intent(InfoStoActivity.this,InfoStoActivity.class);
							startActivity(intent);
							InfoStoActivity.this.finish();

						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {


						}
					})
					.show();
			return true;
		}
	}

	private void deleteSto(String did){
		final String id = did;

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				url="http://10.0.84.18:8080/Storage2.0/TelStoAction!telStoDelete";
				Looper.prepare();//子线程用Toast必须用
				HttpClient httpClient = new DefaultHttpClient();
				String result = null;
				try{
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("id", id));
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
						Intent intent = new Intent(InfoStoActivity.this,InfoStoActivity.class);
						startActivity(intent);
						InfoStoActivity.this.finish();
					}

					if(json == null){
						tips("仓库删除失败");
					}

					if(result.equals("1")){
						tips(id+"仓库删除成功");
						Intent intent = new Intent(InfoStoActivity.this,InfoStoActivity.class);
						startActivity(intent);
						InfoStoActivity.this.finish();
					}
					if(result.equals("0")){
						tips(id+"仓库删除失败");
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
		}).start();


	}

	private void tips(String str){
		Toast.makeText(InfoStoActivity.this, str, Toast.LENGTH_LONG).show();
	}
}
