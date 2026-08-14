-- V50: 占位（保留 flyway 版本号与历史 script 文件名）
-- 原 inspection_method 实例 REF 方案已废弃；物理清理由 V55 执行。
-- 新库无 inspection_method 表，本迁移为空操作。

SET search_path TO dynamicbusiness, public;

DO $$ BEGIN NULL; END $$;
