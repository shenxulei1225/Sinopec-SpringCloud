-- 分类关联身份定稿：
-- 1) entity_type_code 固定为实际存储类型（注册编码 task_patrol 归一为 task）
-- 2) 新增 domain 列，作为实体业务域的同步镜像（查询维度，不进入唯一键）
-- 3) 归一存量后去重，并按「租户 + 存储类型 + 实体 + 分类」建唯一约束

SET search_path TO dynamicbusiness;

-- 1. 新增 domain 列
ALTER TABLE dynamic_entity_category_relation
	ADD COLUMN IF NOT EXISTS domain character varying(128);

-- 通用存储表补 domain：V21 只给 ent_* 加了列，但 EntityDO 统一映射 domain。
ALTER TABLE dynamic_entity
	ADD COLUMN IF NOT EXISTS domain character varying(128);

COMMENT ON COLUMN dynamic_entity.domain IS
	'业务域（Domain）；创建或更换型号时从型号抄写；无业务域时为空';
COMMENT ON COLUMN dynamic_entity_category_relation.entity_type_code IS
	'实际存储类型编码（如 task）；注册编码（如 task_patrol）不得写入本列';
COMMENT ON COLUMN dynamic_entity_category_relation.domain IS
	'业务域（Domain）；实体行 domain 的同步镜像，随实体迁移更新，不参与唯一键';

-- 2. 注册编码归一为实际存储类型
-- DOMAIN / SCOPE / CATEGORY 三类入口复用基础类型存储；含已软删的入口，
-- 因为历史关联仍按这些注册编码写入（如已删除的 patrol_target_equipment）。
UPDATE dynamic_entity_category_relation r
SET entity_type_code = btrim(t.base_entity_type_code)
FROM dynamic_entity_type t
WHERE r.entity_type_code = t.code
	AND t.entry_kind IN ('DOMAIN', 'SCOPE', 'CATEGORY')
	AND t.base_entity_type_code IS NOT NULL
	AND btrim(t.base_entity_type_code) <> ''
	AND r.entity_type_code <> btrim(t.base_entity_type_code);

-- 3. 同一「租户 + 存储类型 + 实体 + 分类」只保留一行
-- 有效关联优先于软删除的排除标记；同状态取最新一行。
-- 软删除行是「显式排除」标记，不能与有效行并存，否则恢复软删除会产生两行有效关联。
DELETE FROM dynamic_entity_category_relation r
USING (
	SELECT id,
		row_number() OVER (
			PARTITION BY tenant_id, entity_type_code, entity_id, category_id
			ORDER BY deleted ASC, id DESC
		) AS rn
	FROM dynamic_entity_category_relation
) ranked
WHERE r.id = ranked.id
	AND ranked.rn > 1;

-- 4. 回填 domain，来源是实体行
DO $$
DECLARE
	rel_code text;
	physical_table text;
BEGIN
	FOR rel_code IN
		SELECT DISTINCT btrim(entity_type_code)
		FROM dynamic_entity_category_relation
		WHERE entity_type_code IS NOT NULL
			AND btrim(entity_type_code) <> ''
	LOOP
		SELECT CASE
				WHEN t.storage_type IS NULL OR upper(t.storage_type) <> 'DEDICATED' THEN 'dynamic_entity'
				WHEN t.dedicated_table_name IS NOT NULL AND btrim(t.dedicated_table_name) <> ''
					THEN btrim(t.dedicated_table_name)
				ELSE 'ent_' || t.code
			END
		INTO physical_table
		FROM dynamic_entity_type t
		WHERE t.code = rel_code
		ORDER BY t.deleted ASC
		LIMIT 1;

		IF physical_table IS NULL THEN
			RAISE NOTICE '[V24] 关联表存在未登记的存储类型 %，跳过 domain 回填', rel_code;
			CONTINUE;
		END IF;

		IF to_regclass('dynamicbusiness.' || quote_ident(physical_table)) IS NULL THEN
			RAISE NOTICE '[V24] 存储类型 % 的物理表 % 不存在，跳过 domain 回填', rel_code, physical_table;
			CONTINUE;
		END IF;

		EXECUTE format(
			'UPDATE dynamicbusiness.dynamic_entity_category_relation r '
			|| 'SET domain = e.domain '
			|| 'FROM dynamicbusiness.%I e '
			|| 'WHERE r.entity_id = e.id AND btrim(r.entity_type_code) = %L '
			|| 'AND r.domain IS DISTINCT FROM e.domain',
			physical_table,
			rel_code
		);
	END LOOP;
END $$;

-- 5. 唯一约束与查询索引
-- 唯一身份不含 domain：实体换业务域时仍是同一条关联，只刷新 domain。
CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_entity_category_relation_identity
	ON dynamic_entity_category_relation (tenant_id, entity_type_code, entity_id, category_id);

-- 点选分类的主查询路径：分类范围 + 存储类型 + 业务域。
CREATE INDEX IF NOT EXISTS idx_dynamic_entity_category_relation_category_scope
	ON dynamic_entity_category_relation (category_id, entity_type_code, domain)
	WHERE deleted = false;
