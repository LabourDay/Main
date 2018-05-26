package com.example.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
	private static  String processURL="http://10.0.84.18:8080/Storage2.0/login!telLogin";
	private EditText txUserName;
	private EditText txPassword;
	private Button btnLogin;
	private RadioGroup roles;
	private RadioButton admin;
	private RadioButton normal;
	private ProgressDialog proDia = null;
	private String role = null;
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			LoginActivity.this.proDia.dismiss();
			if(msg.what == 0x1){
				String flag = (String)msg.obj;
				if(flag.equals("1")){
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					intent.putExtra("role",role);
					startActivity(intent);
					LoginActivity.this.finish();
				}
				if(flag.equals("0")){
					Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
				}
				if(flag.equals("-1")){
					Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);

		// 设置页面布局
		setContentView(R.layout.activity_login);
		// 设置初始化视图
		initView();
		// 设置事件监听器方法
		setListener();
	}

	private void initView() {
		btnLogin = (Button) findViewById(R.id.btn_login);
		txPassword = (EditText) findViewById(R.id.password);
		roles = (RadioGroup)findViewById(R.id.role);
		admin = (RadioButton)findViewById(R.id.admin);
		normal = (RadioButton)findViewById(R.id.worker);
		txUserName = (EditText) findViewById(R.id.username);

		roles.setOnCheckedChangeListener(new OnCheckedChangeListenerImpl());
	}

	private void setListener() {
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName=txUserName.getText().toString();
				String password=txPassword.getText().toString();
				if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
					Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();;
				}else{

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("sType", role));
					params.add(new BasicNameValuePair("username", userName));
					params.add(new BasicNameValuePair("password", password));
					LoginThread l_thread = new LoginThread(processURL, params);
					l_thread.start();
					proDia = ProgressDialog.show(LoginActivity.this,
							"",
							"登录中");
				}
			}
		});
	}

	class LoginThread extends Thread{
		public String url;
		public List<NameValuePair> params;
		public LoginThread(String url,List<NameValuePair> params){
			this.url = url;
			this.params = params;
		}

		@Override
		public void run() {
			String json = null;
			String flag = null;
			Message msg = new Message();
			msg.what = 0x1;
			try{
				json = readParse(url,params);
				if(json != null){
					JSONObject jsonObject = new JSONObject(json);
					flag = jsonObject.getString("message").toString();
				}
				if(json == null){
					flag = "-1";
				}
				msg.obj = flag;
				myHandler.sendMessage(msg);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String readParse(String urlPath,List<NameValuePair> params) throws Exception {
		String result = null;
		try{
			HttpClient httpClient = new DefaultHttpClient();

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

		private class OnCheckedChangeListenerImpl implements RadioGroup.OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
			if(LoginActivity.this.admin.getId() == checkedId){
				LoginActivity.this.role = LoginActivity.this.admin.getText().toString();
			}
			if(LoginActivity.this.normal.getId() == checkedId){
				LoginActivity.this.role = LoginActivity.this.normal.getText().toString();
			}
		}
	}
}

//@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class LoginActivity extends Activity {
//	private static  String processURL="http://10.0.84.18:8080/Storage2.0/login!telLogin";
//
//	private EditText txUserName;
//	private EditText txPassword;
//	private RadioGroup roles;
//	private RadioButton admin;
//	private RadioButton normal;
//	private Button btnLogin;
//	private String role = null;
//	@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		///在Android2.2以后必须添加以下代码
//		//本应用采用的Android4.0
//		//设置线程的策略
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads()
//				.detectDiskWrites()
//				.detectNetwork()   // or .detectAll() for all detectable problems
//				.penaltyLog()
//				.build());
//		//设置虚拟机的策略
//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//				.detectLeakedSqlLiteObjects()
//				//.detectLeakedClosableObjects()
//				.penaltyLog()
//				.penaltyDeath()
//				.build());
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//
//		//设置页面布局
//		setContentView(R.layout.activity_login);
//		//设置初始化视图
//		initView();
//		//设置事件监听器方法
//		setListener();
//	}
//	private void initView() {
//		btnLogin= (Button) findViewById(R.id.btn_login);
//		txPassword = (EditText) findViewById(R.id.password);
//		roles = (RadioGroup)findViewById(R.id.role);
//		admin = (RadioButton)findViewById(R.id.admin);
//		normal = (RadioButton)findViewById(R.id.worker);
//		txUserName = (EditText) findViewById(R.id.username);
//
//		roles.setOnCheckedChangeListener(new OnCheckedChangeListenerImpl());
//	}
//	private void setListener() {
//		btnLogin.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String userName=txUserName.getText().toString();
//				String password=txPassword.getText().toString();
//				if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
//					Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();;
//				}else{
//					loginRemoteService(userName,password);
//				}
//			}
//		});
//	}
//	/**
//	 * 获取Struts2 Http 登录的请求信息
//	 * @param  userName
//	 * @param  password
//	 */
//	public void loginRemoteService(String userName,String password){
//		ProgressDialog proDia = ProgressDialog.show(this,
//				"",
//				"登录中...");
//		proDia.show();
//		String result=null;
//		try {
//
//			//创建一个HttpClient对象
//			HttpClient httpclient = new DefaultHttpClient();
//			//远程登录URL
//
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("sType", role));
//			params.add(new BasicNameValuePair("username", userName)); // 用户名
//			params.add(new BasicNameValuePair("password", password)); // 密码
//
//			Log.d("远程URL", processURL);
//
//			//创建HttpGet对象
//			HttpPost request=new HttpPost(processURL);
//			//请求信息类型MIME每种响应类型的输出（普通文本、html 和 XML，json）。允许的响应类型应当匹配资源类中生成的 MIME 类型
//			//资源类生成的 MIME 类型应当匹配一种可接受的 MIME 类型。如果生成的 MIME 类型和可接受的 MIME 类型不 匹配，那么将
//			//生成 com.sun.jersey.api.client.UniformInterfaceException。例如，将可接受的 MIME 类型设置为 text/xml，而将
//			//生成的 MIME 类型设置为 application/xml。将生成 UniformInterfaceException。
//			request.addHeader("Accept","text/json");
//
//			if(params != null){
//				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//				request.setEntity(entity);
//			}
//
//			//获取响应的结果
//			HttpResponse response =httpclient.execute(request);
//			//获取HttpEntity
//			HttpEntity entity=response.getEntity();
//			//获取响应的结果信息
//			String json = EntityUtils.toString(entity,"UTF-8");
//			//JSON的解析过程
//			if(json!=null){
//				JSONObject jsonObject=new JSONObject(json);
//				result=jsonObject.get("message").toString();
//				Log.d("结果--------------",result);
//			}
//			if(result==null){
//				json="登录失败请重新登录";
//				proDia.dismiss();
//                Toast.makeText(LoginActivity.this,"登录失败请重新登录",Toast.LENGTH_SHORT).show();
//			}
//			if("1".equals(result)){
//				proDia.dismiss();
//				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//				intent.putExtra("role",role);
//				startActivity(intent);
//				LoginActivity.this.finish();
//			}
//			else if("0".equals(result)){
//				proDia.dismiss();
//				Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
//				LoginActivity.this.txUserName.setText("");
//				LoginActivity.this.txPassword.setText("");
//
//			}
//			/*//创建提示框提醒是否登录成功
//			 AlertDialog.Builder builder=new Builder(LoginActivity.this);
//			 builder.setTitle("提示")
//			 .setMessage(result)
//			 .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}
//			}).create().show();*/
//
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private class OnCheckedChangeListenerImpl implements RadioGroup.OnCheckedChangeListener{
//		@Override
//		public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//			if(LoginActivity.this.admin.getId() == checkedId){
//				LoginActivity.this.role = LoginActivity.this.admin.getText().toString();
//			}
//			if(LoginActivity.this.normal.getId() == checkedId){
//				LoginActivity.this.role = LoginActivity.this.normal.getText().toString();
//			}
//		}
//	}
//}
