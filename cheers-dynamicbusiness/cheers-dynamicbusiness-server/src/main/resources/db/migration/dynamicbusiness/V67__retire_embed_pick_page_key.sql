-- V67: 废止 catalog:{code}:embed-pick 过渡约定；嵌入复用目录 dataLayoutId
-- 软删历史 page_ref 与「嵌入勾选·*」布局实例（栏行随逻辑删除策略由应用侧过滤）

SET search_path TO dynamicbusiness, public;

-- 页面引用：过渡 page_key
UPDATE dynamicbusiness.dm_page_layout_ref
SET deleted = TRUE,
    updater = 'flyway-v67',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND page_key LIKE 'catalog:%:embed-pick';

-- 工作台布局头：历史「嵌入勾选·」实例（非模版）
UPDATE dynamicbusiness.dm_workbench_layout w
SET deleted = TRUE,
    updater = 'flyway-v67',
    update_time = CURRENT_TIMESTAMP
WHERE w.deleted = FALSE
  AND w.is_template = FALSE
  AND w.name LIKE '嵌入勾选·%';

-- 对应栏行
UPDATE dynamicbusiness.dm_data_tab_layout t
SET deleted = TRUE,
    updater = 'flyway-v67',
    update_time = CURRENT_TIMESTAMP
WHERE t.deleted = FALSE
  AND t.layout_id IN (
    SELECT id FROM dynamicbusiness.dm_workbench_layout
    WHERE updater = 'flyway-v67'
      AND name LIKE '嵌入勾选·%'
      AND is_template = FALSE
  );

COMMENT ON TABLE dynamicbusiness.dm_page_layout_ref IS
    '页面→布局：layout_id=整页布局；data_layout_id=页内嵌入工作台实例（须显式配置；禁止 catalog:*:embed-pick 约定键）';
