-- 检查项定义及方法属于全网主数据；目录元数据是过滤规则的权威来源。
-- 仅修正检查项定义 REUSE 入口与既有 NATIVE 标准库，不触碰站场级 DOMAIN 入口。
UPDATE dynamicbusiness.dynamic_entity_type
SET work_scope = 'NETWORK',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE work_scope IS DISTINCT FROM 'NETWORK'
  AND (
    (
      entry_kind = 'REUSE'
      AND lower(code) IN ('inspection-content', 'inspection_content')
    )
    OR (
      entry_kind = 'NATIVE'
      AND lower(code) IN ('inspection_item', 'inspection_method')
    )
  );
