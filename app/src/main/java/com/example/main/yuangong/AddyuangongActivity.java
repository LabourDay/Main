package com.example.main.yuangong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class AddyuangongActivity extends Activity {
    private EditText id;
    private EditText username;
    private EditText password;
    private EditText truename;
    private EditText tel;
    private EditText mail;
    private EditText num;

    private Button submit;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuangong_add);
//�����ݿ��ж�����������ʾ�ڰ�׿��ҳ���ϣ��ٽ��û�����Ҫ�޸ĵ����ݣ�����ǰ̨Ҫ���ӵ����ݽ�������   �������������й��ܴ���
        this.id = (EditText)findViewById(R.id.g_add_id);
        this.username = (EditText)findViewById(R.id.g_add_username);
        this.password = (EditText)findViewById(R.id.g_add_password);
        this.truename = (EditText)findViewById(R.id.g_add_truename);
        this.tel = (EditText)findViewById(R.id.g_add_tel);
        this.mail = (EditText)findViewById(R.id.g_add_mail);
        this.num = (EditText)findViewById(R.id.g_add_l_num);

        this.submit = (Button)findViewById(R.id.submit);

//        ��ת��SubmitOnClickLIstenerImpl()����
        this.submit.setOnClickListener(new SubmitOnClickLIstenerImpl());
//����yuangong action��telyuangongInsert�������в������
        url="http://10.0.84.18:8080/Storage2.0/yuangong!telyuangongInsert";
    }

    @SuppressLint("NewApi") private class SubmitOnClickLIstenerImpl implements OnClickListener{
        @SuppressLint("NewApi") @Override
        public void onClick(View arg0) {
//��֤�û��޸ĺ��Ƿ���һ��Ϊ��  ��Ϊ������ʾ
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String w_id = id.getText().toString();
            String w_username = username.getText().toString();
            String w_password = password.getText().toString();
            String w_truename = truename.getText().toString();
            String w_tel = tel.getText().toString();
            String w_mail = mail.getText().toString();
            String l_num = num.getText().toString();
            System.out.println(w_id+":"+w_username+":"+w_password+":"+w_truename+":"+w_tel+":"+w_mail+":"+l_num);
            if(TextUtils.isEmpty(w_id) || TextUtils.isEmpty(w_username) || TextUtils.isEmpty(w_password)
                    || TextUtils.isEmpty(w_truename) || TextUtils.isEmpty(w_tel) || TextUtils.isEmpty(w_mail)|| TextUtils.isEmpty(l_num)){
                String str = "请输入完整信息！！！";
                tips(str);
            }
            addGoods(w_id,w_username,w_password,w_truename,w_tel,w_mail,l_num);

        }
    }

    private void addGoods(String id, String username, String password, String truename, String tel, String mail, String num){
        String result = null;
        try{
            HttpClient httpClient = new DefaultHttpClient();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("w_id", id));�� ��ɫ��w_idҪ�����ݿ���goods.setW_id(request.getParameter("w_id"));  ��w_id  keyֵҪ��Ӧ
            params.add(new BasicNameValuePair("w_id", id));
            params.add(new BasicNameValuePair("w_username", username));
            params.add(new BasicNameValuePair("w_password", password));
            params.add(new BasicNameValuePair("w_truename", truename));
            params.add(new BasicNameValuePair("w_tel", tel));
            params.add(new BasicNameValuePair("w_mail", mail));
            params.add(new BasicNameValuePair("l_num", num));
            HttpPost request = new HttpPost(url);

            if(params != null){
                HttpEntity entity = new UrlEncodedFormEntity(params,"utf-8");
                request.setEntity(entity);
            }

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, "utf-8");

//            Log.e("json",json);

            if(json != null){
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.get("message").toString();

            }

            if(json == null){
                tips("员工信息添加失败");
            }

            if(result.equals("1")){
                tips(username+"员工信息添加成功");
                //������
                Intent intent = new Intent(AddyuangongActivity.this,yuangongMsgActivity.class);
                startActivity(intent);
                AddyuangongActivity.this.finish();
            }
            if(result.equals("0")){
                tips(username+"员工信息添加失败");
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
//        Ϊ��ʱ���������ʾ����
        Toast.makeText(AddyuangongActivity.this, str, Toast.LENGTH_LONG).show();
    }
}
