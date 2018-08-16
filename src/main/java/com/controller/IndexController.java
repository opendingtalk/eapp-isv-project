package com.controller;

import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.mapper.biz.AuthedCorpMapper;
import com.model.biz.AuthedCorpDO;
import com.service.ding.DingOAPIServiceImpl;
import com.service.biz.SystemConfigServiceImpl;
import com.util.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * ISV三方企业E应用解决方案的实例代码
 * 实现以下功能
 * 1.程序自建欢迎页面
 * 2.自动获取用户身份，包含用户userid，昵称，企业信息。
 * 3.给自己发消息功能
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
     * 程序自检欢迎页面
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome() {
        //用作模拟测试免登
        return "welcome";
    }


    /**
     * 钉钉用户登录，显示当前登录的企业和用户
     * @param corpId          授权企业的corpId。当前登录用户应该是属于该企业的
     * @param requestAuthCode 免登临时code。由客户端传上来。
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult login(@RequestParam(value = "corpId") String corpId,
                               @RequestParam(value = "authCode") String requestAuthCode) {
        //获取accessToken,注意正式代码要有异常流处理
        ServiceResult<String> corpTokenSr = dingOAPIService.getOapiServiceGetCorpToken(corpId,systemConfigService.getSuiteKey());
        if(!corpTokenSr.isSuccess()){
            return corpTokenSr;
        }
        String accessToken = corpTokenSr.getResult();
        //访问ISV自己的数据库。获取企业信息
        AuthedCorpDO authedCorpDO = authedCorpMapper.getAuthedCorp(corpId,systemConfigService.getSuiteKey());
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
     * 钉钉用户发消息。
     * @param corpId          授权企业的CorpId
     * @param userId          消息接收人的userId
     */
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult sendMsg(@RequestParam(value = "corpId") String corpId,
                                 @RequestParam(value = "userId") String userId) {
        //获取accessToken,注意正式代码要有异常流处理
        ServiceResult<String> corpTokenSr = dingOAPIService.getOapiServiceGetCorpToken(corpId,systemConfigService.getSuiteKey());
        if(!corpTokenSr.isSuccess()){
            return corpTokenSr;
        }
        String accessToken = corpTokenSr.getResult();
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


