package com.model;

import java.util.Date;
/**
 * 读取钉钉云推送RDS数据表的DO
 */
public class OpenSyncBizDataDO {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    /**
     * 订阅者ID。默认格式为suiteid_0的格式
     */
    private String subscribeId;
    /**
     * 发生变更事件的所属企业corpid
     * 也就是bizId字段的所属企业corpid
     */
    private String corpId;
    /**
     * 业务类型。具体值参见DingCloudPushBizTypeEnum的value字段
     * bizType和bizId确认一条业务数据。
     */
    private int bizType;
    /**
     * 业务ID
     */
    private String bizId;
    /**
     * 业务ID记录所对应的业务数据。JSON格式
     */
    private String bizData;
    /**
     * 钉钉保留字段。勿动
     */
    private Integer openCursor;
    /**
     * 数据记录状态。
     * 钉钉推送数据过来默认该字段值为0
     * 如果ISV轮训读取了该记录，并将该记录存入自己的数据表中，该字段更新为1。表示已经处理完毕
     * 如果处理过程中处理失败。可以将该字段标记为除0，1之外的其他值。进入人工处理。
     */
    private Integer status;


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

    public String getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(String subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public int getBizType() {
        return bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public String getBizData() {
        return bizData;
    }

    public void setBizData(String bizData) {
        this.bizData = bizData;
    }

    public Integer getOpenCursor() {
        return openCursor;
    }

    public void setOpenCursor(Integer openCursor) {
        this.openCursor = openCursor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
