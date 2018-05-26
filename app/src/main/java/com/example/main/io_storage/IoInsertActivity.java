package com.example.main.io_storage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

/**
 * 作者： 菜鸟先吃
 * 时间：2017/12/7
 * 说明：
 */

public class IoInsertActivity extends Activity {
    private EditText id;
    private EditText name;
    private RadioGroup type;
    private RadioButton add_in;
    private RadioButton add_out;
    private EditText amount;
    private EditText time;
    private Button btn,b;
    private String v_type;
    private String url;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io_update);
        url="http://10.0.84.18:8080/Storage2.0/telIOAction!telIOInsert";
        this.id = (EditText) findViewById(R.id.io_add_id);
        this.name = (EditText) findViewById(R.id.io_add_name);

        this.type = (RadioGroup) findViewById(R.id.io_add_type);//单选框+-
        this.add_in= (RadioButton) findViewById(R.id.in_io);
        this.add_out= (RadioButton) findViewById(R.id.out_io);

        this.amount= (EditText) findViewById(R.id.io_add_amount);
        this.time = (EditText) findViewById(R.id.io_add_time);
        this.btn= (Button) findViewById(R.id.io_add_btn);

        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton io_type = (RadioButton) findViewById(i);
                v_type=io_type.getText().toString();
                Toast.makeText(IoInsertActivity.this, String.valueOf(io_type.getText()), Toast.LENGTH_SHORT).show();
            }
        });

        this.btn.setOnClickListener(new BtnOnClickInsertImpl());

    }

    private class BtnOnClickInsertImpl implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            String g_id = id.getText().toString();
            String g_name = name.getText().toString();
            String v_amount = amount.getText().toString();
            String v_time = time.getText().toString();
     //     String v_type=String.valueOf(id.getText());
            try {
               // ConnetUtil conn=new ConnetUtil();
                addIOMsg(g_id, g_name, v_type, v_amount, v_time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        }

        private void addIOMsg(String g_id, String g_name,String v_type, String v_amount, String v_time) throws JSONException {
            String result = null;
            try{
                HttpClient httpClient = new DefaultHttpClient();

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("g_id", g_id));
                params.add(new BasicNameValuePair("g_name", g_name));
                params.add(new BasicNameValuePair("v_type", v_type));
                params.add(new BasicNameValuePair("v_amount", v_amount));
                params.add(new BasicNameValuePair("v_time", v_time));

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
                    result = jsonObject.get("flag").toString();

                }

                if(json == null){
                    tips("添加失败");
                }

                if(result.equals("操作成功")){
                    tips("操作成功");
                    Intent intent=new Intent(IoInsertActivity.this,IoMsgActivity.class);
                    startActivity(intent);
                }
                if(result.equals("库存不足")){
                    tips("库存不足！");
                }
                if(result.equals("商品不存在")){
                    tips("商品不存在");
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
        Toast.makeText(IoInsertActivity.this, str, Toast.LENGTH_LONG).show();
    }
/*
        private class OnCheckedChangeListenerImp implements RadioGroup.OnCheckedChangeListener {
            String temp=null;
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(IoInsertActivity.this.add_in.getId()==i){
                  temp="入库";
                } else if(IoInsertActivity.this.add_out.getId()==i){
                    temp="出库";
                }
        }
    }*/
}
