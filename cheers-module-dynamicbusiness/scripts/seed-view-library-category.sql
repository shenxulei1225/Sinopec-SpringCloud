-- 视图库分类基础数据（手动执行）
-- psql -h 127.0.0.1 -U postgres -d sinopec -f scripts/seed-view-library-category.sql
-- Schema: dynamicbusiness | categoryTypeCode = view

\echo '>>> 写入视图库分类类型与默认分类节点 ...'

\echo '>>> 写入视图库分类（V3 + V4）...'

\ir ../cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/V3__seed_view_library_category.sql
\ir ../cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/V4__seed_view_library_category_extend.sql

\echo '>>> 验证 dynamic_category_type'
SELECT id, category_type_code, name, top_level_category_id
FROM dynamicbusiness.dynamic_category_type
WHERE category_type_code = 'view' AND deleted = false;

\echo '>>> 验证 dynamic_category'
SELECT id, parent_id, name, code, category_type_code, tree_path, level, sort
FROM dynamicbusiness.dynamic_category
WHERE category_type_code = 'view' AND deleted = false
ORDER BY level, sort, id;
