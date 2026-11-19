-- 组织机构拆分（租户 1，一次性）· 第 1 步：字段库加三个字段。
--
-- 背景：公司组织架构以前借用「运营区域」（region）目录，管理区域和组织混在一起。
-- 现在单独建「组织机构」（organization）目录：集团 → 省公司/分公司 → 作业区。
-- 部门、场站都改为指向组织机构。租户 1 不再使用 region。
--
-- 执行顺序（只执行一次，不挂启动、查询或开通）：
--   0. 平台接口建「组织机构」目录（分类种类先设为高级，建目录时一次挂上「上级」字段）、四个型号、角色勾选。
--   1. 本脚本：字段库加 驻地、职能定位、所属组织。
--   2. 平台接口把字段挂到组织机构、场站、部门上（建列、建索引由接口完成）。
--   3. organization_split_2026-11-17_03_data.sql：复制记录、改引用、删租户 1 的 region。
--
-- 不负责：建列（由基础字段接口建）；部门迁到作业区（等真实部门设置）。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (code, name, type, description, source, status, provider_code,
                           governance_status, creator, updater, tenant_id)
SELECT v.code, v.name, v.type, v.description, 'BASE', 1, v.provider_code, 'COMPANY',
       'org-split', 'org-split', 1
FROM (VALUES
    ('hq_location', '驻地', 'TEXT', '这个组织机构的办公所在地。', NULL),
    ('mission_summary', '职能定位', 'TEXT', '这个组织机构负责什么。', NULL),
    ('organization_id', '所属组织', 'ENTITY_REF',
     '指向组织机构里的一个节点。场站上填管辖它的作业区，部门上填它所属的组织。',
     'dynamic-entity:organization')
) AS v(code, name, type, description, provider_code)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_field f
    WHERE f.tenant_id = 1 AND f.deleted = false AND f.code = v.code
);
