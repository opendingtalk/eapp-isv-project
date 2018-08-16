package com.model.biz;

import java.util.Date;

/**
 * DB锁模型
 */
public class BizLockDO {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;

    private String lockKey;
    /**
     * 锁过期的时间。距今秒数
     */
    private Long expire;
    /**
     * 这个字段并不在表中。这个字段的值取DB的当前时间。要注意
     */
    private Long curTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Long getCurTime() {
        return curTime;
    }

    public void setCurTime(Long curTime) {
        this.curTime = curTime;
    }
}
