package com.service.biz;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配置服务。存储当前项目的配置
 */
@Service("systemConfigService")
public class SystemConfigServiceImpl {

    @Value("${suiteId}")
    private Long suiteId;
    @Value("${suiteKey}")
    private String suiteKey;
    @Value("${suiteSecret}")
    private String suiteSecret;
    @Value("${preEvnCorpIdList}")
    private String preEvnCorpIdList;
    @Value("${isvCorpId}")
    private String isvCorpId;
    public Long getSuiteId(){
        return suiteId;
    }

    public String getSuiteKey() {
        return suiteKey;
    }

    public String getSuiteSecret() {
        return suiteSecret;
    }

    public List<String> getPreEvnCorpIdList() {
        return JSON.parseArray(preEvnCorpIdList,String.class);
    }

    public String getIsvCorpId() {
        return isvCorpId;
    }
}
