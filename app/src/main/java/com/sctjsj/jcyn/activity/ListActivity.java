package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.adapter.ListActivityAdapter;
import com.sctjsj.jcyn.bean.ListActivityBean;
import com.sctjsj.jcyn.util.BnUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/**
 * Created by Aaron on 2017/3/17.
 */

public class ListActivity extends BaseAppcompatActivity {
    @Bind(R.id.basegreen_second_title_tv)TextView indroationTv;
    @Bind(R.id.basegreen_title_tv)TextView titleTv;
    @Bind(R.id.activity_list_refresh)QRefreshLayout qRefresh;
    @Bind(R.id.actiity_list_lv)ListView lv;
    @Bind(R.id.basegreen_back_rl)RelativeLayout relativeLayout;


    private List<ListActivityBean> list = new ArrayList<>();
    private String typeId = null;
    private String popId = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Log.e("laa",list.toString());
                    lv.setAdapter(new ListActivityAdapter(ListActivity.this,list));
                    if(list.size()<1){
                        Toast.makeText(ListActivity.this,"此分类暂时无数据",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };
    @Override
    public void setView() {
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        titleTv.setText("VR城市空间");
        indroationTv.setVisibility(View.GONE);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(getIntent()!=null){
           typeId = (String) getIntent().getSerializableExtra("typeId");
            popId = (String) getIntent().getSerializableExtra("popId");
            Log.e("la",popId+","+typeId);
            queryListMsg(popId,typeId);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this,DetailsActivity.class);
                intent.putExtra("videoId",list.get(position).getId());
                startActivity(intent);
                finish();
            }
        });

        qRefresh.setLoadMoreEnable(false);
        qRefresh.setRefreshHandler(new RefreshHandler() {
            //下啦刷新
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                list.clear();
                queryListMsg(popId,typeId);
            }
            //上啦加载更多
            @Override
            public void onLoadMore(QRefreshLayout refresh) {

            }
        });
    }

    @Override
    public void reloadData() {

    }

    public void queryListMsg(String popId , String typeId){
        RequestParams params = new RequestParams(BnUrl.queryList);
        // column_id=6&parent_id=1&jf=thumbnail
        params.setUseCookie(false);
        params.addBodyParameter("column_id",typeId);
        params.addBodyParameter("parent_id",popId);
        params.addBodyParameter("jf","thumbnail");
        Log.e("la",typeId+","+popId);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject object) {
                Log.e("la",object.toString());
                if(object!=null){
                    try {
                        JSONArray reslutList = object.getJSONArray("resultList");
                        for(int i=0;i<reslutList.length();i++){
                            JSONObject obj = reslutList.getJSONObject(i);
                            JSONObject thumbnail = obj.getJSONObject("thumbnail");
                            ListActivityBean lab = new ListActivityBean();
                            lab.setId(obj.getString("id"));
                            lab.setTitle(obj.getString("title"));
                            lab.setIndroation(obj.getString("keyword"));
                            lab.setCollection(obj.getString("collectNumber"));
                            lab.setImg(thumbnail.getString("url"));
                            list.add(lab);
                        }
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                  qRefresh.refreshComplete();
            }
        });


    }
}
