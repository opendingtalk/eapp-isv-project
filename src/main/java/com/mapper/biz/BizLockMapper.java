package com.mapper.biz;

import com.model.BizLockDO;
import org.apache.ibatis.annotations.Param;

/**
 * 分布式DB锁服务
 * 推荐redis等产品实现。
 * TODO 不引入过多依赖。
 * 免责
 */
public interface BizLockMapper {
    /**
     * TODO 注释
     * @param lockKey
     * @param relativeExpire
     * @return
     */
    int insertBizLock(@Param("lockKey") String lockKey,@Param("relativeExpire") Long relativeExpire);

    int deleteBizLock(@Param("lockKey") String lockKey,@Param("id") Long id);


    BizLockDO getBizLock(@Param("lockKey") String lockKey);

}
