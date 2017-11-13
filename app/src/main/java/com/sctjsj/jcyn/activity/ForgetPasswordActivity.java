package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.MyCountTimer;
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
 * Created by Aaron on 2017/3/13.
 */

public class ForgetPasswordActivity extends BaseAppcompatActivity {

    private MsApp app;
    private String cookie;
    //颜色
    private int orange= Color.parseColor("#f08200");
    private int gray=Color.parseColor("#b4b4b4");
    //图片验证码容器
    @Bind(R.id.activity_get_pwd_back_rl_verify_code_parent)
    RelativeLayout mRLVerifyCodeParent;
    //提示
    @Bind(R.id.activity_get_pwd_back_tv_remind)
    TextView mTVRemind;
    //手机号
    @Bind(R.id.activity_get_pwd_back_et_phone)
    EditText mETphonenum;
    //图片验证码
    @Bind(R.id.activity_get_pwd_back_et_code)
    EditText mETVerifyCode;

    //短信验证码
    @Bind(R.id.activity_get_pwd_back_et_sms_code)
    EditText mETSmsCode;

    @Bind(R.id.activity_get_pwd_back_btn_get_sms)
    Button mBtnSendSms;

    @Bind(R.id.base_title_tv)
    TextView title;

    @Bind(R.id.base_second_title_tv)
    TextView secondTitle;


    @Override
    public void setView() {
        setContentView(R.layout.activity_forgetpassword_verify);
        ButterKnife.bind(this);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
        title.setText("忘记密码");
        secondTitle.setVisibility(View.GONE);
        app = (MsApp) getApplication();
        getVerifyCode();
    }

    @Override
    public void reloadData() {
        startActivity(new Intent(ForgetPasswordActivity.this,ForgetPasswordActivity.class));
        finish();

    }

    @OnClick({R.id.base_back_rl,R.id.activity_get_pwd_back_tv_change_code,
            R.id.activity_get_pwd_back_btn_get_sms,R.id.activity_get_pwd_back_btn_next})
    public void getPwdbackClick(View view){
        switch (view.getId()){
            //返回
            case R.id.base_back_rl:
                Intent intent=new Intent(ForgetPasswordActivity.this,LodingLoginActivity.class);
                startActivity(intent);
                finish();
                break;
            //更换图片验证码
            case R.id.activity_get_pwd_back_tv_change_code:
                getVerifyCode();
                break;
            //发送短信
            case R.id.activity_get_pwd_back_btn_get_sms:
                if(check()){
                    MyCountTimer timer=new MyCountTimer(60000,1000,mBtnSendSms,R.string.get_verify_code_txt,orange,gray);
                    sendSms(timer);
                }
                break;
            //下一步
            case R.id.activity_get_pwd_back_btn_next:
                if(checkInput()){
                    Toast.makeText(ForgetPasswordActivity.this, "下一步", Toast.LENGTH_SHORT).show();
                    goToNext();
                }
                break;

        }
    }

    /**
     * 验证短信Code，跳转到下一步
     */
    private void goToNext() {
        Intent intent=new Intent(ForgetPasswordActivity.this,ForgetChangePasswodActivity.class);
        intent.putExtra("mobile",mETphonenum.getText().toString());
        intent.putExtra("code",mETSmsCode.getText().toString());
        intent.putExtra("cookie",cookie);
        Log.e(TAG,"传递的cookie:"+cookie);
        startActivity(intent);
        finish();
    }

    /**
     * 加载验证码图片
     */
    private void getVerifyCode(){
        mRLVerifyCodeParent.removeAllViews();
        ImageView tempIV=new ImageView(ForgetPasswordActivity.this);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        tempIV.setLayoutParams(params);
        tempIV.setScaleType(ImageView.ScaleType.FIT_XY);
        mRLVerifyCodeParent.addView(tempIV);
        OkHttpClient client=new OkHttpClient();
        client = new OkHttpClient();
        final CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        final OkHttpDownloader downloader = new OkHttpDownloader(client);
        Picasso picasso=new Picasso.Builder(ForgetPasswordActivity.this).downloader(downloader).build();

        picasso.load(BnUrl.getVerifyCode).skipMemoryCache().resize(100,80).into(tempIV, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                List<HttpCookie> list = new ArrayList<HttpCookie>();
                list=cookieManager.getCookieStore().getCookies();

            /*    //获取此次请求的cookie
                for(int i=0;i<list.size();i++){
                    //Log.e(TAG,"验证码session："+list.get(i).toString());
                }*/
                if(list!=null&&list.size()>0){
                    cookie=list.get(0).toString();
                    Log.e(TAG,"图片验证码session："+cookie);
                }

            }

            @Override
            public void onError() {
                Toast.makeText(ForgetPasswordActivity.this, "数字验证码加载失败", Toast.LENGTH_LONG).show();
                mTVRemind.setText("提示：数字验证码加载失败，请重试");
            }
        });
    }

    /**
     * 发送短信之前验证
     * @return
     */
    public boolean check(){
        if(TextUtils.isEmpty(mETphonenum.getText().toString())){
            mTVRemind.setText("手机号为空");
            Toast.makeText(ForgetPasswordActivity.this, "手机号为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(mETVerifyCode.getText().toString())){
            mTVRemind.setText("图片验证码为空");
            Toast.makeText(ForgetPasswordActivity.this, "图片验证码为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 发送短信
     */
    public void sendSms(final MyCountTimer timer){
        RequestParams params=new RequestParams(BnUrl.getSMSUrl);
        params.setUseCookie(false);
        params.addHeader("Cookie",cookie);
        params.addBodyParameter("mobile",mETphonenum.getText().toString());
        //图片验证码
        params.addBodyParameter("code",mETVerifyCode.getText().toString());

        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject results) {
                Log.e(TAG,"短信发送结果："+results.toString());
                timer.start();
                try {
                    boolean result=results.getBoolean("result");
                    if(result){
                        String resultMsg=results.getString("resultMsg");
                        if(TextUtils.isEmpty(resultMsg)){
                            Toast.makeText(ForgetPasswordActivity.this,resultMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

             /*   //获取session
                DbCookieStore cookieInstance=DbCookieStore.INSTANCE;
                List<HttpCookie> cookies = cookieInstance.getCookies();
                for(HttpCookie c:cookies) {
                    String name = c.getName();
                    String val = c.getValue();
                    if ("JSESSIONID".equals(name)) {
                        cookie = name + "=" + val;
                        SharedPreferences.Editor editor = app.getSPF().edit();
                        editor.putString("cookie", cookie);
                        Log.e(TAG,"发送短信的cookie:"+cookie);
                        editor.commit();
                    }
                }*/
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

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
     * 检查全部输入
     * @return
     */
    public boolean checkInput(){
        if(TextUtils.isEmpty(mETphonenum.getText().toString())){
            mTVRemind.setText("手机号为空");
            Toast.makeText(ForgetPasswordActivity.this, "手机号为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(mETVerifyCode.getText().toString())){
            mTVRemind.setText("图片验证码为空");
            Toast.makeText(ForgetPasswordActivity.this, "图片验证码为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(mETSmsCode.getText().toString())){
            mTVRemind.setText("短信验证码为空");
            Toast.makeText(ForgetPasswordActivity.this, "短信验证码为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
