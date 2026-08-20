-- 布局行扩展 jsonb：历史字段名 category_column 易被读成「仅分类栏」。
-- 实际按 column_kind 存分类 / 型号 / 实体各自扩展，统一改名为 column_meta（列扩展）。

ALTER TABLE dm_data_tab_layout
  RENAME COLUMN category_column TO column_meta;

COMMENT ON COLUMN dm_data_tab_layout.column_meta IS
  '列扩展 jsonb：按 column_kind 解释为分类栏 / 型号栏 / 实体栏扩展（含 columnSection、widthPx 等）';
