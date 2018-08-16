
--建库。ISV自己的业务库。库名可以自行制定
CREATE DATABASE isv_dingtalk_biz DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE USER 'isv_biz'@'%' IDENTIFIED BY 'ISV@@##Pass123';


GRANT DELETE,SELECT,UPDATE,INSERT ON `isv_dingtalk_biz`.* TO 'isv_biz'@'%';


CREATE TABLE `biz_lock` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `lock_key` varchar(128) NOT NULL COMMENT '锁Key',
  `expire` bigint(20) unsigned NOT NULL COMMENT '锁过期时间戳',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lock` (`lock_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步业务锁';


CREATE TABLE `authed_corp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '更新时间',
  `corp_name` varchar(128) NOT NULL COMMENT '企业名称',
  `corp_id` varchar(64) NOT NULL COMMENT '企业ID',
  `access_token` varchar(128) NOT NULL COMMENT '企业访问AccessToken',
  `access_token_expire` bigint(20) NOT NULL COMMENT '企业访问AccessToken过期时间毫秒,绝对时间',
  `permanent_code` varchar(128) NOT NULL DEFAULT '' COMMENT '企业访问开通应用的永久授权码,已弃用',
  `suite_key` varchar(128) NOT NULL COMMENT '企业开通的应用/套件',
  `agent_id` bigint(20) unsigned DEFAULT NULL COMMENT '企业开通应用实例id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_corp_suite` (`corp_id`,`suite_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COMMENT='授权开通应用套件的企业';


-- ISV可以在改数据库中建立自己的业务表。处理自己的业务逻辑。