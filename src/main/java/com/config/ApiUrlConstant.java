package com.config;

/**
 * 钉钉开放平台接口URL的常量类
 */
public class ApiUrlConstant {
	
	/**
     * 获取可访问企业相关信息的accessToken的URL
     */
    public static final String URL_GET_CORP_TOKEN = "https://oapi.dingtalk.com/service/get_corp_token";
    
    /**
     *获取用户在企业内userId的接口URL
     */
    public static final String URL_GET_USER_INFO = "https://oapi.dingtalk.com/user/getuserinfo";
    
    /**
     *获取用户昵称的接口URL
     */
    public static final String URL_GET_USER_NICK = "https://oapi.dingtalk.com/user/get";

    /**
     * 发送工作链接通知消息的URL
     */
    public static final String URL_SEND_LINK_MESSAGE = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";

}
