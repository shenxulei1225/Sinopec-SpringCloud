-- 判断当前库适合「只修 Flyway 历史」还是「重建 schema 再跑 V1+V2」
-- 用法：psql -h 127.0.0.1 -U postgres -d sinopec -f scripts/diagnose-baseline-readiness.sql

\echo '=== Flyway 历史 ==='
SELECT installed_rank, version, description, script, success
FROM dynamicbusiness.flyway_schema_history_dynamicbusiness
ORDER BY installed_rank;

\echo ''
\echo '=== 数据量（与 Mac 完整库对照：equipment ~10273, model ~115, capability dynamic ~15）==='
SELECT 'dynamic_entity_type' AS item, COUNT(*)::text AS cnt
FROM dynamicbusiness.dynamic_entity_type WHERE deleted = FALSE
UNION ALL
SELECT 'dynamic_model', COUNT(*)::text FROM dynamicbusiness.dynamic_model WHERE deleted = FALSE
UNION ALL
SELECT 'dynamic_field', COUNT(*)::text FROM dynamicbusiness.dynamic_field WHERE deleted = FALSE
UNION ALL
SELECT 'ent_equipment', COUNT(*)::text FROM dynamicbusiness.ent_equipment WHERE deleted = FALSE
UNION ALL
SELECT 'business_capability (dynamic)', COUNT(*)::text
FROM dynamicbusiness.business_capability WHERE deleted = FALSE AND business_category = 'dynamic'
UNION ALL
SELECT 'capability_component_projection', COUNT(*)::text
FROM dynamicbusiness.capability_component_projection WHERE deleted = FALSE;

\echo ''
\echo '=== 建议 ==='
\echo '若 Flyway 历史含 V3/V14 等旧版本，或 ent_equipment 接近 0：'
\echo '  -> 执行 reset-dynamicbusiness-for-baseline.sql 后启动服务（全量 V1+V2）'
\echo '若历史已是 V1+V2 且上表行数正常：'
\echo '  -> 无需重建；若仍报 checksum，执行 repair-flyway-history-baseline.sql'
