-- 组织机构拆分（租户 1，一次性）· 第 3 步：场站所属组织、清旧键、下线租户 1 的运营区域目录。
--
-- 背景：原「运营区域 / 公司架构」（region）同时被当成公司组织架构用。现在公司 / 分公司 / 作业区
-- 由新数据目录「组织机构」（organization）持有。场站和部门通过「所属组织」（organization_id）关联它。
--
-- 执行顺序（前两步已经走平台接口完成，本脚本不重复）：
--   1. organization_split_2026-11-17_01_library_fields.sql：字段库加 驻地 / 职能定位 / 所属组织。
--   2. 平台接口：建组织机构目录与型号，挂基础字段（建列、建索引）。按分类新建接口复制 67 条组织记录
--      （分类节点、实体、绑定一起写）。实体删除接口删掉 42 个错误部门（应急指挥小组不是部门）。
--      基础字段删除接口拿掉 场站.region_id、设备.region_ids / region_id、部门.company_id。
--      业务类型删除接口删掉租户 1 的 region 注册项。
--   3. 本脚本。
--
-- 负责：
--   a. 每座场站的「所属组织」写成管辖它的作业区。依据是旧区域值（福建 38 座站 + 东营油库 + 金桥 + 洛阳）。
--      漳州首站、漳州LNG接收站、厦门集美分输阀室、水头分输站 旧值只到福建省公司，按地名推断，需业务确认。
--   b. 删掉场站 / 设备 / 部门扩展字段袋里已经不存在的旧键。
--   c. 业务类型删除接口只逻辑删注册项。租户 1 的 region 记录、型号、分类、布局、授权等在这里一并逻辑删。
-- 不负责：
--   第一次建错（注册项 592 已删）的组织机构目录在平台资源里留下的布局与组件配置。
--   176 个场站部门不改「所属场站」，也不填「所属组织」。部门到底挂在哪一级，业务未确认。
--   租户 123 的 region（管廊包在用）不动。字段库里 region_id / region_ids 字段定义不动。
-- 只能执行一次。重复执行不会再改到数据（条件里都带了 deleted = false 或目标值判断）。

SET search_path TO dynamicbusiness;

BEGIN;

