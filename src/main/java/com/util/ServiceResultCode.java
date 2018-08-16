package com.util;

/**
 * 全局错误码
 * 如果业务方有自己的业务错误码,可以重新定义
 * Created by lifeng.zlf on 2016/3/4.
 */
public enum ServiceResultCode {
    /**获取套件suiteToken失败**/
    SUCCESS("0","success"),
    SYS_ERROR("-1","系统繁忙"),

    LOCK_EXIST("20001","获取锁失败,有线程正在持有锁"),
    LOCK_DELETE_FAILED("20002","获取锁失败,删除过期锁失败"),

    CORP_NOT_AUTH("20003","企业没有授权开通应用");

    private String errCode;
    private String errMsg;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    private ServiceResultCode(String errCode, String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
