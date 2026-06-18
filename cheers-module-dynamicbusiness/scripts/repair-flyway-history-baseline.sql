-- 一次性修复：旧版 Flyway 历史（含 V1–V3、V14–V16、V4–V13 等任意组合）→ 新版基线 V1 + V2
-- 不删业务表、不重新导入数据，只改 flyway_schema_history_dynamicbusiness。
--
-- ⚠️ 若 Windows 仅执行过 V1–V3 + V14–V16（未跑 V4–V13），数据不完整，
--    请改用 reset-dynamicbusiness-for-baseline.sql 重建后再跑 V1+V2，不要用本脚本。
--
-- 用法（Mac / Windows 本机 PostgreSQL）：
--   psql -h 127.0.0.1 -U postgres -d sinopec -f scripts/repair-flyway-history-baseline.sql
--
-- 执行后重启 dynamicbusiness-server，或：
--   cd cheers-module-dynamicbusiness-server
--   mvn flyway:repair flyway:validate

SET search_path TO dynamicbusiness;

DELETE FROM flyway_schema_history_dynamicbusiness;

INSERT INTO flyway_schema_history_dynamicbusiness (
    installed_rank,
    version,
    description,
    type,
    script,
    checksum,
    installed_by,
    installed_on,
    execution_time,
    success
) VALUES
    (
        1,
        '1',
        'init dynamicbusiness schema',
        'SQL',
        'V1__init_dynamicbusiness_schema.sql',
        NULL,
        'repair-baseline',
        NOW(),
        0,
        TRUE
    ),
    (
        2,
        '2',
        'seed dynamicbusiness data',
        'SQL',
        'V2__seed_dynamicbusiness_data.sql',
        NULL,
        'repair-baseline',
        NOW(),
        0,
        TRUE
    );

SELECT installed_rank, version, description, success
FROM flyway_schema_history_dynamicbusiness
ORDER BY installed_rank;