-- a. 场站 → 作业区（作业区按组织编码定位，编码由第 2 步复制时从 region 编码改前缀得到）
WITH station_zone(station_id, org_code) AS (VALUES
    (1,  'ORG-OP-SD-DY'),                               -- 东营油库 → 东营作业区（旧列）
    (44, 'ORG-OP-EAST-JQ'),                             -- 金桥厂区 → 金桥作业区（旧值）
    (45, 'ORG-OP-EAST-LY'),                             -- 洛阳圣瑞 → 东部储运 洛阳作业区（旧值）
    -- 厦门作业区：旧值 2；厦门集美分输阀室按地名推断
    (8,  'org-e48d41272ee14524a811c0652a64697b'), (9,  'org-e48d41272ee14524a811c0652a64697b'),
    (35, 'org-e48d41272ee14524a811c0652a64697b'), (36, 'org-e48d41272ee14524a811c0652a64697b'),
    (37, 'org-e48d41272ee14524a811c0652a64697b'), (49, 'org-e48d41272ee14524a811c0652a64697b'),
    -- 泉州作业区：旧值 3；水头分输站按地名推断
    (5,  'org-4fce5d159b6b4556aaa2a7f4dd08e02a'), (6,  'org-4fce5d159b6b4556aaa2a7f4dd08e02a'),
    (7,  'org-4fce5d159b6b4556aaa2a7f4dd08e02a'), (13, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'),
    (14, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'), (22, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'),
    (31, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'), (32, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'),
    (33, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'), (34, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'),
    (47, 'org-4fce5d159b6b4556aaa2a7f4dd08e02a'),
    -- 漳州作业区：旧值 4；漳州首站、漳州LNG接收站按地名推断（LNG 站可能属漳浦作业区，组织机构里还没有）
    (10, 'org-ae0512198362407f9d61909c6f911018'), (15, 'org-ae0512198362407f9d61909c6f911018'),
    (16, 'org-ae0512198362407f9d61909c6f911018'), (17, 'org-ae0512198362407f9d61909c6f911018'),
    (18, 'org-ae0512198362407f9d61909c6f911018'), (19, 'org-ae0512198362407f9d61909c6f911018'),
    (20, 'org-ae0512198362407f9d61909c6f911018'), (38, 'org-ae0512198362407f9d61909c6f911018'),
    (39, 'org-ae0512198362407f9d61909c6f911018'), (46, 'org-ae0512198362407f9d61909c6f911018'),
    (48, 'org-ae0512198362407f9d61909c6f911018'),
    -- 福州作业区：旧值 6
    (3,  'org-a3b589bc27354c1fa14d392e2197bf86'), (4,  'org-a3b589bc27354c1fa14d392e2197bf86'),
    (23, 'org-a3b589bc27354c1fa14d392e2197bf86'), (24, 'org-a3b589bc27354c1fa14d392e2197bf86'),
    (25, 'org-a3b589bc27354c1fa14d392e2197bf86'), (26, 'org-a3b589bc27354c1fa14d392e2197bf86'),
    (27, 'org-a3b589bc27354c1fa14d392e2197bf86'), (28, 'org-a3b589bc27354c1fa14d392e2197bf86'),
    (29, 'org-a3b589bc27354c1fa14d392e2197bf86'), (30, 'org-a3b589bc27354c1fa14d392e2197bf86'),
    -- 龙岩作业区：旧值 7
    (11, 'org-7c8b5412ab7444efb6214179a62172cb'), (12, 'org-7c8b5412ab7444efb6214179a62172cb'),
    (21, 'org-7c8b5412ab7444efb6214179a62172cb')
)
UPDATE ent_facility_t1 f
SET organization_id = o.id, updater = 'org-split', update_time = now()
FROM station_zone s
JOIN ent_organization_t1 o ON o.code = s.org_code AND o.deleted = false AND o.tenant_id = 1
WHERE f.id = s.station_id AND f.deleted = false AND f.organization_id IS DISTINCT FROM o.id;

DO $$
DECLARE missing int;
BEGIN
    SELECT count(*) INTO missing FROM ent_facility_t1 WHERE deleted = false AND tenant_id = 1 AND organization_id IS NULL;
    IF missing > 0 THEN
        RAISE EXCEPTION '仍有 % 座场站没有所属组织，映射表不完整', missing;
    END IF;
END $$;

-- b. 扩展字段袋里的旧键（字段已从类型和型号上拿掉）
UPDATE ent_facility_t1 SET custom_fields = custom_fields - 'region_id'
WHERE deleted = false AND custom_fields ? 'region_id';
UPDATE ent_equipment_t1 SET custom_fields = custom_fields - 'region_ids' - 'region_id'
WHERE deleted = false AND (custom_fields ? 'region_ids' OR custom_fields ? 'region_id');
UPDATE ent_department_t1 SET custom_fields = custom_fields - 'company_id'
WHERE deleted = false AND custom_fields ? 'company_id';

-- c. 租户 1 的 region 目录其余数据（注册项已由删除接口逻辑删）
UPDATE ent_region_t1 SET deleted = true, updater = 'org-split', update_time = now()
WHERE tenant_id = 1 AND deleted = false;
UPDATE dynamic_model_field_assignment a SET deleted = true
FROM dynamic_model m
WHERE a.model_id = m.id AND m.tenant_id = 1 AND m.entity_type_code = 'region' AND a.deleted = false;
UPDATE dynamic_model SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dynamic_entity_type_base_field SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dynamic_category_entity_link_t1 SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dynamic_category SET deleted = true WHERE tenant_id = 1 AND category_type_code = 'region' AND deleted = false;
UPDATE dynamic_category_type SET deleted = true WHERE tenant_id = 1 AND category_type_code = 'region' AND deleted = false;
UPDATE dynamic_catalog_role_grant SET deleted = true WHERE tenant_id = 1 AND registry_code = 'region' AND deleted = false;
UPDATE dm_data_tab_layout SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dm_data_tab_column_relation SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dm_model_tab_category SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dm_catalog_orchestration SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dynamic_business_entry SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE dynamic_entity_type_config SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE business_capability SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE capability_component_projection SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;
UPDATE model_crud_form_definition SET deleted = true WHERE tenant_id = 1 AND entity_type_code = 'region' AND deleted = false;

COMMIT;
