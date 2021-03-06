package com.sctjsj.jcyn.util;

/**
 * Created by Chris-Jason on 2016/10/3.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式验证工具
 */
public class RegexpValidateUtil {
    private static RegexpValidateUtil instance;
    private RegexpValidateUtil(){

    }

    public static RegexpValidateUtil getInstance(){
        if(instance==null){
            instance=new RegexpValidateUtil();
        }
        return instance;
    }

    /**
     * 邮箱验证
     * @param email
     * @return
     */
    public static boolean checkEmail(String email){
        boolean flag = false;
        try{
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     * @param
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber){
        boolean flag = false;
        try{
            Pattern regex = Pattern.compile("^0?(13[0-9]|15[012356789]|18[0-9]|17[0-9])[0-9]{8}$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    public static boolean checkPwd(String pwd){
        //6-20位，中英文混合

       /* try{
            Pattern regex = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$");
            Matcher matcher = regex.matcher(pwd);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }*/
        //Log.e("xxxx:",pwd.length()+"");

        if(pwd.length()>=8){
            return true;
        }else{
            return false;
        }
    }
}
