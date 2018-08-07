package com.mapper.ding;

import com.model.OpenSyncBizDataDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OpenSyncBizDataBaseMapper {
    OpenSyncBizDataDO getOpenSyncBizData(@Param("subscribeId") String subscribeId,
                                         @Param("corpId") String corpId,
                                         @Param("bizType") Integer bizType,
                                         @Param("bizId") String bizId);


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
