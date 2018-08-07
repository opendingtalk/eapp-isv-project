package com.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * ISV自己的业务数据源配置
 */
@Configuration
@MapperScan(basePackages = "com.mapper.biz", sqlSessionTemplateRef  = "bizSqlSessionTemplate")
public class BizDataSourceConfig {
    /**
     * dataSource
     */
    @Bean(name = "bizDataSource")
    @ConfigurationProperties(prefix = "datasource.biz")
    public DataSource bizDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * sqlSessionFactory
     * @param dataSource    数据源
     */
    @Bean(name = "bizSqlSessionFactory")
    public SqlSessionFactory bizSqlSessionFactory(@Qualifier("bizDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/biz/*.xml"));
        return bean.getObject();
    }

    /**
     * dataSourceTransactionManager
     * @param dataSource    数据源
     */
    @Bean(name = "bizTransactionManager")
    public DataSourceTransactionManager bizTransactionManager(@Qualifier("bizDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * sqlSessionTemplate
     * @param sqlSessionFactory  sqlsession
     */
    @Bean(name = "bizSqlSessionTemplate")
    public SqlSessionTemplate bizSqlSessionTemplate(@Qualifier("bizSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
