package com.sctjsj.jcyn.util;

/**
 * Created by Aaron on 2017/2/23.
 */

public class BnUrl {
    //退出接口
    public static String loginoutUrl="http://118.123.22.190:8088/vr-zone/tuser/logout.htm";
    //个推cid：2be95f70121d9a182807da0fdfe06f66
    //登录接口
    public static String loginUrl="http://118.123.22.190:8088/vr-zone/admin/login.htm";
    //注册接口
    public static String registerUrl="http://118.123.22.190:8088/vr-zone/register$ajax.htm?";
    //首页分类接口 http://118.123.22.190:8088/vr-zone/video_class$ajax.htm
    public static String homeFgTypeUrl="http://118.123.22.190:8088/vr-zone/video_class$ajax.htm?";
    //首页轮播图接口： http://localhost:8080/wp/home_slide$ajax.htm?jf=accessory
    public static String homeFgBannerUrl="http://118.123.22.190:8088/vr-zone/home_slide$ajax.htm?jf=accessory";
    //随机视频接口： http://localhost:8080/wp/random_video$ajax.htm?jf=thumbnail
    public static String homeFgVideoUrl="http://118.123.22.190:8088/vr-zone/random_video$ajax.htm?jf=thumbnail";
    //查询栏目下面的子栏目：比如说我要查询地产下面的子栏目：http://localhost:8080/wp/pageSearch$ajax.htm?ctype=column&cond={parent:{id:1}}
    // 这个id是名字是地产的id：1
    public static String homeFgPopUrl="http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?";
    //查询详情页面查询数据
    //http://118.123.22.190:8088/vr-zone/singleSearch$ajax.htm?
    // ctype=article&id=1&jf=accessorys|videos&size=999
    public static String deailsActivityUrl="http://118.123.22.190:8088/vr-zone/singleSearch$ajax.htm?";
    //详情页面评论
    //http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?
    //http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?
    // ctype=comment&cond={article:{id:1}}&jf=creator|accessory|headPortraitUrl&size=3
    // ctype=article&cond={id:1}&jf=comments|creator|headPortraitUrl|accessory&size=3
    public static String deailsCommentUrl="http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?";
    //详情页面推荐
    //http://118.123.22.190:8088/vr-zone/obtain_article_aecommend$ajax.htm?
    // jf=thumbnail
    public static String deailsRecommentUrl="http://118.123.22.190:8088/vr-zone/obtain_article_aecommend$ajax.htm?";

    //获取短信验证码
    public static String getSMSUrl="http://118.123.22.190:8088/vr-zone/verify_sms.htm?";

    //获取验证码图片
    public static String getVerifyCode="http://118.123.22.190:8088/vr-zone/verify_output.htm";

    //验证手机号
    public static final String verifyPhonenumUrl="http://118.123.22.190:8088/vr-zone/verify.htm?";

    //修改密码
    public static final String modifyPwdUrl="http://118.123.22.190:8088/vr-zone/tuser/updatepassword$ajax.htm?";

    //http://localhost:8080/wp/singleSearch$ajax.htm?ctype=user&id=82&jf=headPortraitUrl&size=999
    public static String UserDataUrl="http://118.123.22.190:8088/vr-zone/singleSearch$ajax.htm?";

    //获取视频页四条视频
    public static String FgVideoTopUrl="http://118.123.22.190:8088/vr-zone/obtain_video_aecommend$ajax.htm";

    //获取视频列表数据
    //http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?ctype=video&size=10
    public static String FgVideoListUrl="http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?";

    //判断是否登录
    public static String userLogin="http://118.123.22.190:8088/vr-zone/intf/isOnline.htm";

    //修改用户信息
    //http://118.123.22.190:8088/vr-zone/user/dataModify$ajax.htm?
    // ctype=user&data={id:2,gender:1,realName:"修改昵称",address:"修改地址"}
    public static String changeUserMsgUrl="http://118.123.22.190:8088/vr-zone/user/dataModify$ajax.htm?";

    //上传图片接口
    public static String upLoadImgUrl="http://118.123.22.190:8088/vr-zone/upload/uploadImgByAccessory.htm";

    //重设密码
    public static String resetPwdSUrl="http://118.123.22.190:8088/vr-zone/forgetpassword$ajax.htm";
   //校验旧手机
    //user/validation_code.htm
    public static String validateOldTelUrl="http://118.123.22.190:8088/vr-zone/tuser/validation_code$ajax.htm";

    //绑定新手机
    public static String bindNewPhone = "http://118.123.22.190:8088/vr-zone/tuser/upDateNewPhone$ajax.htm";
    //搜索
    //http://118.123.22.190:8088/vr-zone/obtain_article_like_list$ajax.htm?
    // keyword=%E4%B8%AD&jf=thumbnail
    public static String queryMsg ="http://118.123.22.190:8088/vr-zone/obtain_article_like_list$ajax.htm?";


    //http://118.123.22.190:8088/vr-zone/obtain_article_by_id$ajax.htm?
    // user_id=134&article_id=1
    //收藏
    public static String userColleageUrl="http://118.123.22.190:8088/vr-zone/obtain_article_by_id$ajax.htm?";
    //是否为收藏
    //http://118.123.22.190:8088/vr-zone/is_collect$ajax.htm?
    // id=136&article_id=1
    public static String isCollectUrl="http://118.123.22.190:8088/vr-zone/is_collect$ajax.htm?";
    //http://118.123.22.190:8088/vr-zone/obtain_article_column$ajax.htm?
    // column_id=6&parent_id=1&jf=thumbnail
    public static String queryList ="http://118.123.22.190:8088/vr-zone/obtain_article_column$ajax.htm?";
    //http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?
    // ctype=column&cond={parent:{id:1}}&jf=articles|videos|panorama&size=999

    public static String queryListAll = "";

    //用户评论
    //user/dataSave$ajax.htm?
    // ctype=comment&data={creator:{id:1},article:{id:1},accessory:{id:1},content: “我评论了数据222”}
    public static String userUpContent = "http://118.123.22.190:8088/vr-zone/user/dataSave$ajax.htm?";

    public static String userUpContentUrl = "http://118.123.22.190:8088/vr-zone/comment_save$ajax.htm?";
    //用户收藏
    //http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?
    // ctype=collect&cond={usere:{id:1}}&jf=article|thumbnail&size=999
    public static String userCollection = "http://118.123.22.190:8088/vr-zone/pageSearch$ajax.htm?";

    //用户删除
    //userid=1&params=1
    public static String deleteCollectionUrl="http://118.123.22.190:8088/vr-zone/user/delete_collect$ajax.htm?";


   //地图接口
    public static String mapUrl="http://118.123.22.190:8088/vr-zone/rangeSearch$ajax.htm?";

   //版本更新
    public static String verisonUpdata = "http://118.123.22.190:8088/vr-zone/last_version$ajax.htm?";
  //检查用户受否绑定邮箱
    public static String isBindEmailUrl="http://118.123.22.190:8088/vr-zone/tuser/validation_emial$ajax.htm";
  //绑定邮箱
    public static String bindEamilUrl="http://118.123.22.190:8088/vr-zone/user/upDateEmail$ajax.htm";
}
