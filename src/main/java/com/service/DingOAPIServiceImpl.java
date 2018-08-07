package com.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.config.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.DingTalkSignatureUtil;
import com.taobao.api.ApiException;

@Service("dingOAPIService")
public class DingOAPIServiceImpl {
    private static final Logger bizLogger = LoggerFactory.getLogger(DingOAPIServiceImpl.class);

    @Resource
    private SystemConfigServiceImpl systemConfigService;
    /**
     * ISV获取企业访问凭证
     *
     * @param corpId 授权企业的corpId
     */
    public OapiServiceGetCorpTokenResponse getOapiServiceGetCorpToken(String corpId) {
        if (corpId == null || corpId.isEmpty()) {
            return null;
        }

        long timestamp = System.currentTimeMillis();
        //正式应用应该由钉钉通过开发者的回调地址动态获取到
        String suiteTicket = getSuiteTickt(systemConfigService.getSuiteKey());
        String signature = DingTalkSignatureUtil.computeSignature(systemConfigService.getSuiteSecret(), DingTalkSignatureUtil.getCanonicalStringForIsv(timestamp, suiteTicket));
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("timestamp", String.valueOf(timestamp));
        params.put("suiteTicket", suiteTicket);
        params.put("accessKey", systemConfigService.getSuiteKey());
        params.put("signature", signature);
        String queryString = DingTalkSignatureUtil.paramToQueryString(params, "utf-8");
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_CORP_TOKEN + "?" + queryString);
        OapiServiceGetCorpTokenRequest request = new OapiServiceGetCorpTokenRequest();
        request.setAuthCorpid(corpId);
        OapiServiceGetCorpTokenResponse response;
        try {
            response = client.execute(request, systemConfigService.getSuiteKey(), systemConfigService.getSuiteKey(), suiteTicket);
        } catch (ApiException e) {
            bizLogger.info(e.toString(), e);
            return null;
        }
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return response;
    }

    /**
     * 通过钉钉服务端API获取用户在当前企业的userId
     *
     * @param accessToken 企业访问凭证Token
     * @param code        免登code
     * @
     */
    public OapiUserGetuserinfoResponse getOapiUserGetuserinfo(String accessToken, String code) {
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_INFO);
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(code);
        request.setHttpMethod("GET");

        OapiUserGetuserinfoResponse response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return response;
    }

    /**
     * 通过钉钉服务端API获取用户的昵称
     *
     * @param accessToken 企业访问凭证Token
     * @param userId      用户在当前企业的userId
     * @
     */
    public OapiUserGetResponse getOapiUserGetUserName(String accessToken, String userId) {

        DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_GET_USER_NICK);
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("GET");
        OapiUserGetResponse response = null;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        if (response == null || !response.isSuccess()) {
            return null;
        }
        return response;
    }

    /**
     * ISV发送链接通知消息
     *
     * @param agentId     授权企业的agentId
     * @param userIdList  用户列表：测试中只包含一个用户
     * @param url         发送消息包含的url地址
     * @param accessToken 授权企业的accessToken
     */
    public OapiMessageCorpconversationAsyncsendV2Response sendLinkMessage(Long agentId, List<String> userIdList, String url, String accessToken) {
        if (agentId == null || userIdList == null || userIdList.isEmpty() || accessToken == null) {
            return null;
        }

        DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_SEND_LINK_MESSAGE + "?access_token=" + accessToken);
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();

        request.setUseridList(userIdList.get(0));
        request.setAgentId(agentId);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("link");
        msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
        msg.getLink().setTitle("E应用消息"+new Random().nextLong());
        msg.getLink().setText("第一个E应用消息消息体"+ new Random().nextLong());
        msg.getLink().setMessageUrl(url);
        msg.getLink().setPicUrl("@lADOdvRYes0CbM0CbA");
        request.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response response;
        try {
            response = client.execute(request, accessToken);
        } catch (ApiException e) {
            bizLogger.info(e.toString(), e);
            return null;
        }
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return response;
    }

    /**
     * suiteTicket是一个定时变化的票据，主要目的是为了开发者的应用与钉钉之间访问时的安全加固。
     * 测试应用：可随意设置，钉钉只做签名不做安全加固限制。
     * 正式应用：开发者应该从自己的db中读取suiteTicket,suiteTicket是由开发者在开发者平台设置的应用回调地址，由钉钉定时推送给应用，
     * 由开发者在回调地址所在代码解密和验证签名完成后获取到的.正式应用钉钉会在开发者代码访问时做严格检查。
     *
     * @return suiteTicket
     */
    private String getSuiteTickt(String suiteKey) {
        //正式应用必须由应用回调地址从钉钉推送获取
        return "zngkJOKb4RmK";

    }

}
