package com.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EnvironmentServiceTest {
    @Resource
    private EnvironmentServiceImpl environmentService;
    @Test
    public void testGetEnvironment() {
        System.err.println(environmentService.isDaily());
        System.err.println(environmentService.isOnline());
        System.err.println(environmentService.isPre());
    }
}
