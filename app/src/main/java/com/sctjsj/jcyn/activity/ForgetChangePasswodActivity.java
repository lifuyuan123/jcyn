package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.BnUrl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/3/13.
 */

public class ForgetChangePasswodActivity extends BaseAppcompatActivity {

    private MsApp app;

    @Bind(R.id.activity_reset_pwd_tv_remind)
    TextView mTVRemind;

    @Bind(R.id.activity_reset_pwd_et_new_pwd)
    EditText mETNewPwd;

    @Bind(R.id.activity_reset_pwd_et_confirm_new_pwd)
    EditText mETConfirmNewPwd;
    private String mobile="";
    private String code="";
    private String cookie="";

    @Bind(R.id.base_title_tv)
    TextView title;

    @Bind(R.id.base_second_title_tv)
    TextView secondTitle;
    @Override
    public void setView() {
        setContentView(R.layout.activity_forget_changpassword);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
        title.setText("设置新密码");
        secondTitle.setVisibility(View.GONE);
        app = (MsApp) getApplication();
        mobile=getIntent().getStringExtra("mobile");
        mTVRemind.setText("提示：您正在通过"+mobile+"找回密码");
        code=getIntent().getStringExtra("code");
        cookie=getIntent().getStringExtra("cookie");
    }

    @Override
    public void reloadData() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.base_back_rl,R.id.reset_pwd_activity_btn_submit})
    public void ResetPwdClick(View view){
        switch (view.getId()){
            //返回
            case R.id.base_back_rl:
                Intent intent=new Intent(ForgetChangePasswodActivity.this,LodingLoginActivity.class);
                startActivity(intent);
                finish();
                break;
            //提交
            case R.id.reset_pwd_activity_btn_submit:

                if(checkInput()){
                    submit();
                }

                break;
        }
    }

    public boolean checkInput(){
        if(TextUtils.isEmpty(mETNewPwd.getText().toString())){
            Toast.makeText(ForgetChangePasswodActivity.this, "新密码为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(mETConfirmNewPwd.getText().toString())){
            Toast.makeText(ForgetChangePasswodActivity.this, "请确认新密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!mETNewPwd.getText().toString().equals(mETConfirmNewPwd.getText().toString())){
            Toast.makeText(ForgetChangePasswodActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    public void submit(){
        RequestParams params=new RequestParams(BnUrl.resetPwdSUrl);
        params.setUseCookie(false);
        params.addHeader("Cookie",cookie);
        Log.e(TAG,"找回密码使用的cookie"+cookie);
        params.addBodyParameter("mobile",mobile);
        params.addBodyParameter("code",code);
        params.addBodyParameter("password",mETConfirmNewPwd.getText().toString());
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject results) {
                try {
                    boolean  result=results.getBoolean("result");
                    String msg=results.getString("msg");

                    //重置成功
                    if(result && "设置成功".equals(msg)){
                        Toast.makeText(ForgetChangePasswodActivity.this,"密码重置成功，请重新登录", Toast.LENGTH_LONG).show();
                        //清除所有参数
                        //app.getSPF().edit().clear().commit();
                        Intent intent= new Intent(ForgetChangePasswodActivity.this,LodingLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(ForgetChangePasswodActivity.this,msg, Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(ForgetChangePasswodActivity.this,ForgetPasswordActivity.class);
                        startActivity(intent);
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                Toast.makeText(ForgetChangePasswodActivity.this, "密码找回失败", Toast.LENGTH_SHORT).show();
                app.getSPF().edit().clear().commit();
                Intent in=new Intent(ForgetChangePasswodActivity.this,ForgetPasswordActivity.class);
                startActivity(in);
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
