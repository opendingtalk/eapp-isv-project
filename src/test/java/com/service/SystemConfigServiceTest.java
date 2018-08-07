package com.service;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SystemConfigServiceTest {
    @Resource
    private SystemConfigServiceImpl systemConfigService;

    @Test
    public void testGetPreEvnCorpIdList() {
        List<String> list = systemConfigService.getPreEvnCorpIdList();
        System.err.println(JSON.toJSONString(list));
    }
}
