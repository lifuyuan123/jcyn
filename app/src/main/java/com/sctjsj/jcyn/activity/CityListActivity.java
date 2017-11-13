package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.adapter.CityAdapter;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.ChangeCityName;
import com.sctjsj.jcyn.bean.CityBean;
import com.sctjsj.jcyn.fragment.HomeFg;
import com.sctjsj.jcyn.fragment.MapFg;
import com.sctjsj.jcyn.util.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aaron on 2017/2/10.
 */

public class CityListActivity extends AppCompatActivity{
    private static final String INDEX_STRING_TOP = "↑";
    private RecyclerView mRv;
    private CityAdapter mAdapter;
    private LinearLayoutManager mManager;
    private List<CityBean> mDatas = new ArrayList<>();

    private SuspensionDecoration mDecoration;
    private MsApp msApp;

    private String CityName;

    private AMap aMap;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;

    @Bind(R.id.basegreen_second_rl)RelativeLayout relativeLayout;
    @Bind(R.id.basegreen_back_rl)RelativeLayout relativeLayout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citylist);
        ButterKnife.bind(this);
        relativeLayout.setVisibility(View.GONE);
        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(CityListActivity.this,IndexActivity.class);
//                startActivity(intent);
                finish();
            }
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        msApp = (MsApp) getApplication();

        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));

        mAdapter = new CityAdapter(this, mDatas);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, mDatas));
        //如果add两个，那么按照先后顺序，依次渲染。
        mRv.addItemDecoration(new DividerItemDecoration(CityListActivity.this, DividerItemDecoration.VERTICAL_LIST));

        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar

        //indexbar初始化
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager);//设置RecyclerView的LayoutManager


        //模拟线上加载数据
        initDatas(getResources().getStringArray(R.array.provinces));
        mAdapter.setOnItemClickListen(new CityAdapter.OnItemClickListen() {
            @Override
            public void setOnItemClickListen(String cityName) {
                Log.e("aabb",cityName);
               if(cityName!=null){
                   msApp.getSPF().edit().putString("cityName",cityName).commit();
                   Intent intent = new Intent(CityListActivity.this, MapFg.class);
                   intent.putExtra("city",cityName);
                   setResult(101,intent);
                   finish();
               }
            }
        });



    }

    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(final String[] data) {
        mDatas = new ArrayList<>();
        //微信的头部 也是可以右侧IndexBar导航索引的，
        // 但是它不需要被ItemDecoration设一个标题titile
        mDatas.add((CityBean) new CityBean("点击返回自动加载当前城市定位").setTop(true).setBaseIndexTag(INDEX_STRING_TOP));
        for (int i = 0; i < data.length; i++) {
            CityBean cityBean = new CityBean();
            cityBean.setCity(data[i]);//设置城市名称
            mDatas.add(cityBean);
        }
        mAdapter.setDatas(mDatas);
        mAdapter.notifyDataSetChanged();

        mIndexBar.setmSourceDatas(mDatas)//设置数据
                .invalidate();
        mDecoration.setmDatas(mDatas);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
//           startActivity(new Intent(this,IndexActivity.class));
           finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //    /**
//     * 更新数据源
//     *
//     * @param view
//     */
//    public void updateDatas(View view) {
//        for (int i = 0; i < 5; i++) {
//            mDatas.add(new CityBean("东京"));
//            mDatas.add(new CityBean("大阪"));
//        }
//
//        mIndexBar.setmSourceDatas(mDatas)
//                .invalidate();
//        mAdapter.notifyDataSetChanged();
//    }

    /**
     * 获取当前定位数据
     */

}
