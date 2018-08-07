package com.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 运行环境服务。
 * 判断当前程序运行的环境。
 */
@Service("environmentService")
public class EnvironmentServiceImpl {
    @Value("${environment}")
    private String environment;

    /**
     * 是否是线上环境
     */
    public Boolean isOnline(){
        return "online".equals(environment);
    }

    /**
     * 是否是预发环境
     */
    public Boolean isPre(){
        return "pre".equals(environment);
    }

    /**
     * 是否是日常环境
     */
    public Boolean isDaily(){
        return "daily".equals(environment);
    }

    /**
     * 返回当前的环境
     */
    public String getEnvironment(){
        return environment;
    }
}
