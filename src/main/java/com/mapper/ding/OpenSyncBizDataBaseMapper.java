package com.mapper.ding;

import com.model.OpenSyncBizDataDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 处理钉钉云推送数据的抽象DAO方法。
 * 由于钉钉云推送的两张表open_sync_biz_data,open_sync_biz_data_medium字段结构完全相同，
 * 所以两个DAO采用相同的方法和入参、出参。
 * 参考文档:https://open-doc.dingtalk.com/microapp/ln6dmh/troq7i
 */
public interface OpenSyncBizDataBaseMapper {
    /**
     * 唯一索引查询
     * @param subscribeId   订阅者ID。形式为suiteid_0
     * @param corpId        当前业务发生的所属企业。
     * @param bizType       业务类型。参考枚举 DingCloudPushBizTypeEnum
     * @param bizId         业务ID。
     */
    OpenSyncBizDataDO getOpenSyncBizData(@Param("subscribeId") String subscribeId,
                                         @Param("corpId") String corpId,
                                         @Param("bizType") Integer bizType,
                                         @Param("bizId") String bizId);

    /**
     * 查询收件箱列表列表
     * @param subscribeId           订阅者ID。形式为suiteid_0
     * @param excludeCorpIdList     按照corpid字段做排除。
     * @param status                收件箱处理状态
     * @param limit                 查询列表大小
     */
    List<OpenSyncBizDataDO> getOpenSyncBizDataListExcludeCorpIdByStatus(@Param("subscribeId") String subscribeId,
                                                                        @Param("excludeCorpIdList") List<String> excludeCorpIdList,
                                                                        @Param("status") Integer status,
                                                                        @Param("limit") Integer limit);

    /**
     * 查询收件箱列表列表
     * @param subscribeId           订阅者ID。形式为suiteid_0
     * @param includeCorpIdList     按照corpid字段做过滤。
     * @param status                收件箱处理状态
     * @param limit                 查询列表大小
     * @return
     */
    List<OpenSyncBizDataDO> getOpenSyncBizDataListIncludeCorpIdByStatus(@Param("subscribeId") String subscribeId,
                                                                        @Param("includeCorpIdList") List<String> includeCorpIdList,
                                                                        @Param("status") Integer status,
                                                                        @Param("limit") Integer limit);

    /**
     * 更新收件箱的状态
     * @param id        主键ID。这里不能用唯一索引来更新。防止并发。
     * @param status    处理状态
     */
    void updateOpenSyncBizDataStatus(@Param("id") Long id,@Param("status") Integer status);
}
