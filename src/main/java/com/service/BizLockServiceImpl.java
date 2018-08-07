package com.service;

import com.mapper.biz.BizLockMapper;
import com.model.BizLockDO;
import com.util.LogFormatter;
import com.util.ServiceResult;
import com.util.ServiceResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 锁服务。DB锁。
 */
@Service("bizLockService")
public class BizLockServiceImpl {
    private static final Logger mainLogger  = LoggerFactory.getLogger(BizLockServiceImpl.class);
    private static final Logger bizLogger = LoggerFactory.getLogger("PROJECT_LOG");

    @Resource
    private BizLockMapper bizLockMapper;

    ServiceResult<BizLockVO> tryLock(String lockKey, Long seconds){
        bizLogger.info(LogFormatter.getKVLogData(LogFormatter.LogEvent.START,
                LogFormatter.KeyValue.getNew("lockKey", lockKey),
                LogFormatter.KeyValue.getNew("seconds", seconds)
        ));
        try{
            Long lastLockSec = 0L;
            BizLockDO bizLockDO = bizLockMapper.getBizLock(lockKey);
            //如果DB中的锁记录存在,并且DB的当前时间小于锁过期的预期时间。那么说明锁是正常存在的
            //说明有线程正在持有锁
            if(null!=bizLockDO&&bizLockDO.getCurTime()<=bizLockDO.getExpire()){
                return ServiceResult.failure(ServiceResultCode.TRY_LOCK_FAILE_LOCK_EXIST.getErrCode(),ServiceResultCode.TRY_LOCK_FAILE_LOCK_EXIST.getErrMsg());
            }
            //如果DB中锁存在,但是DB的当时时间已经比锁过期的预期时间大了。说明锁已经过期了,但是因为某些原因删除这个锁失败了。
            //所以要被动的删掉过期的锁。当删除锁出现并发的时候,哪个线程删掉了锁,哪个线程被允许去加锁。
            if(null!=bizLockDO&&bizLockDO.getCurTime()>bizLockDO.getExpire()){
                //bizLockDO.getExpire() - bizLockDO.getGmtCreate()
                //根据时间判断。计算一下之前被连续锁了几次。做为锁延时的计数器
                int delCount = bizLockMapper.deleteBizLock(lockKey,bizLockDO.getId());
                lastLockSec = bizLockDO.getExpire() - bizLockDO.getGmtCreate().getTime()/1000;
                if(delCount<1){
                    return ServiceResult.failure(ServiceResultCode.GET_LOCK_FAILE_LOCK_DELETE_FAILE.getErrCode(),ServiceResultCode.GET_LOCK_FAILE_LOCK_DELETE_FAILE.getErrMsg());
                }
            }
            //向DB中插入数据,利用唯一索引排他来加锁.
            bizLockMapper.insertBizLock(lockKey,seconds);
            bizLockDO = bizLockMapper.getBizLock(lockKey);
            BizLockVO bizLockVO = new BizLockVO();
            bizLockVO.setId(bizLockDO.getId());
            bizLockVO.setLastLockSec(lastLockSec);
            bizLockVO.setGmtCreate(bizLockDO.getGmtCreate());
            bizLockVO.setExpire(bizLockDO.getExpire());
            return ServiceResult.success(bizLockVO);
        }catch (Exception e){
            String errLog = LogFormatter.getKVLogData(LogFormatter.LogEvent.START,
                    "系统异常获取锁失败"+e.toString(),
                    LogFormatter.KeyValue.getNew("lockKey", lockKey),
                    LogFormatter.KeyValue.getNew("seconds", seconds)
            );
            bizLogger.error(errLog,e);
            return ServiceResult.failure(ServiceResultCode.SYS_ERROR.getErrCode(),ServiceResultCode.SYS_ERROR.getErrMsg());
        }
    }

    /**
     * 删除锁。
     * 为什么要加上ID呢。因为当DB中存在过期的锁的情况下,两个线程并发的去删除过期锁,加上新的锁。这种场景下如果没有ID做乐观锁版本控制。
     * 删除会误删除新加的锁，从而引起BUG
     * @param lockKey 锁Key
     * @param id      锁ID。主键ID
     */
    ServiceResult<Boolean> unLock(String lockKey,Long id){
        bizLogger.info(LogFormatter.getKVLogData(LogFormatter.LogEvent.START,
                LogFormatter.KeyValue.getNew("lockKey", lockKey),
                LogFormatter.KeyValue.getNew("id", id)
        ));
        bizLockMapper.deleteBizLock(lockKey,id);
        return ServiceResult.success(Boolean.TRUE);
    }


    static class BizLockVO{
        //本次拿到锁的主键
        private Long id;
        private Date gmtCreate;
        //上一次加锁的具体时间。如果上一次的锁已经不存在，那么这个字段返回0
        private Long lastLockSec;

        private Long expire;
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getLastLockSec() {
            return lastLockSec;
        }

        public void setLastLockSec(Long lastLockSec) {
            this.lastLockSec = lastLockSec;
        }

        public Long getExpire() {
            return expire;
        }

        public void setExpire(Long expire) {
            this.expire = expire;
        }

        public Date getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Date gmtCreate) {
            this.gmtCreate = gmtCreate;
        }
    }
}
