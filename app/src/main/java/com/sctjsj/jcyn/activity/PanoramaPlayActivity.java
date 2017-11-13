package com.sctjsj.jcyn.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.HorizontalProgressBarWithNumber;


import butterknife.Bind;
import butterknife.ButterKnife;


public class PanoramaPlayActivity extends Activity {
    private WebView web;
    private WebSettings settings;
    private String url;
    private Handler mHandler;
    @Bind(R.id.activity_panorama_play_rl_web_parent)
    RelativeLayout mRLWebParent;
    @Bind(R.id.activity_panorama_play_iv_back)ImageView mIVBack;
    //加载进度
    @Bind(R.id.activity_panorama_play_progress_1)HorizontalProgressBarWithNumber mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        MsApp myApp= (MsApp) getApplication();
        myApp.addActivity(this);
        ButterKnife.bind(this);

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        mProgress.setProgress((Integer) msg.obj);
                        break;
                }
            }
        };
        mIVBack.bringToFront();
        mIVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        url= (String) getIntent().getSerializableExtra("panoramaUrl");
        web=new WebView(PanoramaPlayActivity.this);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        web.setLayoutParams(params);
        mRLWebParent.addView(web);
        settings=web.getSettings();
        settings.setJavaScriptEnabled(true);



        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }



        });
        web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress<100){
                    mProgress.setVisibility(View.VISIBLE);
                    Message msg=new Message();
                    msg.what=0;
                    msg.obj=newProgress;
                    mHandler.sendMessage(msg);
                }else{
                    mProgress.setVisibility(View.GONE);
                }
            }

        });
        web.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
    }
}
