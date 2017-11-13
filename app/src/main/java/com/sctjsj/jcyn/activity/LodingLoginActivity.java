package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.UrltitleBean;
import com.sctjsj.jcyn.getui.DemoPushService;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.cookie.DbCookieStore;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/2/23.
 */

public class LodingLoginActivity extends BaseAppcompatActivity{
    @Bind(R.id.activity_loding_username_et)EditText usernameEt;
    @Bind(R.id.activity_loding_password_et)EditText passwordEt;




    private  List<UrltitleBean> list=new ArrayList<>();
    private String cookie;
    private MsApp app;


    @Override
    public void setView() {
        setContentView(R.layout.activity_loding);
        ButterKnife.bind(this);
        app= (MsApp) getApplication();
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));

        if(!app.getAccount().equals("none")){
            usernameEt.setText(app.getAccount());
            if(!app.getPassword().equals("none")){
            passwordEt.setText(app.getPassword());
            loGin();
            }
        }
    }

    /**
     * 点击事件
     * @param view
     */
    @OnClick({R.id.activity_loding_login_bt,
            R.id.activity_loding_register_bt,
            R.id.activity_loding_look,
            R.id.activity_loding_forget_tv})
    public void LoginClick(View view){
        int id=view.getId();
        Intent intent = null;
        switch (id){
            //登录
            case R.id.activity_loding_login_bt:
                loGin();
                break;
            //注册
            case R.id.activity_loding_register_bt:
                intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            //先看一看
            case R.id.activity_loding_look:
                intent=new Intent(this,IndexActivity.class);
                startActivity(intent);
                finish();
                break;
            //忘记密码
            case R.id.activity_loding_forget_tv:
                intent = new Intent(this,ForgetPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void reloadData() {
        startActivity(new Intent(LodingLoginActivity.this,LodingLoginActivity.class));
        finish();
    }
    //登录方法
    public void loGin(){
        final String username = usernameEt.getText().toString();
        final String password = passwordEt.getText().toString();
        Log.e("login",username+"    "+password);
        list.add(new UrltitleBean("userName",username));
        list.add(new UrltitleBean("password",password));
        Log.e("login",list.toString());
        if(username!=null && password!=null){
            LinUtil.newInstance().getMsg(BnUrl.loginUrl , list , this ,false ,new LinUtil.OnNetRequestListner() {
                        @Override
                        public void onSuccess(JSONObject objects) {
                            Log.e("lgoin11111",objects.toString());
                            if(objects!=null){
                                try {
                                    Boolean result=objects.getBoolean("result");
                                    String resultMsg=objects.getString("resultMsg");
                                    String userId=objects.getString("resultData");
//                                        SharedPreferences.Editor editor1=app.getSPF().edit();
//                                        editor1.putString("userId",userId);
                                    app.saveUserId(userId);
                                    Log.e("msg",userId);
                                    if(result&&"登录成功".equals(resultMsg)){
                                        Toast.makeText(LodingLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        dismissLoading();
                                        //登录之后获取用户ID
                                        //获取session
                                        DbCookieStore cookieInstance=DbCookieStore.INSTANCE;
                                        List<HttpCookie> cookies = cookieInstance.getCookies();
                                        for(HttpCookie c:cookies){
                                            String name=c.getName();
                                            String val=c.getValue();
                                            if("JSESSIONID".equals(name)){
                                                cookie=name+"="+val;
                                                SharedPreferences.Editor editor=app.getSPF().edit();
                                                editor.putString("cookie",cookie);
                                                Log.e("cuma",cookie);
                                                editor.commit();
                                                break;
                                            }
                                        }
                                        
                                    Intent i=new Intent(LodingLoginActivity.this,IndexActivity.class);
                                    startActivity(i);
                                    finish();
                                        app.saveAccount(username,password);
                                    }else{
                                        Toast.makeText(LodingLoginActivity.this,"登录失败:"+resultMsg,Toast.LENGTH_LONG).show();
                                        dismissLoading();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(LodingLoginActivity.this,"数据加载失败",Toast.LENGTH_LONG).show();
                            }

                        }
                    }
            );
        }
       list.clear();
    }
}
