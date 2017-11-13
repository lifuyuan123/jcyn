package com.sctjsj.jcyn.bean;



import java.io.Serializable;

/**
 * Created by Aaron on 2017/3/21.
 */

public class Comments implements Serializable{
    public Creator creator;
    public Article article;
    public Accessory accessory;
    private String content;


    public Accessory getAccessory() {
        return accessory;
    }

    public void setAccessory(Accessory accessory) {
        this.accessory = accessory;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "accessory=" + accessory +
                ", creator=" + creator +
                ", article=" + article +
                ", content='" + content + '\'' +
                '}';
    }
}
