CREATE TABLE IF NOT EXISTS `plc` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_code` VARCHAR(64) NOT NULL COMMENT '分区编码',
  `plc_partition` VARCHAR(64) NOT NULL COMMENT '对应的分区',
  `plc_code` VARCHAR(128) NOT NULL COMMENT 'PLC定义的topic',
  `ip` VARCHAR(45) NOT NULL COMMENT 'PLC对应的IP地址',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plc_partition` (`plc_partition`),
  UNIQUE KEY `uk_plc_code` (`plc_code`),
  KEY `idx_area_code` (`area_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='PLC信息表';