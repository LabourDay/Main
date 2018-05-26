package com.example.main.yuangong;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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
import org.json.JSONObject;

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
import java.util.Map;

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class yuangongMsgActivity extends Activity {
    private String url=null;
    private EditText txtfind = null;
    private Button btnfind = null;
    private Button btnadd = null;
	private ListView listview=null;
	private SimpleAdapter simpleAdapter = null; // �������ݵ�ת������
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//����ǰ̨activity_yuangong_msgҳ��

		setContentView(R.layout.activity_yuangong_msg);
		this.btnadd = (Button)findViewById(R.id.add);
		listview = (ListView) super.findViewById(R.id.datalist);
		//����ǰ̨yuangong action�е�telyuangongMessage����
		url="http://10.0.84.18:8080/Storage2.0/yuangong!telyuangongMessage";
		//���ü�������ʵʱ����
		this.txtfind = (EditText)findViewById(R.id.key);
		this.btnfind = (Button)findViewById(R.id.find);


//		<!--�����ֶβ�ѯ������ܣ������ʼ��-->
		this.btnfind.setOnClickListener(new FindyuangongOnClickListenr());
//		<!--������빦�ܣ������ʼ��-->
		this.btnadd.setOnClickListener(new AddyuangongListenerImpl());

//		<!--����޸����ݹ��ܣ������ʼ��-->
		this.listview.setOnItemClickListener(new UpdateyuangongOnItemClickListener());

//		<!--�������ɾ����ǰ�ж�Ӧ�����ݿ��е����ݵĹ��ܣ������ʼ��-->
		this.listview.setOnItemLongClickListener(new DeleteyuangongItemLongClicListener());
		
		//ǿ����4.0�汾 ��ȥ������̷߳������硣
	    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);

		//�����ݿ������ݲ�ѯ�����ŵ���׿����
	    resultListJson();
	}
	
	public static String readParse(String urlPath) throws Exception {
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
	
  
	
	/**
     * ����
     * 
     * @throws JSONException
     */
    private static ArrayList<HashMap<String, Object>> Analysis(String jsonStr)
            throws JSONException {
        /******************* ���� ***********************/
        //��Ŀ��  Ϊ��ȡ�����ݿ⴫�����������õ�ǰ̨����ʾ
        // ��ʼ��list�������
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
      
        /* //androidĬ�Ͻ���������
        JSONArray jsonArray = null;
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // ��ʼ��map�������
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("_id", jsonObject.getString("_id"));
            map.put("_name", jsonObject.getString("_name"));
            list.add(map);
        }*/
        
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
     * ListView ��������  �������ʼ��map�������󣬴�json������ȡ�������ݽ���ListView��������  Ϊ�������õ�ǰ̨ʹ��
     */
    private void resultListJson(){
		//��Ŀ��  Ϊ��ȡ�����ݿ⴫�����������õ�ǰ̨����ʾ
        List<HashMap<String, Object>> lists = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        try {
            lists = Analysis(readParse(url));//������json����
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
//		Ϊ�������õ�ǰ̨ʹ��     �������������json���� ���͵�  data_list.xml��ǰ̨��������ʾ
        this.simpleAdapter = new SimpleAdapter(this, data,
				R.layout.w_data_list, new String[] { "_id", "_username", "_password","_truename", "_tel", "_mail" , "_num"} // Map�е�key������
				, new int[] { R.id._id, R.id._username, R.id._password, R.id._truename, R.id._tel, R.id._mail, R.id._num }); // ��data_list.xml�ж�����������ԴID
		 this.listview.setAdapter(this.simpleAdapter);

    }
	private class FindyuangongOnClickListenr implements OnClickListener {
		@Override
		public void onClick(View view) {
			String key = yuangongMsgActivity.this.txtfind.getText().toString();
			if(TextUtils.isEmpty(key)){
				Toast.makeText(yuangongMsgActivity.this, "请输入关键字", Toast.LENGTH_LONG).show();
			}else{
//				activity��intent��ת����ֵkey������ת��FindResultActitivity.classҳ��
				Intent intent = new Intent(yuangongMsgActivity.this, FindResultActitivity.class);
				intent.putExtra("key", key);
				startActivity(intent);
			}
		}
	}



	private class AddyuangongListenerImpl implements OnClickListener{
		//	�ֱ�����GoodsMsgActivity(Ϊ�˲�ѯ�����ݿ�����ݴ�����׿����ʾ����)��Intent����  ��yuangongMsgActivity.thisҳ������AddyuangongActivity.class)ҳ��
		//����ע��CTIL�����AddyuangongActivity.class����class�����class����Alt+enter+��ʾ���е��Add activity to manifest
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(yuangongMsgActivity.this, AddyuangongActivity.class);
			startActivity(intent);

		}
	}

	private class DeleteyuangongItemLongClicListener implements OnItemLongClickListener {
		//		ɾ�����ݹ���
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
									   int position, long id) {
			Map<String , String> map=(Map<String, String>)yuangongMsgActivity.this								//ȡ���б���
					.simpleAdapter.getItem(position);
//			���û�����ѡ�е�id���浽gid�ַ�����
			final String gid = map.get("_id");
			Dialog dialog = new AlertDialog.Builder(yuangongMsgActivity.this)
					.setTitle("删除员工信息")
					.setMessage("您确定删除该条信息吗")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
//                            ʹ�÷���deleteyuangong()��Ϊ�˴�gid  ���keyֵ�����ݿ��ѯ�����
							deleteyuangong(gid);
							//��yuangongMsgActivity����yuangongMsgActivity    ˢ��
							Intent intent = new Intent(yuangongMsgActivity.this,yuangongMsgActivity.class);
							startActivity(intent);
//                            ˢ�±�ҳ��yuangongMsgActivity
							yuangongMsgActivity.this.finish();

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

	private void deleteyuangong(String id){
//		ɾ�����ݹ���   ���û�������������Ӧ��ID  ����yuangong action��telyuangongDelete������  ���ݴ���ȥ��IDִ�����ݿ�ɾ�����ݹ���
		url="http://10.0.84.18:8080/Storage2.0/yuangong!telyuangongDelete";
		HttpClient httpClient = new DefaultHttpClient();
		String result = null;
		try{
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("w_id", id));
			HttpPost request = new HttpPost(url);

			if(params != null){
				HttpEntity entity = new UrlEncodedFormEntity(params,"utf-8");
				request.setEntity(entity);
			}

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			String json = EntityUtils.toString(entity, "utf-8");

			if(json != null){
				//��װ��json
				JSONObject jsonObject = new JSONObject(json);
				result = jsonObject.get("message").toString();
				Intent intent = new Intent(yuangongMsgActivity.this,yuangongMsgActivity.class);
				startActivity(intent);
				yuangongMsgActivity.this.finish();
			}

			if(json == null){
				//��ҳ�������Ϣ
				tips("员工信息删除失败");
			}

			if(result.equals("1")){
				tips(id+"员工信息删除成功");
				//��yuangongMsgActivity����yuangongMsgActivity   ��ҳ��ˢ��
				Intent intent = new Intent(yuangongMsgActivity.this,yuangongMsgActivity.class);
				startActivity(intent);
				yuangongMsgActivity.this.finish();
			}
			if(result.equals("0")){
				tips(id+"员工信息删除失败");
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
//		��ҳ�������Ϣ
		Toast.makeText(yuangongMsgActivity.this, str, Toast.LENGTH_LONG).show();
	}

	private class UpdateyuangongOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			Map<String , String> map=(Map<String, String>)yuangongMsgActivity.this								//ȡ���б���
					.simpleAdapter.getItem(position);
//����굥��ʱ��Ӧ�������е�ID���浽gid��
			String gid = map.get("_id");
//			��ҳ��yuangongMsgActivity��ת��UpdateGoodsActivityҳ��   ���Ҵ�keyֵ id
			Intent intent = new Intent(yuangongMsgActivity.this, UpdateyuangongActivity.class);
			intent.putExtra("id", gid);
			startActivity(intent);

		}

	}
}
