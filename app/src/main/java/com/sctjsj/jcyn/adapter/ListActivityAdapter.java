package com.sctjsj.jcyn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.bean.ListActivityBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Aaron on 2017/3/17.
 */

public class ListActivityAdapter extends BaseAdapter {
    List<ListActivityBean> listBeen;
    Context context;

    public ListActivityAdapter(Context context, List<ListActivityBean> listBean) {
        this.context = context;
        this.listBeen = listBean;
    }

    @Override
    public int getCount() {
        return listBeen.size();
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
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.activity_list_item,null);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.activity_list_item_iv);
            viewHolder.title = (TextView) view.findViewById(R.id.activity_list_item_title_tv);
            viewHolder.keyWord = (TextView) view.findViewById(R.id.activity_list_item_keyword_tv);
            viewHolder.collegNum = (TextView) view.findViewById(R.id.activity_list_item_collage_tv);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Picasso.with(context).load(listBeen.get(position).getImg()).resize(200,200).into(viewHolder.imageView);
        viewHolder.title.setText(listBeen.get(position).getTitle());
        viewHolder.keyWord.setText(listBeen.get(position).getIndroation());
        viewHolder.collegNum.setText(listBeen.get(position).getCollection());
        return view;
    }
    class ViewHolder{
        ImageView imageView;
        TextView title,keyWord,collegNum;
    }
}
