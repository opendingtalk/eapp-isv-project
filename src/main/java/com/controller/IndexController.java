package com.controller;

import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.mapper.biz.AuthedCorpMapper;
import com.model.AuthedCorpDO;
import com.service.DingOAPIServiceImpl;
import com.service.SystemConfigServiceImpl;
import com.util.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * ISV E应用Quick-Start示例代码
 * 实现了最简单的免密登录（免登）功能
 */
@RestController
public class IndexController {
    private static final Logger bizLogger = LoggerFactory.getLogger(IndexController.class);

    @Resource
    private DingOAPIServiceImpl dingOAPIService;
    @Resource
    private AuthedCorpMapper authedCorpMapper;
    @Resource
    private SystemConfigServiceImpl systemConfigService;
    /**
     * 欢迎页面
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome() {
        //用作模拟测试免登
        return "welcome";
    }


    /**
     * 钉钉用户登录，显示当前登录的企业和用户
     *
     * @param corpId          授权企业的CorpId
     * @param requestAuthCode 免登临时code
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult login(@RequestParam(value = "corpId") String corpId,
                               @RequestParam(value = "authCode") String requestAuthCode) {
        Long start = System.currentTimeMillis();
        //获取accessToken,注意正式代码要有异常流处理
        OapiServiceGetCorpTokenResponse oapiServiceGetCorpTokenResponse = dingOAPIService.getOapiServiceGetCorpToken(corpId);
        String accessToken = oapiServiceGetCorpTokenResponse.getAccessToken();
        //获取企业信息
        AuthedCorpDO authedCorpDO = authedCorpMapper.getAuthedCorp(corpId,systemConfigService.getSuiteKey());
        //查询等得到企业名
        String corpName = authedCorpDO.getCorpName();

        //获取用户信息
        OapiUserGetuserinfoResponse oapiUserGetuserinfoResponse = dingOAPIService.getOapiUserGetuserinfo(accessToken, requestAuthCode);
        //3.查询得到当前用户的userId
        // 获得到userId之后应用应该处理应用自身的登录会话管理（session）,避免后续的业务交互（前端到应用服务端）每次都要重新获取用户身份，提升用户体验
        String userId = oapiUserGetuserinfoResponse.getUserid();

        //查询得到当前用户的name
        OapiUserGetResponse oapiUserGetResponse = dingOAPIService.getOapiUserGetUserName(accessToken, userId);
        String userName = oapiUserGetResponse.getName();

        //返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("corpId", corpId);
        resultMap.put("corpName", corpName);
        resultMap.put("userId", userId);
        resultMap.put("userName", userName);
        ServiceResult serviceResult = ServiceResult.success(resultMap);
        return serviceResult;
    }

    /**
     * 钉钉用户登录，显示当前登录的企业和用户
     *
     * @param corpId          授权企业的CorpId
     * @param userId          消息接收人的userId
     */
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult sendMsg(@RequestParam(value = "corpId") String corpId,
                                 @RequestParam(value = "userId") String userId) {
        Long start = System.currentTimeMillis();
        //获取accessToken,注意正式代码要有异常流处理
        OapiServiceGetCorpTokenResponse oapiServiceGetCorpTokenResponse = dingOAPIService.getOapiServiceGetCorpToken(corpId);
        String accessToken = oapiServiceGetCorpTokenResponse.getAccessToken();

        //获取企业信息
        AuthedCorpDO authedCorpDO = authedCorpMapper.getAuthedCorp(corpId,systemConfigService.getSuiteKey());
        Long agentId = authedCorpDO.getAgentId();
        //设置消息接收人
        List<String> userIdList = new ArrayList<>();
        userIdList.add(userId);
        //发送一个带有参数的链接消息。客户端打开应用可以拿到参数

        String url = "eapp://page/index/index?param=random_"+new Random().nextLong();
        OapiMessageCorpconversationAsyncsendV2Response response = dingOAPIService.sendLinkMessage(agentId,userIdList,url,accessToken);

        //返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("errCode", response.getErrcode());

        ServiceResult serviceResult = ServiceResult.success(resultMap);
        return serviceResult;
    }

}


