package com.example.main.goods;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.main.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsMsgActivity extends Activity {
	private String url=null;
	private EditText txtfind = null;
	private Button btnfind = null;
	private Button btnadd = null;
	private ListView listview=null;
	private SimpleAdapter simpleAdapter = null; // 进行数据的转换操作
	private GoodsMsgThread g_thread;

	/**
	 * 通过handler更新界面
	 */
	public Handler myHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x11) {
//				Bundle bundle = msg.getData();
//				ArrayList datalist = bundle.getParcelableArrayList("list");
//				ArrayList datalist2 = (ArrayList<HashMap<String,Object>>)datalist;
				ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String,Object>>();
				datalist = (ArrayList<HashMap<String,Object>>)msg.obj;
				GoodsMsgActivity.this.simpleAdapter = new SimpleAdapter(GoodsMsgActivity.this, datalist,
						R.layout.data_list, new String[] { "_id", "_name", "_unit","_stoid", "_num", "_price" } // Map中的key的名称
						, new int[] { R.id._id, R.id._name, R.id._unit, R.id._stoid, R.id._num, R.id._price }); // 是data_list.xml中定义的组件的资源ID
				GoodsMsgActivity.this.listview.setAdapter( GoodsMsgActivity.this.simpleAdapter);
				GoodsMsgActivity.this.btnfind.setOnClickListener(new FindGoodsOnClickListenr());
				GoodsMsgActivity.this.btnadd.setOnClickListener(new AddGoodsListenerImpl());
			}

			if(msg.what == 0x13){
				String flag = (String) msg.obj;
				if(flag.equals("1")){
					Toast.makeText(GoodsMsgActivity.this, "删除成功", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(GoodsMsgActivity.this,GoodsMsgActivity.class);
					startActivity(intent);
					GoodsMsgActivity.this.finish();
//					GoodsMsgActivity.this.simpleAdapter.notifyDataSetChanged();
//					GoodsMsgActivity.this.listview.setAdapter(simpleAdapter);
				}
				if(flag.equals("0")){
					Toast.makeText(GoodsMsgActivity.this, "删除失败", Toast.LENGTH_LONG).show();
				}
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_msg);
//		initView();
		this.txtfind = (EditText)findViewById(R.id.key);
		this.btnadd = (Button)findViewById(R.id.add);
		this.btnfind = (Button)findViewById(R.id.find);

		listview = (ListView) super.findViewById(R.id.datalist);
		//访问地址，根据实际吧ip地址改为自己电脑ip地址，端口号改为服务器端口号，项目名和方法也一样（本程序其他地方也一样）
		url="http://10.0.84.18:8080/Storage2.0/goods!telGoodsMessage";
		/*myHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x11) {

					ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String,Object>>();
					datalist = (ArrayList<HashMap<String,Object>>)msg.obj;
					   GoodsMsgActivity.this.simpleAdapter = new SimpleAdapter(GoodsMsgActivity.this, datalist,
							R.layout.data_list, new String[] { "_id", "_name", "_unit","_stoid", "_num", "_price" } // Map中的key的名称
								, new int[] { R.id._id, R.id._name, R.id._unit, R.id._stoid, R.id._num, R.id._price }); // 是data_list.xml中定义的组件的资源ID
				        GoodsMsgActivity.this.listview.setAdapter( GoodsMsgActivity.this.simpleAdapter);
				        GoodsMsgActivity.this.btnfind.setOnClickListener(new FindGoodsOnClickListenr());
				        GoodsMsgActivity.this.btnadd.setOnClickListener(new AddGoodsListenerImpl());
				}
			}

		};*/

		this.listview.setOnItemClickListener(new DeleteGoodsOnItemClickImpl());
		this.listview.setOnItemLongClickListener(new UpdateOnItemClickImpl());
		/*this.listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
//				url="http://10.0.84.18:8080/Storage/goods!telGoodsDelete";
//				DeleteThread d_thread = new DeleteThread(url);
//				d_thread.start();
				Map<String , String> map=(Map<String, String>)GoodsMsgActivity.this								//取得列表项
						.simpleAdapter.getItem(position);
				final String gid = map.get("_id");
				Dialog dialog = new AlertDialog.Builder(GoodsMsgActivity.this)
									.setTitle("删除物品信息")
									.setMessage("您确定删除该条信息吗？")
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {


										}
									})
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {


										}
									})
									.show();
			}

		});*/

		g_thread = new GoodsMsgThread(url);
		g_thread.start();
//		GoodsMsgActivity.this.btnfind.setOnClickListener(new FindGoodsOnClickListenr());
//	    GoodsMsgActivity.this.btnadd.setOnClickListener(new AddGoodsListenerImpl());
	}

