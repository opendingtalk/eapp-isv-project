package com.mapper.biz;

import com.model.biz.BizLockDO;
import org.apache.ibatis.annotations.Param;

/**
 * 为了代码中不引入过多的中间件产品依赖。采用了DB锁的实现方式。
 * 免责:DB锁服务，没有经过真实项目的检验，性能稳定性可能有问题。推荐redis等产品实现。
 */
public interface BizLockMapper {

    int insertBizLock(@Param("lockKey") String lockKey,@Param("relativeExpire") Long relativeExpire);

    int deleteBizLock(@Param("lockKey") String lockKey,@Param("id") Long id);


    BizLockDO getBizLock(@Param("lockKey") String lockKey);

}
