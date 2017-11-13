package com.sctjsj.jcyn.bean;

import java.io.Serializable;

/**
 * Created by Aaron on 2017/3/9.
 */

public class CommentBean implements Serializable {
    //用户头像
    private String userImg;
    //用户名称
    private String userName;
    //评论信息
    private String commentContent;
    //评论图片
    private String commentImg;
    //评论时间
    private String commentTime;

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentImg() {
        return commentImg;
    }

    public void setCommentImg(String commentImg) {
        this.commentImg = commentImg;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "CommentBean{" +
                "commentContent='" + commentContent + '\'' +
                ", userImg='" + userImg + '\'' +
                ", userName='" + userName + '\'' +
                ", commentImg='" + commentImg + '\'' +
                ", commentTime='" + commentTime + '\'' +
                '}';
    }
}
