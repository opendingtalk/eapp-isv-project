package com.enums;

/**
 * 钉钉给ISV的推送事件。
 * 请关注钉钉开放平稳文档。
 */
public enum BizTypeEnum {
    /**
     * 套件票据
     */
    SUITE_TICKET("套件票据",2),
    /**
     * 企业授权变更
     */
    ORG_AUTH("企业授权变更",4),
    /**
     * 企业微应用变更
     */
    ORG_MICROAPP("企业微应用变更",7),
    /**
     * 企业用户变更
     */
    ORG_USER("企业用户变更",13),
    /**
     * 企业部门变更
     */
    ORG_DEPT("企业部门变更",14),
    /**
     * 企业角色变更
     */
    ORG_ROLE("企业角色变更",15),
    /**
     * 企业变更
     */
    ORG("企业变更",16),

    /**
     * 市场订单
     */
    MARKET_ORDER("市场订单",17);


    private final String name;

    private final Integer value;

    BizTypeEnum(String tagName, Integer tagType){
        this.name = tagName;
        this.value = tagType;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }


    public static BizTypeEnum getBizTypeEnum(Integer value){
        BizTypeEnum[] bizTypeArr = BizTypeEnum.values();
        for (BizTypeEnum o : bizTypeArr) {
            if (o.getValue().equals(value)) {
                return o;
            }
        }
        return null;
    }

}
