package com.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.enums.BizTypeEnum;
import com.enums.SyncDataStatusEnum;
import com.mapper.biz.AuthedCorpMapper;
import com.mapper.ding.OpenSyncBizDataBaseMapper;
import com.mapper.ding.OpenSyncBizDataMapper;
import com.mapper.ding.OpenSyncBizDataMediumMapper;
import com.model.AuthedCorpDO;
import com.model.OpenSyncBizDataDO;
import com.util.LogFormatter;
import com.util.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理钉钉云推送数据的服务
 * 将钉钉云推送过来的数据解析,插入ISV自己的数据库中
 */
@Service
public class DingCloudPushDataService {
    private static final Logger mainLogger = LoggerFactory.getLogger(DingCloudPushDataService.class);
    private static final Logger bizLogger = LoggerFactory.getLogger("PROJECT_LOG");
    //每次从收件箱中查出20条数据处理。此处的处理速度可以自行控制。
    private final int INBOX_SIZE = 20;
    //每次处理收件箱的时间间隔。如果对于处理速度不敏感。可以释放放宽该时间。注意不要改为0，会对DB的轮训压力过大。
    private final long SYNC_INTERVAL = 1000 * 3;
    @Resource
    private OpenSyncBizDataMapper openSyncBizDataMapper;
    @Resource
    private OpenSyncBizDataMediumMapper openSyncBizDataMediumMapper;
    @Resource
    private EnvironmentServiceImpl environmentService;
    @Resource
    private BizLockServiceImpl bizLockService;
    @Resource
    private SystemConfigServiceImpl systemConfigService;
    @Resource
    private AuthedCorpMapper authedCorpMapper;
    @Resource
    private DingOAPIServiceImpl dingOAPIService;

