package com.sctjsj.jcyn.bean;

/**
 * Created by Administrator on 2017/4/24.
 */

public class VideoImg {
    private String PictureImg;
    private String PictureUrl;
    private String VideoImg;
    private String VideoUrl;
    private String introduction;

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String getPictureImg() {
        return PictureImg;
    }

    public void setPictureImg(String pictureImg) {
        PictureImg = pictureImg;
    }

    public String getPictureUrl() {
        return PictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        PictureUrl = pictureUrl;
    }

    public String getVideoImg() {
        return VideoImg;
    }

    public void setVideoImg(String videoImg) {
        VideoImg = videoImg;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "VideoImg{" +
                "PictureImg='" + PictureImg + '\'' +
                ", PictureUrl='" + PictureUrl + '\'' +
                ", VideoImg='" + VideoImg + '\'' +
                ", VideoUrl='" + VideoUrl + '\'' +
                ", introduction='" + introduction + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
