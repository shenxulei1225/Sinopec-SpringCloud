-- 编排头不再承载 What/How 槽。详情跟谁认布局栏 + 关系图连线；点树/点行只认 object_pick_from。

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.dm_five_w_orchestration
    DROP COLUMN IF EXISTS what_mode,
    DROP COLUMN IF EXISTS what_config,
    DROP COLUMN IF EXISTS how_mode,
    DROP COLUMN IF EXISTS how_config;

COMMENT ON TABLE dynamicbusiness.dm_five_w_orchestration IS
    '数据目录编排头：是否启用、点树还是点列表行即当前对象。开哪些栏认页面布局。';