    /**
     * 处理钉钉推送过来的各种业务事件
     * @param subscribeId                   订阅者ID
     * @param openSyncBizDataBaseMapper     不同的处理dao抽象类
     */
    public void processor(String subscribeId, OpenSyncBizDataBaseMapper openSyncBizDataBaseMapper) {
        //根据优先级高低定义锁。没有获取锁直接返回。也可以使用TAIR实现
        List<OpenSyncBizDataDO> inboxList = new ArrayList<>();
        List<String> corpIdList = systemConfigService.getPreEvnCorpIdList();
        Long startTime = System.currentTimeMillis();
        String lockKey;
        if (openSyncBizDataBaseMapper instanceof OpenSyncBizDataMediumMapper) {
            lockKey = "ding_cloud_push_medium";
        } else if (openSyncBizDataBaseMapper instanceof OpenSyncBizDataMapper) {
            lockKey = "ding_cloud_push";
        } else {
            return;
        }
        ServiceResult<BizLockServiceImpl.BizLockVO> tryLockSr = bizLockService.tryLock(lockKey, 100L);
        if (!tryLockSr.isSuccess()) {
            return;
        }
        BizLockServiceImpl.BizLockVO bizLockVO = tryLockSr.getResult();
        try {
            if (environmentService.isOnline()) {
                //如果是线上服务,排除掉不需要处理的企业数据。这些排除掉的企业数据在预发和日常被测试使用。
                inboxList = openSyncBizDataBaseMapper.getOpenSyncBizDataListExcludeCorpIdByStatus(subscribeId, corpIdList, SyncDataStatusEnum.DEAL_WAIT.getValue(), INBOX_SIZE);
            }
            if (environmentService.isPre() || environmentService.isDaily()) {
                //如果是预发日常服务,只处理用于测试的企业。不要处理正常的线上数据。
                inboxList = openSyncBizDataBaseMapper.getOpenSyncBizDataListIncludeCorpIdByStatus(subscribeId, corpIdList, SyncDataStatusEnum.DEAL_WAIT.getValue(), INBOX_SIZE);
            }
            if (CollectionUtils.isEmpty(inboxList)) {
                return;
            }
            //处理收件箱数据。根据优先级和返回结果标记数据
            for (OpenSyncBizDataDO openSyncBizDataDO : inboxList) {
                String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END,
                        LogFormatter.KeyValue.getNew("openSyncBizDataBaseMapper", openSyncBizDataBaseMapper.getClass()),
                        LogFormatter.KeyValue.getNew("openSyncBizDataDO", JSON.toJSONString(openSyncBizDataDO))
                );
                bizLogger.info(errLog);
                //对于每一条消息，解析消息写入Biz数据库
                try {
                    SyncDataStatusEnum result = dealSyncInboxDO(openSyncBizDataDO);
                    openSyncBizDataBaseMapper.updateOpenSyncBizDataStatus(openSyncBizDataDO.getId(), result.getValue());
                } catch (Exception ex) {
                    bizLogger.error("subscribeId:" + subscribeId + "处理任务异常", ex);
                }
            }
        } catch (Throwable e) {
            String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END,
                    LogFormatter.KeyValue.getNew("subscribeId", subscribeId),
                    LogFormatter.KeyValue.getNew("env", environmentService.getEnvironment()),
                    LogFormatter.KeyValue.getNew("openSyncBizDataBaseMapper", openSyncBizDataBaseMapper.getClass())
            );
            bizLogger.error(errLog, e);
        } finally {
            //释放分布式锁后，处理线程先休息一下，然后继续扫描inbox表处理。否则DB会变成死循环。压力过大。
            bizLockService.unLock(lockKey, bizLockVO.getId());
            try {
                Thread.sleep(SYNC_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            processor(subscribeId, openSyncBizDataBaseMapper);
            String accessLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.END,
                    LogFormatter.KeyValue.getNew("subscribeId", subscribeId),
                    LogFormatter.KeyValue.getNew("env", environmentService.getEnvironment()),
                    LogFormatter.KeyValue.getNew("openSyncBizDataBaseMapper", openSyncBizDataBaseMapper.getClass()),
                    LogFormatter.KeyValue.getNew("size", inboxList.size()),
                    LogFormatter.KeyValue.getNew("cost", System.currentTimeMillis() - startTime)
            );
            bizLogger.info(accessLog);
        }
    }

    /**
     * 具体处理每种推送数据类型的方法
     * @param openSyncBizDataDO DO
     */
    public SyncDataStatusEnum dealSyncInboxDO(OpenSyncBizDataDO openSyncBizDataDO) {
        bizLogger.info(LogFormatter.getKVLogData(LogFormatter.LogEvent.END,
                LogFormatter.KeyValue.getNew("openSyncBizDataDO", JSON.toJSONString(openSyncBizDataDO))));
        if (null == openSyncBizDataDO) {
            return SyncDataStatusEnum.DEAL_SUCCESS;
        }
        SyncDataStatusEnum result = SyncDataStatusEnum.DEAL_FAILED;
        try {
            JSONObject bizObject = JSONObject.parseObject(openSyncBizDataDO.getBizData());
            String syncAction = bizObject.getString("syncAction");
            if (BizTypeEnum.SUITE_TICKET.getValue().equals(openSyncBizDataDO.getBizType())) {
                //套件SuiteKey票据
                //套件Ticket不存入ISV的业务库了,可以直接使用钉钉推送库的数据。
                result = SyncDataStatusEnum.DEAL_SUCCESS;
            } else if (BizTypeEnum.ORG_AUTH.getValue().equals(openSyncBizDataDO.getBizType())) {
                //企业和套件的授权关系处理。企业授权、解除授权、权限变更
                if ("org_suite_auth".equals(syncAction)) {
                    //企业授权应用套件
                    JSONObject corpObject = bizObject.getJSONObject("auth_corp_info");
                    JSONObject authObject = bizObject.getJSONObject("auth_info");
                    AuthedCorpDO authedCorpDO = new AuthedCorpDO();
                    authedCorpDO.setCorpName(corpObject.getString("corp_name"));
                    authedCorpDO.setCorpId(corpObject.getString("corpid"));
                    //使用钉钉服务端API获取accessToken及过期时间
                    OapiServiceGetCorpTokenResponse oapiServiceGetCorpTokenResponse = dingOAPIService.getOapiServiceGetCorpToken(corpObject.getString("corpid"));
                    String accessToken = oapiServiceGetCorpTokenResponse.getAccessToken();
                    Long expire = oapiServiceGetCorpTokenResponse.getExpiresIn();
                    authedCorpDO.setAccessToken(accessToken);
                    authedCorpDO.setAccessTokenExpire(expire);
                    bizObject.getString("permanent_code");
                    authedCorpDO.setSuiteKey(systemConfigService.getSuiteKey());
                    authedCorpDO.setPermanentCode("permanentCode");
                    //对于E应用,agent的数组只有一个长度
                    authedCorpDO.setAgentId(authObject.getJSONArray("agent").getJSONObject(0).getLong("agentid"));
                    authedCorpMapper.addOrUpdateAuthedCorp(authedCorpDO);
                    result = SyncDataStatusEnum.DEAL_SUCCESS;
                }
                //TODO 其他的syncActionISV自行处理
            } else if (BizTypeEnum.ORG_MICROAPP.getValue().equals(openSyncBizDataDO.getBizType())) {
                //企业微应用状态变更
                //TODO ISV自行处理该业务
            } else if (BizTypeEnum.ORG_USER.getValue().equals(openSyncBizDataDO.getBizType())) {
                //企业员工信息变更
                //TODO ISV自行处理该业务
            } else if (BizTypeEnum.ORG_DEPT.getValue().equals(openSyncBizDataDO.getBizType())) {
                //企业部门信息变更
                //TODO ISV自行处理该业务
            } else if (BizTypeEnum.ORG_ROLE.getValue().equals(openSyncBizDataDO.getBizType())) {
                //企业橘色信息变更
                //TODO ISV自行处理该业务
            } else {
                //TODO 关注新增回调事件类型
                bizLogger.error("未知收件箱bizType,请关注!" + openSyncBizDataDO.getBizType());
            }
            return result;
        } catch (Exception e) {
            bizLogger.error("处理任务失败:" + JSONObject.toJSONString(openSyncBizDataDO), e);
            return result;
        }
    }

    /**
     * 程序启动自动执行收件箱事件。
     * 同时这个任务还要配置DTS任务来执行。
     */
    @PostConstruct
    public void init() {
        Long suiteId = systemConfigService.getSuiteId();
        final String subscribeId = suiteId + "_0";
        //起两个线程处理高低优先级收件箱。
        new Thread(new Runnable() {
            @Override
            public void run() {
                processor(subscribeId,openSyncBizDataMapper);
            }
        }).start();
        bizLogger.error("start1.....");
        new Thread(new Runnable() {
            @Override
            public void run() {
                processor(subscribeId,openSyncBizDataMediumMapper);
            }
        }).start();
        bizLogger.error("start2.....");
    }
}
