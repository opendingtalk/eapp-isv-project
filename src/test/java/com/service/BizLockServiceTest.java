package com.service;

import com.alibaba.fastjson.JSON;
import com.util.ServiceResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BizLockServiceTest {
    @Resource
    private BizLockServiceImpl bizLockService;

    @Test
    public void testTryLock() {
        ServiceResult<BizLockServiceImpl.BizLockVO> sr = bizLockService.tryLock("sss",100L);
        System.err.println(JSON.toJSONString(sr));
    }

    @Test
    public void testUnLock() {
        ServiceResult<Boolean> sr = bizLockService.unLock("sss",1L);
        System.err.println(JSON.toJSONString(sr));
    }

}
