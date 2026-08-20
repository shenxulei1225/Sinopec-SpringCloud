-- V60: 编排不再持久化选层；Who 开列认布局，What 绑层认 bindLayer

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.dm_five_w_orchestration
    DROP COLUMN IF EXISTS selection_level;

COMMENT ON TABLE dynamicbusiness.dm_five_w_orchestration IS
    '五维编排语义块（What/How 模式）；与 data-tab-layout 并行、互不覆盖';
