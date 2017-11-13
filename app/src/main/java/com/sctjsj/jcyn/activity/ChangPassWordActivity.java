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
import com.sctjsj.jcyn.util.SessionUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/3/6.
 */

public class ChangPassWordActivity extends BaseAppcompatActivity {

    private MsApp app;


    @Bind(R.id.base_title_tv)TextView titleTv;
    @Bind(R.id.base_second_title_tv)TextView secondTitleTv;



    //旧密码
    @Bind(R.id.modify_pwd_activity_et_old_pwd)
    EditText mETOldPwd;
    //新密码
    @Bind(R.id.modify_pwd_activity_et_new_pwd)
    EditText mETNewPwd;
    //确认新密码
    @Bind(R.id.modify_pwd_activity_et_confirm_new_pwd)
    EditText mETConfirmNewPwd;
    //提示
    @Bind(R.id.activity_change_pwd_tv_remind)
    TextView mTVRemind;

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
        app = (MsApp) getApplication();
        titleTv.setText("修改密码");
        secondTitleTv.setVisibility(View.GONE);

    }

    @Override
    public void reloadData() {

    }



    @OnClick({R.id.base_back_rl,R.id.modify_pwd_activity_btn_confirm})
    public void changePwdClick(View view){
        switch (view.getId()){
            //返回
            case R.id.base_back_rl:
                finish();
                break;
            //确认修改密码
            case R.id.modify_pwd_activity_btn_confirm:
                if(checkInput()){
                    saveModify(mETOldPwd.getText().toString(),mETNewPwd.getText().toString(),mETConfirmNewPwd.getText().toString());
                }
                break;
        }
    }

    private boolean checkInput(){

        if(TextUtils.isEmpty(mETOldPwd.getText().toString())){
            Toast.makeText(ChangPassWordActivity.this, "旧密码为空", Toast.LENGTH_SHORT).show();
            mTVRemind.setText("旧密码为空");
            return false;
        }

        if(TextUtils.isEmpty(mETNewPwd.getText().toString())){
            Toast.makeText(ChangPassWordActivity.this, "新密码为空", Toast.LENGTH_SHORT).show();
            mTVRemind.setText("新密码为空");
            return false;
        }

        if(TextUtils.isEmpty(mETConfirmNewPwd.getText().toString())){
            Toast.makeText(ChangPassWordActivity.this, "请确认新密码", Toast.LENGTH_SHORT).show();
            mTVRemind.setText("请确认新密码");
            return false;
        }

        //两次输入的新密码不一致
        if(!mETNewPwd.getText().toString().equals(mETConfirmNewPwd.getText().toString())){
            Toast.makeText(ChangPassWordActivity.this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            mTVRemind.setText("两次输入的新密码不一致");
            return false;
        }


        return true;
    }
    private void saveModify(String oldpwd, String newpwd, String confirmpwd){
        RequestParams params=new RequestParams(BnUrl.modifyPwdUrl);
        params.setUseCookie(false);
        params.setHeader("cookie",SessionUtil.getCookie());
        params.addBodyParameter("olderpassword",mETOldPwd.getText().toString());
        params.addBodyParameter("newpassword",mETNewPwd.getText().toString());
        params.addBodyParameter("user_id",app.getUserId());
        showLoading(true,"请稍后...");
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject results) {
                Log.e("cpw",results.toString());
                Log.e(TAG,"修改密码结果："+results.toString());
                try {
                    Boolean result=results.getBoolean("result");
                    //修改成功
                    if(result){
                        Toast.makeText(ChangPassWordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                        app.getSPF().edit().putString("password",mETNewPwd.getText().toString()).commit();
                        Intent i=new Intent(ChangPassWordActivity.this,LodingLoginActivity.class);
                        app.saveAccount("","");
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(ChangPassWordActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(ChangPassWordActivity.this,SetActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
              dismissLoading();
            }
        });
    }

}
