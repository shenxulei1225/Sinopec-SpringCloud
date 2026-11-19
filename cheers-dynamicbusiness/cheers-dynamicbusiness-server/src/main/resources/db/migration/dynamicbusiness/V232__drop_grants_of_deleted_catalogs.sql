-- 一次性清理：数据目录已删除，角色在它上面的授权还留着。
-- 原因：删除数据目录时没有删角色授权。删除路径已改为一并删除（EntityTypeServiceImpl#delete）。
-- 留着的授权会让角色页列出不存在的目录，保存角色授权和可管目录时报「数据目录不存在」。
-- 范围：按租户核对，本租户里已没有同编码未删除目录的授权行，逻辑删除，和接口删除一致。
-- 执行时本库有 132 行：company_vehicle、pipeline_line、vehicle_dispatch，租户 1 和 123。
SET search_path TO dynamicbusiness;

UPDATE dynamic_catalog_role_grant g
SET deleted = true,
    updater = 'V232',
    update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_entity_type t
      WHERE t.code = g.registry_code
        AND t.tenant_id = g.tenant_id
        AND t.deleted = false
  );
