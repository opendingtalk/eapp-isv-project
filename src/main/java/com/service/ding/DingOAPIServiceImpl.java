package com.service.ding;

import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.enums.DingCloudPushBizTypeEnum;
import com.mapper.biz.AuthedCorpMapper;
import com.mapper.ding.OpenSyncBizDataMapper;
import com.model.AuthedCorpDO;
import com.model.OpenSyncBizDataDO;
import com.service.SystemConfigServiceImpl;
import com.service.biz.BizLockServiceImpl;
import com.util.LogFormatter;
import com.util.ServiceResult;
import com.util.ServiceResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.config.ApiUrlConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.DingTalkSignatureUtil;
import com.taobao.api.ApiException;

/**
 * 对钉钉开放平台服务端SDK的封装类
 * 避免到处写SDK的调用逻辑
 */
@Service("dingOAPIService")
public class DingOAPIServiceImpl {
    private static final Logger bizLogger = LoggerFactory.getLogger(DingOAPIServiceImpl.class);
    private static final Logger mainLogger = LoggerFactory.getLogger(DingOAPIServiceImpl.class);

    @Resource
    private SystemConfigServiceImpl systemConfigService;
    @Resource
    private OpenSyncBizDataMapper openSyncBizDataMapper;
    @Resource
    private AuthedCorpMapper authedCorpMapper;
    @Resource
    private BizLockServiceImpl bizLockService;

    /**
     * ISV获取企业访问凭证。获取企业的accessToken
     * @param corpId 授权企业的corpId
     */
    public ServiceResult<String> getOapiServiceGetCorpToken(String corpId, String suiteKey) {
        if (corpId == null || corpId.isEmpty()) {
            return null;
        }
        //先从本地拿企业的token
        AuthedCorpDO authedCorpDO = authedCorpMapper.getAuthedCorp(corpId,suiteKey);
        if(null==authedCorpDO){
            return ServiceResult.failure(ServiceResultCode.CORP_NOT_AUTH.getErrCode(),ServiceResultCode.CORP_NOT_AUTH.getErrMsg());
        }
        Long expireAt = authedCorpDO.getAccessTokenExpire();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10);//为了防止误差,提前10分钟更新corptoken
        if (calendar.getTime().getTime()<expireAt) {
            //如果当前时间加上10分钟的毫秒数比过期的绝对时间小。说明tokne尚在有些有消息。直接使用。
            return ServiceResult.success(authedCorpDO.getAccessToken());
        }else{
            //否则认为企业的token过期了。重新获取token。
            //加锁,防止并发
            String lockKey = corpId+"_"+suiteKey+"_token";
            ServiceResult<BizLockServiceImpl.BizLockVO> tryLockSr = bizLockService.tryLock(lockKey,10L);
            if (!tryLockSr.isSuccess()) {
                return ServiceResult.failure(tryLockSr.getCode(),tryLockSr.getMessage());
            }
            try{
                long timestamp = System.currentTimeMillis();
                //正式应用应该由钉钉通过开发者的回调地址动态获取到
                String suiteTicket = getSuiteTicket(systemConfigService.getSuiteId());
                String signature = DingTalkSignatureUtil.computeSignature(systemConfigService.getSuiteSecret(), DingTalkSignatureUtil.getCanonicalStringForIsv(timestamp, suiteTicket));
                Map<String, String> params = new LinkedHashMap<String, String>();
                params.put("timestamp", String.valueOf(timestamp));
                params.put("suiteTicket", suiteTicket);
                params.put("accessKey", systemConfigService.getSuiteKey());
                params.put("signature", signature);
                String queryString = DingTalkSignatureUtil.paramToQueryString(params, "utf-8");
                DingTalkClient client = new DefaultDingTalkClient(ApiUrlConstant.URL_GET_CORP_TOKEN + "?" + queryString);
                OapiServiceGetCorpTokenRequest request = new OapiServiceGetCorpTokenRequest();
                request.setAuthCorpid(corpId);
                OapiServiceGetCorpTokenResponse response;
                response = client.execute(request, systemConfigService.getSuiteKey(), systemConfigService.getSuiteKey(), suiteTicket);
                //更新掉DB中的企业token时间
                authedCorpDO.setAccessToken(response.getAccessToken());
                authedCorpDO.setAccessTokenExpire(System.currentTimeMillis() + response.getExpiresIn() * 1000);
                authedCorpDO.setPermanentCode("permanentCode");
                authedCorpMapper.addOrUpdateAuthedCorp(authedCorpDO);
                return ServiceResult.success(response.getAccessToken());
            }catch (Exception e){
                String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.START,
                        LogFormatter.KeyValue.getNew("corpId", corpId),
                        LogFormatter.KeyValue.getNew("suiteKey", suiteKey)
                );
                bizLogger.info(errLog,e);
                mainLogger.error(errLog,e);
                return ServiceResult.failure(ServiceResultCode.SYS_ERROR.getErrCode(),ServiceResultCode.SYS_ERROR.getErrMsg());
            }finally {
                //解锁
                bizLockService.unLock(lockKey,tryLockSr.getResult().getId());
            }
        }
    }

    /**
     * 通过钉钉服务端API获取用户在当前企业的userId
     * @param accessToken 企业访问凭证Token
     * @param code        免登code
     * @
     */
    public OapiUserGetuserinfoResponse getOapiUserGetuserinfo(String accessToken, String code) {
        DingTalkClient client = new DefaultDingTalkClient(ApiUrlConstant.URL_GET_USER_INFO);
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

        DingTalkClient client = new DefaultDingTalkClient(ApiUrlConstant.URL_GET_USER_NICK);
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

        DingTalkClient client = new DefaultDingTalkClient(ApiUrlConstant.URL_SEND_LINK_MESSAGE + "?access_token=" + accessToken);
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();

        request.setUseridList(userIdList.get(0));
        request.setAgentId(agentId);
        request.setToAllUser(false);
        //这个参数提出来。
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
     * 直接读钉钉云推送的数据库。直接拿到suiteTicket
     * @return suiteTicket
     */
    private String getSuiteTicket(Long suiteId) {
        String subscibeId = suiteId+"_0";
        OpenSyncBizDataDO openSyncBizDataDO = openSyncBizDataMapper.getOpenSyncBizData(subscibeId,systemConfigService.getIsvCorpId(), DingCloudPushBizTypeEnum.SUITE_TICKET.getValue(),String.valueOf(suiteId));
        return JSON.parseObject(openSyncBizDataDO.getBizData()).getString("suiteTicket");
    }

}
