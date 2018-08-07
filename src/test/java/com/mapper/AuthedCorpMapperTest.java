package com.mapper;

import com.alibaba.fastjson.JSON;
import com.mapper.biz.AuthedCorpMapper;
import com.model.AuthedCorpDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthedCorpMapperTest {
    @Resource
    private AuthedCorpMapper authedCorpMapper;
    @Test
    public void testAddOrUpdateAuthedCorp() {
        AuthedCorpDO authedCorpDO = new AuthedCorpDO();
        authedCorpDO.setCorpName("corpName");
        authedCorpDO.setCorpId("corpId");
        authedCorpDO.setAccessToken("accesasToken");
        authedCorpDO.setAccessTokenExpire(1234567L);
        authedCorpDO.setSuiteKey("suiteKey");
        authedCorpDO.setPermanentCode("permanentCode");
        authedCorpMapper.addOrUpdateAuthedCorp(authedCorpDO);
    }


    @Test
    public void testGetAuthedCorp() {
        AuthedCorpDO authedCorpDO = authedCorpMapper.getAuthedCorp("corpId","suiteKey");
        System.err.println(JSON.toJSONString(authedCorpDO));
    }
}
