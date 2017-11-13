package com.sctjsj.jcyn.adapter;

import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.bean.CommentBean;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aaron on 2017/3/9.
 */

public class DetailsCommentAdapter extends BaseAdapter {
    List<CommentBean> list ;
    Context context;

    public DetailsCommentAdapter(Context context, List<CommentBean> list) {
        this.context = context;
        this.list = list;
        Log.e("adapter",list.toString());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoleder viewHoleder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_details_comment_item,null);
            viewHoleder = new ViewHoleder();
            viewHoleder.userImg = (CircleImageView) convertView.findViewById(R.id.activity_details_comment_item_civ);
            viewHoleder.commentImg = (ImageView) convertView.findViewById(R.id.activity_details_comment_item_iv);
            viewHoleder.userName = (TextView) convertView.findViewById(R.id.activity_details_comment_item_nametv);
            viewHoleder.userComment = (TextView) convertView.findViewById(R.id.activity_details_comment_item_tv);
            viewHoleder.timeTv = (TextView) convertView.findViewById(R.id.activity_details_comment_item_timetv);
            convertView.setTag(viewHoleder);
        }else{
            viewHoleder = (ViewHoleder) convertView.getTag();
        }
        Picasso.with(context).load(list.get(position).getUserImg()).into(viewHoleder.userImg);
        Log.e("1111111111",list.get(position).getCommentImg());
        if(list.get(position).getCommentImg()==null||list.get(position).getCommentImg().equals("vvv")){
            Log.e("1111111111",list.get(position).getCommentImg());
            viewHoleder.commentImg.setVisibility(View.GONE);
        }else {
            viewHoleder.commentImg.setVisibility(View.VISIBLE);
            Picasso.with(context).load(list.get(position).getCommentImg()).into(viewHoleder.commentImg);
        }
        viewHoleder.userName.setText(list.get(position).getUserName());
        viewHoleder.userComment.setText(list.get(position).getCommentContent());
        viewHoleder.timeTv.setText(list.get(position).getCommentTime());
        return convertView;
    }
    class ViewHoleder{
        ImageView commentImg;
        CircleImageView userImg;
        TextView userName,userComment,timeTv;
    }
}
