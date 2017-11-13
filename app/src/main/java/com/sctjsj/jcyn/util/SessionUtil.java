package com.sctjsj.jcyn.util;

import org.xutils.http.cookie.DbCookieStore;

import java.net.HttpCookie;
import java.util.List;

/**
 * Created by lin on 2016/12/15.
 */
public class SessionUtil {
    private static String cookie;



    public static String getCookie(){
        //获取session
        DbCookieStore cookieInstance= DbCookieStore.INSTANCE;
        List<HttpCookie> cookies = cookieInstance.getCookies();
        for(HttpCookie c:cookies){
            String name=c.getName();
            String val=c.getValue();
            if("JSESSIONID".equals(name)){
                cookie=name+"="+val;
                break;
            }
        }
        return cookie;
    }


}