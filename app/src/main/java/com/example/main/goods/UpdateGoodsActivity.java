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
import android.widget.TextView;
import android.widget.Toast;

import com.example.main.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UpdateGoodsActivity extends Activity {
	private TextView p_name;
	private EditText ud_id;
	private EditText ud_name;
	private EditText ud_unit;
	private EditText ud_stoid;
	private EditText ud_num;
	private EditText ud_price;
	private Button ud_submit;
	private String url;

	/**
	 * 通过handler更新界面
	 */
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x1){
				ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String,Object>>();
				datalist = (ArrayList<HashMap<String,Object>>)msg.obj;
				Iterator<HashMap<String, Object>> it = datalist.iterator();
				while(it.hasNext()) {
					Map<String, Object> map = it.next();
					UpdateGoodsActivity.this.p_name.setText(""+map.get("_name"));
					UpdateGoodsActivity.this.ud_name.setText("" + map.get("_name"));
					UpdateGoodsActivity.this.ud_unit.setText("" + map.get("_unit"));
					UpdateGoodsActivity.this.ud_stoid.setText("" + map.get("_stoid"));
					UpdateGoodsActivity.this.ud_num.setText("" + map.get("_num"));
					UpdateGoodsActivity.this.ud_price.setText("" + map.get("_price"));
				}

			}
			if(msg.what == 0x2){
				String flag = (String) msg.obj;
				if(flag.equals("1")){
					Toast.makeText(UpdateGoodsActivity.this, "修改成功", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(UpdateGoodsActivity.this,GoodsMsgActivity.class);
					startActivity(intent);
					UpdateGoodsActivity.this.finish();
				}
				if(flag.equals("0")){
					Toast.makeText(UpdateGoodsActivity.this, "修改失败", Toast.LENGTH_LONG).show();
				}
			}

			if(msg.what == 0x3){
				String result = (String)msg.obj;
				if(result.equals("该物品已存在")){
					toast(result);
					UpdateGoodsActivity.this.ud_name.setText("");
					UpdateGoodsActivity.this.ud_name.requestFocus();
				}
			}

            if(msg.what == 0x4){
                String result = (String)msg.obj;
                if(result.equals("该仓库不存在")){
                    toast(result);
                    UpdateGoodsActivity.this.ud_stoid.setText("");
                    UpdateGoodsActivity.this.ud_stoid.requestFocus();
                }
            }

		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_update);
		this.p_name = (TextView)findViewById(R.id.p_name);
		this.ud_id = (EditText)findViewById(R.id.g_update_id);
		this.ud_name = (EditText)findViewById(R.id.g_update_name);
		this.ud_unit = (EditText)findViewById(R.id.g_update_unit);
		this.ud_stoid = (EditText)findViewById(R.id.g_update_stoid);
		this.ud_num = (EditText)findViewById(R.id.g_update_num);
		this.ud_price = (EditText)findViewById(R.id.g_update_price);
		Intent intent = getIntent();
		String id = intent.getStringExtra("id");
		this.ud_id.setText(""+id);
		this.ud_submit = (Button)findViewById(R.id.update);

		url="http://10.0.84.18:8080/Storage2.0/goods!telGoodsFindId?Id="+id;
		OriginalThread o_thread = new OriginalThread(url);
		o_thread.start();

		this.ud_name.setOnFocusChangeListener(new OnFocusChangeListenerImp());
		this.ud_unit.setOnFocusChangeListener(new OnFocusChangeListenerImp());
		this.ud_stoid.setOnFocusChangeListener(new OnFocusChangeListenerImp());
		this.ud_num.setOnFocusChangeListener(new OnFocusChangeListenerImp());
		this.ud_price.setOnFocusChangeListener(new OnFocusChangeListenerImp());

		this.ud_submit.setOnClickListener(new SubmitOnClickLIstenerImpl());
	}

	/**
	 * 获取原来的数据线程类
	 */
	class OriginalThread extends Thread {
		public String url;
		public OriginalThread(String url){
			this.url = url;
		}

		@Override
		public void run() {
			List<HashMap<String, Object>> lists = null;
			List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
			Message msg = new Message();
			msg.what=0x1;
			try {
				lists = ConnectUtil.Analysis(ConnectUtil.readParse(url));//解析出json数据
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

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 提交事件
	 */
	private class SubmitOnClickLIstenerImpl implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String g_id = ud_id.getText().toString();
			String g_name = ud_name.getText().toString();
			String g_unit = ud_unit.getText().toString();
			String g_stoid = ud_stoid.getText().toString();
			String g_num = ud_num.getText().toString();
			String g_price = ud_price.getText().toString();
			if(TextUtils.isEmpty(g_id) || TextUtils.isEmpty(g_name) || TextUtils.isEmpty(g_unit)
					|| TextUtils.isEmpty(g_stoid) || TextUtils.isEmpty(g_num) || TextUtils.isEmpty(g_price)){
				String str = "请输入完整信息！！！";
				Toast.makeText(UpdateGoodsActivity.this, str, Toast.LENGTH_LONG).show();
			}

			//访问参数
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("g_id", g_id));
			params.add(new BasicNameValuePair("g_name", g_name));
			params.add(new BasicNameValuePair("g_unit", g_unit));
			params.add(new BasicNameValuePair("sto_id", g_stoid));
			params.add(new BasicNameValuePair("g_num", g_num));
			params.add(new BasicNameValuePair("g_price", g_price));

			//访问地址
			url="http://10.0.84.18:8080/Storage/goods!telGoodsUpdate";

			//new一个更新线程类并开启线程
			UpdatedThread u_thread = new UpdatedThread(url, params);
			u_thread.start();

		}
	}

	/**
	 * 更新物品线程类
	 */
	class UpdatedThread extends Thread {
		public String url;
		public List<NameValuePair> params;
		public UpdatedThread(String url, List<NameValuePair> params){
			this.url = url;
			this.params = params;
		}
		@Override
		public void run() {
			String result = null;
			String flag = "";
			Message msg = new Message();
			msg.what = 0x2;
			try {
				result = ConnectUtil.readParse(url,params);
				if(result != null){
					JSONObject json = new JSONObject(result);
					flag = json.get("message").toString();
				}
				if(result == null){
					flag = "0";
				}

				msg.obj = flag;
				myHandler.sendMessage(msg);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	/**
	 * 焦点事件
	 */
	private class  OnFocusChangeListenerImp implements View.OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			switch (v.getId())
			{
				case  R.id.g_update_name:

					String name = ud_name.getText().toString();
					if (hasFocus) {

					} else{
						if(name == null || "".equals(name)){
							toast("物品名称不能为空");
						}else {
							if (!name.equals(p_name.getText().toString())) {
								CheckThread checkName = new CheckThread("telCheckGoodsName", "g_name",
										name, 0x3, myHandler);
								checkName.start();
							}
						}
					}
					break;

                case R.id.g_update_unit:
                    String unit = ud_unit.getText().toString();
                    if(hasFocus){

                    }else{
                        if(unit==null || "".equals(unit)){
                            toast("计量单位不能为空");
                        }
                    }
                    break;

                case R.id.g_update_stoid:
                    String sid = ud_stoid.getText().toString();
                    if(hasFocus){

                    }else{
                        if(sid==null || "".equals(sid)){
                            toast("仓库编号不能为空");
                        }else{
                            CheckThread checkSId = new CheckThread("telCheckStorageId", "sto_id",sid, 0x4,myHandler);
                            checkSId.start();
                        }
                    }
                    break;

                case R.id.g_update_num:
                    if(hasFocus){

                    }else{
                        String n = ud_num.getText().toString();
                        String regex = "[0-9]*";
						if(n == null || "".equals(n)){
							toast("物品数量不能为空");
						}else {
							if (!n.matches(regex)) {
								UpdateGoodsActivity.this.ud_num.setText("");
								UpdateGoodsActivity.this.ud_num.setHint("物品数量只能用数字");
							}
						}
                    }
                    break;

				case R.id.g_update_price:
					if(hasFocus){

					}else{
						String price = ud_price.getText().toString();
						String regex = "[0-9]*[.]?[0-9]*";
						if(price == null || "".equals(price)){
							toast("计划单价不能为空");
						}else{
							if(!price.matches(regex)){
								toast("计划单价只能为数字");
								UpdateGoodsActivity.this.ud_price.setText("");
								UpdateGoodsActivity.this.ud_price.setHint("计划单价只能为数字");
							}
						}
					}
					break;
			}
		}
	}

	/**
	 *toast方法
	 * @param str 要提示的内容
	 */
	private void  toast(String str){
		Toast.makeText(UpdateGoodsActivity.this, str, Toast.LENGTH_SHORT).show();
	}
}
