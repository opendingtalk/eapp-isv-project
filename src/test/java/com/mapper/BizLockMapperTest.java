package com.mapper;

import com.alibaba.fastjson.JSON;
import com.mapper.biz.BizLockMapper;
import com.model.biz.BizLockDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BizLockMapperTest {
    @Resource
    private BizLockMapper bizLockMapper;
    @Test
    public void testInsertBizLock() {
        bizLockMapper.insertBizLock("lockKey",100L);
    }

    @Test
    public void testGetBizLock() {
        BizLockDO bizLockDO = bizLockMapper.getBizLock("lockKey");
        System.err.println(JSON.toJSON(bizLockDO));
    }

    @Test
    public void testDeleteBizLock() {
        bizLockMapper.deleteBizLock("lockKey",1L);
    }

}
