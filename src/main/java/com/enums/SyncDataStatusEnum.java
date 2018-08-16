package com.enums;

/**
 * 钉钉给ISV的推送事件。
 * 请关注钉钉开放平稳文档。https://open-doc.dingtalk.com/microapp/ln6dmh/troq7i
 */
public enum SyncDataStatusEnum {

    WAITING("等待处理状态",0),

    SUCCEEDED("处理成功状态",1),

    FAILED("处理失败状态",2);

    private final String name;

    private final Integer value;

    SyncDataStatusEnum(String tagName, Integer tagType){
        this.name = tagName;
        this.value = tagType;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }


    public static SyncDataStatusEnum getBizTypeEnum(Integer value){
        SyncDataStatusEnum[] arr = SyncDataStatusEnum.values();
        for (SyncDataStatusEnum o : arr) {
            if (o.getValue().equals(value)) {
                return o;
            }
        }
        return null;
    }

}
