
## 开发工具
INTELLIJ IDEA

## 项目结构
```
├── README.md
├── eapp-isv-project.iml
├── lib
│   ├── taobao-sdk-java-auto_1479188381469-20180801-source.jar
│   └── taobao-sdk-java-auto_1479188381469-20180801.jar
├── log
│   └── project.log
├── pom.xml
└── src
    ├── eapp-isv-project.sql
    ├── main
    │   ├── java
    │   │   └── com
    │   │       ├── Application.java
    │   │       ├── config
    │   │       │   ├── BizDataSourceConfig.java
    │   │       │   ├── DingDataSourceConfig.java
    │   │       │   └── URLConstant.java
    │   │       ├── controller
    │   │       │   └── IndexController.java
    │   │       ├── enums
    │   │       │   ├── BizTypeEnum.java
    │   │       │   └── SyncDataStatusEnum.java
    │   │       ├── mapper
    │   │       │   ├── biz
    │   │       │   │   ├── AuthedCorpMapper.java
    │   │       │   │   └── BizLockMapper.java
    │   │       │   └── ding
    │   │       │       ├── OpenSyncBizDataBaseMapper.java
    │   │       │       ├── OpenSyncBizDataMapper.java
    │   │       │       └── OpenSyncBizDataMediumMapper.java
    │   │       ├── model
    │   │       │   ├── AuthedCorpDO.java
    │   │       │   ├── BizLockDO.java
    │   │       │   └── OpenSyncBizDataDO.java
    │   │       ├── service
    │   │       │   ├── BizLockServiceImpl.java
    │   │       │   ├── DingCloudPushDataService.java
    │   │       │   ├── DingOAPIServiceImpl.java
    │   │       │   ├── EnvironmentServiceImpl.java
    │   │       │   └── SystemConfigServiceImpl.java
    │   │       └── util
    │   │           ├── LogFormatter.java
    │   │           ├── ServiceResult.java
    │   │           └── ServiceResultCode.java
    │   └── resources
    │       ├── application.properties
    │       ├── logback.xml
    │       └── mybatis
    │           └── mapper
    │               ├── biz
    │               │   ├── authed_corp_mapper.xml
    │               │   └── biz_lock_mapper.xml
    │               └── ding
    │                   ├── open_sync_biz_data_mapper.xml
    │                   └── open_sync_biz_data_medium_mapper.xml
    └── test
        └── java
            └── com
                ├── ApplicationTests.java
                ├── mapper
                │   ├── AuthedCorpMapperTest.java
                │   ├── BizLockMapperTest.java
                │   └── OpenSyncBizDataBaseMapperTest.java
                └── service
                    ├── BizLockServiceTest.java
                    ├── EnvironmentServiceTest.java
                    └── SystemConfigServiceTest.java
```
                    
                
## 项目配置
1.更新application.properties文件的数据连接属性和服务器启动端口。

## 打包命令
mvn clean package  -Dmaven.test.skip=true  
打成的包在工程文件的target目录下。文件为  "工程名"-"版本号".jar。()

## 服务部署    
java -jar  target/"工程名"-"版本号".jar


TODO 
https://open-doc.dingtalk.com/microapp/isv/zzqcc6

//包含功能