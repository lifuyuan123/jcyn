package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.adapter.QueryListAdapter;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.CityNameBean;
import com.sctjsj.jcyn.bean.QuerySearchListBean;
import com.sctjsj.jcyn.fragment.MapFg;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.SessionUtil;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/3/10.
 */

public class QueryActivity extends BaseAppcompatActivity {
    @Bind(R.id.activity_search_et)EditText searchEt;
    @Bind(R.id.activity_search_record_lv)ListView recordLv;
    @Bind(R.id.activity_search_lv)ListView searchLv;
    @Bind(R.id.activity_search_ll)LinearLayout searchLl;
    String url="http://118.123.22.190:8088/vr-zone/obtain_article_like_list$ajax.htm?keyword=%E4%B8%AD&jf=";
    //搜索记录
    private List<String> list = new ArrayList<>();
    private List<CityNameBean> stringList=new ArrayList<>();
    //搜索结果
    private List<QuerySearchListBean>  queryList = new ArrayList<>();

    private MsApp msApp;
    private String lan,lang,cityName;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    searchLv.setAdapter(new QueryListAdapter(QueryActivity.this,queryList));
                    break;
                case 2:
                    SerchAdapter adapter=new SerchAdapter(stringList);
                    searchLv.setAdapter(adapter);
                    searchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent=new Intent(QueryActivity.this,MapFg.class);
                            intent.putExtra("lan",stringList.get(i).getLat());
                            intent.putExtra("lang",stringList.get(i).getLont());
                            setResult(103,intent);
                            finish();
                        }
                    });
                    break;
            }
        }
    };
    @Override
    public void setView() {
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        msApp = (MsApp) getApplication();
        list = msApp.getSearchlv();
        searchLl.setVisibility(View.VISIBLE);
        recordLv.setAdapter(new recordAdapter());
        recordLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchEt.setText(list.get(i));
                searchEt.setSelection(list.get(i).length());
            }
        });

        searchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QueryActivity.this,DetailsActivity.class);
                intent.putExtra("videoId",queryList.get(position).getId());
                startActivity(intent);
                finish();
            }
        });
        //文本输入监听
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.activity_search_back_rl,
            R.id.activity_search_tv_rl})
    public void queryOnClick(View view){
        switch (view.getId()){
            case R.id.activity_search_back_rl:
                finish();
            break;
            case R.id.activity_search_tv_rl:
                stringList.clear();
                String search = searchEt.getText().toString();
                Log.i("111111query",search);
                if(search!=null&&!search.equals("")){
//                    searchMsg(search);
                    setch(search);
                    msApp.getSearchlv().add(search);
                    searchLl.setVisibility(View.GONE);
                    InputMethodManager imm =  (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
                    if(imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
                    }
                }
            break;
        }
    }
    private void setch(String s){
        stringList.clear();
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .url(url+s)
                .build();
        okHttpClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
               Log.e("111111onFailure",e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
//                Log.e("111111ononResponse",response.body().string());
                String data=response.body().string();
                if(data!=null&&!data.equals("")){
                    try {
                        JSONObject jsonObject=new JSONObject(data);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object=jsonArray.getJSONObject(i);
                            CityNameBean cityNameBean=new CityNameBean();
                            cityNameBean.setLat(object.getString("lat"));
                            cityNameBean.setLont(object.getString("lng"));
                            cityNameBean.setName(object.getString("address"));
                            cityName=object.getString("address");
                            stringList.add(cityNameBean);
                            Message message=new Message();
                            message.what=2;
                            handler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    private void searchMsg(String s){
//        //keyword=%E4%B8%AD&jf=thumbnail
//        RequestParams params = new RequestParams(BnUrl.queryMsg);
//        params.setHeader("cookie", SessionUtil.getCookie());
//        params.addBodyParameter("keyword",s);
//        params.addBodyParameter("jf","thumbnail");
////        LinUtil.newInstance().showLoading(true,"正在加载...",this);
//        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
//            @Override
//            public void onSuccess(JSONObject object) {
//                Log.i("query",object.toString());
//                if(object!=null){
//                    try {
//                        JSONArray result = object.getJSONArray("result");
//                        for(int i = 0;i<result.length(); i++){
//                            JSONObject objects = result.getJSONObject(i);
//                            JSONObject thumbnail = objects.getJSONObject("thumbnail");
//                            QuerySearchListBean qslb = new QuerySearchListBean();
//                            qslb.setTitle(objects.getString("title"));
//                            qslb.setKeyword(objects.getString("keyword"));
//                            qslb.setCollectNumber(objects.getString("collectNumber"));
//                            qslb.setId(objects.getString("id"));
//                            qslb.setImgUrl(thumbnail.getString("url"));
//                            queryList.add(qslb);
//                        }
//                        Log.i("query",queryList.toString());
//                        Message message = new Message();
//                        message.what = 0;
//                        handler.sendMessage(message);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
////             LinUtil.newInstance().dismissLoading();
//            }
//        });
//    }
    private class recordAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(view == null){
                view = LayoutInflater.from(QueryActivity.this).inflate(R.layout.acitivity_search_item,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.activity_search_item_tv);
                viewHolder.view=view.findViewById(R.id.view);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText(list.get(position));
            return view;
        }
        class ViewHolder{
            TextView textView;
            View view;
        }
    }

    private class SerchAdapter extends BaseAdapter{
        List<CityNameBean> list=new ArrayList<>();

        public SerchAdapter(List<CityNameBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(view == null){
                view = LayoutInflater.from(QueryActivity.this).inflate(R.layout.acitivity_search_itemcity,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.activity_search_item_tv);
                viewHolder.view=view.findViewById(R.id.view);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText(list.get(position).getName());
            return view;
        }
        class ViewHolder{
            TextView textView;
            View view;
        }
    }
}
