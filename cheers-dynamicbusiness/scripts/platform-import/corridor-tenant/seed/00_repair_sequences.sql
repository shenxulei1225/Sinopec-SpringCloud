-- 避免 dynamic_model 等序列落后于 MAX(id)（历史手工/跨租户导入后常见）
SET search_path TO dynamicbusiness;

SELECT setval(
  pg_get_serial_sequence('dynamic_model', 'id'),
  (SELECT COALESCE(MAX(id), 1) FROM dynamic_model)
);
