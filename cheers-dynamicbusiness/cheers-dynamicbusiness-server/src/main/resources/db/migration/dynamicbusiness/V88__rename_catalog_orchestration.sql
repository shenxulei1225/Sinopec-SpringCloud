-- 编排头表去掉五维旧名。内容不变：是否启用、点树还是点列表行。

SET search_path TO dynamicbusiness, public;

ALTER TABLE IF EXISTS dynamicbusiness.dm_five_w_orchestration
    RENAME TO dm_catalog_orchestration;

ALTER INDEX IF EXISTS uk_dm_five_w_orchestration_registry
    RENAME TO uk_dm_catalog_orchestration_registry;

COMMENT ON TABLE dynamicbusiness.dm_catalog_orchestration IS
    '数据目录编排头：是否启用、点树还是点列表行即当前对象。开哪些栏认页面布局。';
