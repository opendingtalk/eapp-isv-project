package com.mapper.biz;

import com.model.AuthedCorpDO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 开通应用的企业信息
 * 建表SQL参见项目中的eapp-isv-project.sql文件
 */
@Mapper
public interface AuthedCorpMapper {

    void addOrUpdateAuthedCorp(AuthedCorpDO authedCorpDO);

    AuthedCorpDO getAuthedCorp(@Param("corpId") String corpId,@Param("suiteKey")String suiteKey);
}
