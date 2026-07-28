-- 路网种类 SITE → FACILITY（与 NetworkKind 枚举对齐）
UPDATE platform_path_network
SET network_kind = 'FACILITY'
WHERE network_kind = 'SITE';

UPDATE platform_mobility_profile
SET allowed_network_kinds = replace(allowed_network_kinds::text, '"SITE"', '"FACILITY"')::jsonb
WHERE allowed_network_kinds::text LIKE '%"SITE"%';