//	private void initView(){
//
//	}

	/**
	 * 查找所有物品信息线程类
	 */
	class GoodsMsgThread extends Thread {
		public String url;
		public GoodsMsgThread(String url){
			this.url = url;
		}

		@Override
		public void run() {
			List<HashMap<String, Object>> lists = null;
			List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
//	        GoodsMsgActivity.this.btnfind.setOnClickListener(new FindGoodsOnClickListenr());
//		    GoodsMsgActivity.this.btnadd.setOnClickListener(new AddGoodsListenerImpl());
			Message msg = new Message();
			msg.what = 0x11;
//			Bundle bundle = new Bundle();
//			bundle.clear();

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
//	           	 System.out.println(user.get("_id"));
					data.add(item);
				}

//	            bundle.putParcelableArrayList("msg", (ArrayList<? extends Parcelable>) data);
//	            msg.setData(bundle);
				msg.obj = data;
				myHandler.sendMessage(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}

//	        GoodsMsgActivity.this.simpleAdapter = new SimpleAdapter(GoodsMsgActivity.this, data,
//					R.layout.data_list, new String[] { "_id", "_name", "_unit","_stoid", "_num", "_price" } // Map中的key的名称
//					, new int[] { R.id._id, R.id._name, R.id._unit, R.id._stoid, R.id._num, R.id._price }); // 是data_list.xml中定义的组件的资源ID
//	        GoodsMsgActivity.this.listview.setAdapter( GoodsMsgActivity.this.simpleAdapter);
		}
	}

	/**
	 * 删除物品线程类
	 */
	class DeleteThread extends Thread {
		public String url;
		public DeleteThread(String url){
			this.url = url;
		}

		@Override
		public void run() {
			String result = null;
			String flag = "";
			Message msg = new Message();
			msg.what = 0x13;
			try {
				result = ConnectUtil.readParse(url);
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
//			System.out.println(result);

		}
	}

	/**
	 * 转向查找结果界面
	 */
	private class FindGoodsOnClickListenr implements OnClickListener {
		@Override
		public void onClick(View view) {
			String key = GoodsMsgActivity.this.txtfind.getText().toString();
			if(TextUtils.isEmpty(key)){
				Toast.makeText(GoodsMsgActivity.this, "请输入关键字", Toast.LENGTH_LONG).show();
			}else{
				Intent intent = new Intent(GoodsMsgActivity.this, FindResultActivity.class);
				intent.putExtra("key", key);
				startActivity(intent);
			}
		}
	}

	/**
	 * 转向添加物品界面
	 */
	private class AddGoodsListenerImpl implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(GoodsMsgActivity.this, AddActivity.class);
			startActivity(intent);

		}
	}

	/**
	 * 删除物品事件
	 */
	private class DeleteGoodsOnItemClickImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
//				url="http://10.0.84.18:8080/Storage/goods!telGoodsDelete";
//				DeleteThread d_thread = new DeleteThread(url);
//				d_thread.start();

			Map<String , String> map=(Map<String, String>)GoodsMsgActivity.this								//取得列表项
					.simpleAdapter.getItem(position);
			final String gid = map.get("_id");
			url="http://10.0.84.18:8080/Storage/goods!telGoodsDelete?g_id="+gid;
			Dialog dialog = new AlertDialog.Builder(GoodsMsgActivity.this)
					.setTitle("删除物品信息")
					.setMessage("您确定删除该条信息吗？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							DeleteThread d_thread = new DeleteThread(url);
							d_thread.start();

						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {


						}
					})
					.show();
		}
	}

	/**
	 * 转向更新界面
	 */
	private class UpdateOnItemClickImpl implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
			Map<String , String> map=(Map<String, String>)GoodsMsgActivity.this								//取得列表项
					.simpleAdapter.getItem(position);
			String gid = map.get("_id");
			Intent intent = new Intent(GoodsMsgActivity.this, UpdateGoodsActivity.class);
			intent.putExtra("id", gid);
			startActivity(intent);
			return true;
		}

	}
}
