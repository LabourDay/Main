package com.example.main.storage_manage;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class FindStoActivity extends Activity {
	private ListView f_list = null;
	private SimpleAdapter simpleAdapter;
	private String url;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sto_findresult);
		this.f_list = (ListView)findViewById(R.id.sto_f_datalist);
		
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Intent intent = getIntent();
		String key = intent.getStringExtra("key");
		url = "http://10.0.84.18:8080/Storage2.0/TelStoAction!telStoSelectById";
		resultListJson(key); 
	}
	
	 /**
     * ListView ��������
     */
    private void resultListJson(String key){ 
        List<HashMap<String, Object>> lists = null;
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        String urlpath=url+"?id="+key;
        try {
            lists = StoConnectUtil.Analysis(StoConnectUtil.readParse(urlpath));//������json����
            for(HashMap<String, Object> sto : lists){
           	 HashMap<String, Object> item = new HashMap<String, Object>();
           	 item.put("sto_id", sto.get("sto_id"));
           	 item.put("sto_name", sto.get("sto_name"));
           	 item.put("sto_type", sto.get("sto_type"));
           	 item.put("sto_money", sto.get("sto_money"));
           	 item.put("sto_addr", sto.get("sto_addr"));
//           	 System.out.println(user.get("_id"));
           	 data.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.simpleAdapter = new SimpleAdapter(this, data,
				R.layout.sto_date_list, new String[] { "sto_id", "sto_name", "sto_type","sto_money", "sto_addr"} // Map�е�key������
				, new int[] { R.id._stoid, R.id._stoname, R.id._stotype, R.id._stomoney, R.id._stoaddr}); // ��data_list.xml�ж�����������ԴID
		 this.f_list.setAdapter(this.simpleAdapter);

    }
}
