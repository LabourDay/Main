package com.example.main.yuangong;


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

import com.example.main.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public  class UpdateyuangongActivity extends Activity {

    ////yuangongMsgActivity�е�������		<!--����޸����ݹ��ܣ������ʼ��-->
		//this.listview.setOnItemClickListener(new UpdateyuangongOnItemClickListener());
    //�ǵñ��ʱ�� ��һ���е�UpdateyuangongActivityѡ�� ALT+ENTER��  ADD������
    // Ŀ������������androidManifest.xml�е����this.listview.setOnItemClickListener(new UpdateyuangongOnItemClickListener());��<activity>����
    private EditText ud_id;
    private EditText ud_username;
    private EditText ud_password;
    private EditText ud_truename;
    private EditText ud_tel;
    private EditText ud_mail;
    private EditText ud_num;
    private Button ud_submit;
    private String url;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //��activity_yuangong_update��ȡǰ̨�û�Ҫ�޸ĵ�����
        setContentView(R.layout.activity_yuangong_update);
        //��ǰ̨���ݻ�ȡ����ҳ����  �����ǰ̨��g_update_id������̨ud_id��
        this.ud_id = (EditText)findViewById(R.id.g_update_id);
        this.ud_username = (EditText)findViewById(R.id.g_update_username);
        this.ud_password = (EditText)findViewById(R.id.g_update_password);
        this.ud_truename = (EditText)findViewById(R.id.g_update_truename);
        this.ud_tel = (EditText)findViewById(R.id.g_update_tel);
        this.ud_mail = (EditText)findViewById(R.id.g_update_mail);
        this.ud_num = (EditText)findViewById(R.id.g_update_num);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        this.ud_id.setText(id);
        this.ud_submit = (Button)findViewById(R.id.update);
///
        this.ud_submit.setOnClickListener(new SubmitOnClickLIstenerImpl());
        //�����ݴ���WEB�˽������ݿ��޸Ĳ�����yuangong action ��telyuangongUpdate�����  �˹����ٹ�������ҳ��� �ɹ����������������ݿ���

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

}

    private class SubmitOnClickLIstenerImpl implements OnClickListener {
        @Override
//        �û������ʱ  ����ǰ������еĸ������ݱ������� ���浽��ҳ��
        public void onClick(View arg0) {
            String w_id = ud_id.getText().toString();
            String w_username = ud_username.getText().toString();
            String w_password = ud_password.getText().toString();
            String w_truename = ud_truename.getText().toString();
            String w_tel = ud_tel.getText().toString();
            String w_mail = ud_mail.getText().toString();
            String l_num = ud_num.getText().toString();
            Log.v("","");
            //��鱣���������ֶ��Ƿ�������һ���ǿյ�  ��һ���յ�ʱ��  ������ʾ �����޸�ҳ�棨��ҳ�棩
            if(TextUtils.isEmpty(w_id) || TextUtils.isEmpty(w_username) || TextUtils.isEmpty(w_password)
                    || TextUtils.isEmpty(w_truename) || TextUtils.isEmpty(w_tel) || TextUtils.isEmpty(w_mail)|| TextUtils.isEmpty(l_num)){
                String str = "请输入完整信息！！！";
                Toast.makeText(UpdateyuangongActivity.this, str, Toast.LENGTH_LONG);
            }
            updateyuangong(w_id,w_username,w_password,w_truename,w_tel,w_mail,l_num);

        }
    }

    private void updateyuangong(String w_id, String w_username, String w_password, String w_truename, String w_tel, String w_mail, String l_num){
        String result = null;
        try{
            HttpClient httpClient = new DefaultHttpClient();
//������ɫ��keyֵ���ڴ���web�����ݿ��������У�yuangong action ��telyuangongUpdate�����
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("w_id", w_id));
            params.add(new BasicNameValuePair("w_username", w_username));
            params.add(new BasicNameValuePair("w_password", w_password));
            params.add(new BasicNameValuePair("w_truename", w_truename));
            params.add(new BasicNameValuePair("w_tel", w_tel));
            params.add(new BasicNameValuePair("w_mail", w_mail));
            params.add(new BasicNameValuePair("l_num", l_num));
            url="http://10.0.84.18:8080/Storage2.0/yuangong!telyuangongUpdate";

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
                Toast.makeText(UpdateyuangongActivity.this, "员工信息修改失败", Toast.LENGTH_LONG).show();
            }

            if(result.equals("1")){
                Toast.makeText(UpdateyuangongActivity.this, "员工信息修改成功", Toast.LENGTH_LONG).show();
                //��UpdateyuangongActivityҳ����ת��yuangongMsgActivityҳ��
                Intent intent = new Intent(UpdateyuangongActivity.this,yuangongMsgActivity.class);
                startActivity(intent);
                UpdateyuangongActivity.this.finish();
            }
            if(result.equals("0")){
                Toast.makeText(UpdateyuangongActivity.this, "员工信息修改失败", Toast.LENGTH_LONG).show();
            }
        } catch(ClientProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

}
