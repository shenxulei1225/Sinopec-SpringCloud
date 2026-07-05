-- 清理 dynamic 业务 model/entity 树组件能力投影中的历史错误读树 URL（误用 POC 分类树 / 空 URL）。
-- 删除后由 BusinessCapabilityServiceImpl 在下次 getProjection 时按 CapabilityBlockProjectionBuilder 重建。
SET search_path TO dynamicbusiness;

DELETE FROM capability_component_projection
WHERE component_code = 'tree'
  AND data_kind IN ('model', 'entity')
  AND deleted = 0
  AND (
    component_interface IS NULL
    OR btrim(component_interface) = ''
    OR component_interface LIKE '%/dynamicbusiness/category/tree%'
    OR component_interface LIKE '%/system/category/tree%'
  );
