package com.mapper.ding;

import com.model.OpenSyncBizDataDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TODO
 * https://open-doc.dingtalk.com/microapp/ln6dmh/troq7i
 */
public interface OpenSyncBizDataBaseMapper {
    /**
     * 唯一索引查询  TODO
     * @param subscribeId
     * @param corpId
     * @param bizType
     * @param bizId
     * @return
     */
    OpenSyncBizDataDO getOpenSyncBizData(@Param("subscribeId") String subscribeId,
                                         @Param("corpId") String corpId,
                                         @Param("bizType") Integer bizType,
                                         @Param("bizId") String bizId);

    /**
     * 为什么exlude TODO
     * @param subscribeId
     * @param excludeCorpIdList
     * @param status
     * @param limit
     * @return
     */
    List<OpenSyncBizDataDO> getOpenSyncBizDataListExcludeCorpIdByStatus(@Param("subscribeId") String subscribeId,
                                                                        @Param("excludeCorpIdList") List<String> excludeCorpIdList,
                                                                        @Param("status") Integer status,
                                                                        @Param("limit") Integer limit);


    List<OpenSyncBizDataDO> getOpenSyncBizDataListIncludeCorpIdByStatus(@Param("subscribeId") String subscribeId,
                                                                        @Param("includeCorpIdList") List<String> includeCorpIdList,
                                                                        @Param("status") Integer status,
                                                                        @Param("limit") Integer limit);


    void updateOpenSyncBizDataStatus(@Param("id") Long id,@Param("status") Integer status);
    
    void insertMessage2OpenSyncBizData(OpenSyncBizDataDO openSyncBizDataDO);
}
