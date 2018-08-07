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
 * 钉钉云推送的数据源配置
 */
@Configuration
@MapperScan(basePackages = "com.mapper.ding", sqlSessionTemplateRef  = "dingSqlSessionTemplate")
public class DingDataSourceConfig {
    /**
     * dataSource
     */
    @Bean(name = "dingDataSource")
    @ConfigurationProperties(prefix = "datasource.ding")
    @Primary
    public DataSource dingDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * sqlSessionFactory
     * @param dataSource    数据源
     */
    @Bean(name = "dingSqlSessionFactory")
    @Primary
    public SqlSessionFactory dingSqlSessionFactory(@Qualifier("dingDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/ding/*.xml"));
        return bean.getObject();
    }

    /**
     * 配置配置mybatis的DataSourceTransactionManager
     * @param dataSource    数据源
     */
    @Bean(name = "dingTransactionManager")
    @Primary
    public DataSourceTransactionManager dingTransactionManager(@Qualifier("dingDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * sqlSessionTemplate
     * @param sqlSessionFactory sqlsession
     */
    @Bean(name = "dingSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate dingSqlSessionTemplate(@Qualifier("dingSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
