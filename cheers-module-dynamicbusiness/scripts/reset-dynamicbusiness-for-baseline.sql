-- Windows / 半成品库：删掉 dynamicbusiness schema，让 Flyway 重新执行新版 V1 + V2
-- 会清空该 schema 下全部业务数据（含 biz_*、模型、能力投影等）。
--
-- 用法：
--   psql -h 127.0.0.1 -U postgres -d sinopec -f scripts/reset-dynamicbusiness-for-baseline.sql
-- 然后启动 cheers-module-dynamicbusiness-server（local），或 mvn flyway:migrate
--
-- 适用：只跑过旧 V1–V3 + V14–V16、未跑 V4–V13，数据不完整。

DROP SCHEMA IF EXISTS dynamicbusiness CASCADE;

\echo 'dynamicbusiness schema 已删除。请启动服务或 flyway:migrate，将自动执行 V1 -> V2。'
