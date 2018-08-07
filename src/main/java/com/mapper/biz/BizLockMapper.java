package com.mapper.biz;

import com.model.BizLockDO;
import org.apache.ibatis.annotations.Param;

public interface BizLockMapper {
    int insertBizLock(@Param("lockKey") String lockKey,@Param("relativeExpire") Long relativeExpire);

    int deleteBizLock(@Param("lockKey") String lockKey,@Param("id") Long id);


    BizLockDO getBizLock(@Param("lockKey") String lockKey);

}
