package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.MyCountTimer;
import com.sctjsj.jcyn.util.RegexpValidateUtil;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/3/6.
 */

public class RegisterActivity extends BaseAppcompatActivity {
    @Bind(R.id.base_title_tv)TextView titleTv;
    @Bind(R.id.base_second_title_tv)TextView secondTitleTv;


    @Bind(R.id.activity_regist_telephone_et)EditText username_et;
    @Bind(R.id.activity_regist_telemessge_et)EditText phoneVerification_et;
    @Bind(R.id.activity_regist_img_et)EditText numVerification_et;
    @Bind(R.id.activity_regist_img_iv)ImageView tempIV;
    @Bind(R.id.activity_regist_password_et)EditText password_et;
    @Bind(R.id.activity_regist_getverification_bt) Button getVerification_bt;

    private int orange= Color.parseColor("#ffffff");
    private int gray=Color.parseColor("#ffffff");

    private String cookie="";
    @Override
    public void setView() {
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        titleTv.setText("注册");
        secondTitleTv.setText("登录");
        getVerifyCode();
//        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
    }

    @Override
    public void reloadData() {
        startActivity(new Intent(RegisterActivity.this,RegisterActivity.class));
        finish();
    }

    @OnClick({R.id.base_back_rl,
            R.id.base_second_title_rl,
            R.id.activity_regist_getverification_bt,
            R.id.activity_regist_bt,
            R.id.activity_regist_img_iv
           })
    public void registerClick(View view){
        switch (view.getId()){
            case R.id.base_back_rl:
                finish();
                break;
            case R.id.base_second_title_rl:
                Intent intent = new Intent(this,LodingLoginActivity.class);
                startActivity(intent);
                break;
            //点击切换图片验证码
            case R.id.activity_regist_img_iv:
                getVerifyCode();
                break;
            //点击获取短信验证码
            case R.id.activity_regist_getverification_bt:
                if(checkBeforeGetSMS()){
                    MyCountTimer timer=new MyCountTimer(60000,1000,getVerification_bt,R.string.get_verify_code_txt,orange,gray);
                    verifyPhonenum(timer);
                }
                break;
            //注册
            case R.id.activity_regist_bt:
                if(checkInput()){
                    gotoRegister();
                }
                break;
        }

    }


    /**
     * 加载验证码图片
     */
    private void getVerifyCode(){

        OkHttpClient client=new OkHttpClient();
        client = new OkHttpClient();
        final CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        final OkHttpDownloader downloader = new OkHttpDownloader(client);
        Picasso picasso=new Picasso.Builder(RegisterActivity.this).downloader(downloader).build();

        picasso.load(BnUrl.getVerifyCode).skipMemoryCache().resize(100,80).into(tempIV, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Log.e(TAG,"验证码加载完成....");
                List<HttpCookie> list = new ArrayList<HttpCookie>();
                //list= cookieManager.getCookieStore().get(URI.create(MainUrl.getVerifyCodeUrl));
                list=cookieManager.getCookieStore().getCookies();
                //获取此次请求的cookie
                for(int i=0;i<list.size();i++){
                    Log.e(TAG,"cookie---:"+list.get(i).toString()+"第"+i+"个");
                }
                if(list!=null&&list.size()>0){
                    cookie=list.get(0).toString();
                }

            }

            @Override
            public void onError() {
                Log.e(TAG,"验证码加载失败");
                Toast.makeText(RegisterActivity.this, "数字验证码加载失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取短信
     */
    private void getSMSCode(){
        RequestParams params=new RequestParams(BnUrl.getSMSUrl);
        params.setUseCookie(false);
        params.addBodyParameter("mobile",username_et==null?"":username_et.getText().toString());
        params.addBodyParameter("code",numVerification_et==null?"":numVerification_et.getText().toString());
        params.addHeader("Cookie",cookie);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                LinUtil.newInstance().showLoading(true,"数据加载中...",RegisterActivity.this);
                Toast.makeText(RegisterActivity.this, "短信发送成功", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"短信："+result.toString());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(RegisterActivity.this, "短信验证码发送失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"cuowu"+ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
             LinUtil.newInstance().dismissLoading();
            }
        });
    }

    /**
     *注册之前验证手机号唯一性
     */
    private void verifyPhonenum(final MyCountTimer timer){
        RequestParams params=new RequestParams(BnUrl.verifyPhonenumUrl);
        params.setUseCookie(false);
        params.addBodyParameter("Cookie",cookie);
        params.addBodyParameter("name","phone");
        params.addBodyParameter("param",username_et.getText().toString());
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                timer.start();
                //Log.e(TAG,"验证手机号："+result.toString());
                try {
                    String status=result.getString("status");
                    String info=result.getString("info");
                    //验证通过
                    if("y".equalsIgnoreCase(status)&&"验证通过".equals(info)){
                        getSMSCode();
                    }else{
                        Toast.makeText(RegisterActivity.this, "该手机号验证失败，请更换手机号", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG,"");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 注册
     */
    private void gotoRegister(){
        RequestParams params=new RequestParams(BnUrl.registerUrl);
        params.setUseCookie(false);
        params.addBodyParameter("phone",username_et==null?"":username_et.getText().toString());
        params.addBodyParameter("password",password_et==null?"":password_et.getText().toString());
        //图片验证码
        params.addBodyParameter("piccode",numVerification_et==null?"":numVerification_et.getText().toString());
        //短信验证码
        params.addBodyParameter("code",phoneVerification_et==null?"":phoneVerification_et.getText().toString());
        params.addHeader("Cookie",cookie);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject results) {
                LinUtil.newInstance().showLoading(true,"数据加载中...",RegisterActivity.this);

                Log.e("register",results.toString());
                if(results!=null){
                    try {
                        boolean result=results.getBoolean("result");
                        //注册成功
                        if(result){
                            JSONObject user=results.getJSONObject("user");
                            String username=user.getString("username");
                            Toast.makeText(RegisterActivity.this, "注册成功，请重新登录", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(RegisterActivity.this,LodingLoginActivity.class);
                            i.putExtra("username",username);
                            startActivity(i);
                            finish();
                        }else{
                            String msg=results.getString("msg");
                            Toast.makeText(RegisterActivity.this, "注册失败："+msg, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"数据加载失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG,"注册错误："+ex.toString());
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
              LinUtil.newInstance().dismissLoading();
            }
        });
    }

    /**
     * 检查输入的是否为空或格式是否有效
     * @return
     */
    public boolean checkInput(){

        if(!RegexpValidateUtil.checkMobileNumber(username_et.getText().toString())){
            Toast.makeText(RegisterActivity.this, "请输入有效的手机号码", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password_et.getText().toString().length()<8){
            Toast.makeText(RegisterActivity.this, "密码要求长度大于8位", Toast.LENGTH_SHORT).show();
            return false;
        }
        //图片验证码
        if(numVerification_et.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "图片验证码为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        //短信验证码
        if (phoneVerification_et.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "短信验证码为空", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }


    public boolean checkBeforeGetSMS(){
        if(!RegexpValidateUtil.checkMobileNumber(username_et.getText().toString())){
            Toast.makeText(RegisterActivity.this, "请输入有效的手机号码", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password_et.getText().toString().length()<8){
            Toast.makeText(RegisterActivity.this, "密码要求长度大于8位", Toast.LENGTH_SHORT).show();
            return false;
        }
        //图片验证码
        if(numVerification_et.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "图片验证码为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
