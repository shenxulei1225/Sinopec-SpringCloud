-- V66: 工作台布局模版/实例 + 页面引用；去掉 layout_scene
-- 口径：模版只管版式、不绑 entityTypeCode；实例新 id；页面/数据页签只引用实例 layoutId

SET search_path TO dynamicbusiness, public;

-- ---------------------------------------------------------------------------
-- 1) 布局头：模版或实例
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dynamicbusiness.dm_workbench_layout (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(128) NOT NULL,
    is_template         BOOLEAN      NOT NULL DEFAULT FALSE,
    source_template_id  BIGINT,
    creator             VARCHAR(64),
    create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64),
    update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.dm_workbench_layout IS
    '工作台布局头：is_template=true 为跨类型版式模版；false 为从模版生成的实例';
COMMENT ON COLUMN dynamicbusiness.dm_workbench_layout.is_template IS
    '是否模版；模版库只筛 true；页面只引用实例（false）';
COMMENT ON COLUMN dynamicbusiness.dm_workbench_layout.source_template_id IS
    '实例来源模版 id；模版行为空';

CREATE INDEX IF NOT EXISTS idx_dm_workbench_layout_template
    ON dynamicbusiness.dm_workbench_layout (tenant_id, is_template)
    WHERE deleted = FALSE;

-- ---------------------------------------------------------------------------
-- 2) 整页 → 布局引用（整体页面布局）；嵌入块另用 dataLayoutId 指向工作台实例
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dynamicbusiness.dm_page_layout_ref (
    id                  BIGSERIAL PRIMARY KEY,
    page_key            VARCHAR(128) NOT NULL,
    layout_id           BIGINT       NOT NULL,
    data_layout_id      BIGINT,
    creator             VARCHAR(64),
    create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64),
    update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.dm_page_layout_ref IS
    '页面→布局：layout_id=整页布局；data_layout_id=页内嵌入数据工作台所用实例（可空）';
COMMENT ON COLUMN dynamicbusiness.dm_page_layout_ref.page_key IS
    '页面身份键（如 view:{viewConfigId}、catalog:{code}:workbench）';
COMMENT ON COLUMN dynamicbusiness.dm_page_layout_ref.data_layout_id IS
    '嵌入数据工作台的布局实例 id；非嵌入页可空';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_page_layout_ref_page
    ON dynamicbusiness.dm_page_layout_ref (tenant_id, page_key)
    WHERE deleted = FALSE;

-- ---------------------------------------------------------------------------
-- 3) 栏行挂 layout_id；类型挂 data_layout_id
-- ---------------------------------------------------------------------------
ALTER TABLE dynamicbusiness.dm_data_tab_layout
    ADD COLUMN IF NOT EXISTS layout_id BIGINT;

ALTER TABLE dynamicbusiness.dynamic_entity_type
    ADD COLUMN IF NOT EXISTS data_layout_id BIGINT;

COMMENT ON COLUMN dynamicbusiness.dm_data_tab_layout.layout_id IS
    '所属工作台布局（模版或实例）';
COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.data_layout_id IS
    '该目录「数据」页签引用的工作台布局实例 id';

-- ---------------------------------------------------------------------------
-- 4) 种子：租户 1 通用台账模版（不绑类型编码）
-- ---------------------------------------------------------------------------
INSERT INTO dynamicbusiness.dm_workbench_layout (
    name, is_template, source_template_id, creator, deleted, tenant_id
)
SELECT '通用台账（分类|型号|实体）', TRUE, NULL, 'flyway-v66', FALSE, 1
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dm_workbench_layout
    WHERE tenant_id = 1 AND deleted = FALSE AND is_template = TRUE
      AND name = '通用台账（分类|型号|实体）'
);

DO $$
DECLARE
  v_tpl BIGINT;
BEGIN
  SELECT id INTO v_tpl FROM dynamicbusiness.dm_workbench_layout
  WHERE tenant_id = 1 AND deleted = FALSE AND is_template = TRUE
    AND name = '通用台账（分类|型号|实体）'
  ORDER BY id LIMIT 1;

  IF v_tpl IS NULL THEN
    RETURN;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dm_data_tab_layout
    WHERE layout_id = v_tpl AND deleted = FALSE
  ) THEN
    INSERT INTO dynamicbusiness.dm_data_tab_layout (
      tenant_id, entity_type_code, layout_scene, column_kind, tab_id,
      props_id, enabled, column_meta, creator, deleted, layout_id
    ) VALUES
      (1, '', 'DATA_TAB', 'CATEGORY', 'default',
       NULL, TRUE,
       '{"label":"分类","columnKey":"default","columnSection":"FILTER","widthPx":240,"sectionWidthPx":240}'::jsonb,
       'flyway-v66', FALSE, v_tpl),
      (1, '', 'DATA_TAB', 'MODEL', NULL,
       NULL, TRUE,
       '{"columnSection":"OBJECT","widthPx":192,"sectionWidthPx":400}'::jsonb,
       'flyway-v66', FALSE, v_tpl),
      (1, '', 'DATA_TAB', 'ENTITY', NULL,
       NULL, TRUE,
       '{"columnSection":"OBJECT","widthPx":240}'::jsonb,
       'flyway-v66', FALSE, v_tpl),
      (1, '', 'DATA_TAB', 'DETAIL', NULL,
       NULL, FALSE, NULL, 'flyway-v66', FALSE, v_tpl);
  END IF;
END $$;

-- ---------------------------------------------------------------------------
-- 5) 现网 DATA_TAB：每类型一份实例，回填 data_layout_id
-- ---------------------------------------------------------------------------
DO $$
DECLARE
  r RECORD;
  v_tpl BIGINT;
  v_layout BIGINT;
  v_name TEXT;
BEGIN
  SELECT id INTO v_tpl FROM dynamicbusiness.dm_workbench_layout
  WHERE tenant_id = 1 AND deleted = FALSE AND is_template = TRUE
    AND name = '通用台账（分类|型号|实体）'
  ORDER BY id LIMIT 1;

  FOR r IN
    SELECT DISTINCT tenant_id, entity_type_code
    FROM dynamicbusiness.dm_data_tab_layout
    WHERE deleted = FALSE
      AND layout_scene = 'DATA_TAB'
      AND entity_type_code IS NOT NULL
      AND btrim(entity_type_code) <> ''
      AND layout_id IS NULL
  LOOP
    v_name := '数据页签·' || r.entity_type_code;
    INSERT INTO dynamicbusiness.dm_workbench_layout (
      name, is_template, source_template_id, creator, deleted, tenant_id
    ) VALUES (
      v_name, FALSE, v_tpl, 'flyway-v66', FALSE, r.tenant_id
    ) RETURNING id INTO v_layout;

    UPDATE dynamicbusiness.dm_data_tab_layout
    SET layout_id = v_layout,
        updater = 'flyway-v66',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = r.tenant_id
      AND entity_type_code = r.entity_type_code
      AND layout_scene = 'DATA_TAB'
      AND deleted = FALSE
      AND layout_id IS NULL;

    UPDATE dynamicbusiness.dynamic_entity_type
    SET data_layout_id = v_layout,
        updater = 'flyway-v66',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = r.tenant_id
      AND code = r.entity_type_code
      AND deleted = FALSE
      AND data_layout_id IS NULL;
  END LOOP;
END $$;

-- ---------------------------------------------------------------------------
-- 6) 现网 INSPECTION_PICKER：收成实例 + 页面引用（过渡 page_key）
-- ---------------------------------------------------------------------------
DO $$
DECLARE
  r RECORD;
  v_tpl BIGINT;
  v_layout BIGINT;
  v_page TEXT;
BEGIN
  SELECT id INTO v_tpl FROM dynamicbusiness.dm_workbench_layout
  WHERE tenant_id = 1 AND deleted = FALSE AND is_template = TRUE
    AND name = '通用台账（分类|型号|实体）'
  ORDER BY id LIMIT 1;

  FOR r IN
    SELECT DISTINCT tenant_id, entity_type_code
    FROM dynamicbusiness.dm_data_tab_layout
    WHERE deleted = FALSE
      AND layout_scene = 'INSPECTION_PICKER'
      AND entity_type_code IS NOT NULL
      AND btrim(entity_type_code) <> ''
      AND layout_id IS NULL
  LOOP
    INSERT INTO dynamicbusiness.dm_workbench_layout (
      name, is_template, source_template_id, creator, deleted, tenant_id
    ) VALUES (
      '嵌入勾选·' || r.entity_type_code, FALSE, v_tpl, 'flyway-v66', FALSE, r.tenant_id
    ) RETURNING id INTO v_layout;

    UPDATE dynamicbusiness.dm_data_tab_layout
    SET layout_id = v_layout,
        updater = 'flyway-v66',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = r.tenant_id
      AND entity_type_code = r.entity_type_code
      AND layout_scene = 'INSPECTION_PICKER'
      AND deleted = FALSE
      AND layout_id IS NULL;

    v_page := 'catalog:' || r.entity_type_code || ':embed-pick';
    INSERT INTO dynamicbusiness.dm_page_layout_ref (
      page_key, layout_id, data_layout_id, creator, deleted, tenant_id
    )
    SELECT v_page, v_layout, v_layout, 'flyway-v66', FALSE, r.tenant_id
    WHERE NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dm_page_layout_ref
      WHERE tenant_id = r.tenant_id AND page_key = v_page AND deleted = FALSE
    );
  END LOOP;
END $$;

-- 残留未挂 layout_id 的行（含已软删）：挂到同租户模版，避免 NOT NULL 失败
UPDATE dynamicbusiness.dm_data_tab_layout t
SET layout_id = COALESCE(
      (
        SELECT id FROM dynamicbusiness.dm_workbench_layout w
        WHERE w.tenant_id = t.tenant_id AND w.deleted = FALSE AND w.is_template = TRUE
        ORDER BY w.id LIMIT 1
      ),
      (
        SELECT id FROM dynamicbusiness.dm_workbench_layout w
        WHERE w.deleted = FALSE AND w.is_template = TRUE
        ORDER BY w.id LIMIT 1
      )
    ),
    updater = 'flyway-v66',
    update_time = CURRENT_TIMESTAMP
WHERE t.layout_id IS NULL;

ALTER TABLE dynamicbusiness.dm_data_tab_layout
    ALTER COLUMN layout_id SET NOT NULL;

ALTER TABLE dynamicbusiness.dm_data_tab_layout
    ALTER COLUMN entity_type_code DROP NOT NULL;

-- ---------------------------------------------------------------------------
-- 7) 唯一键改按 layout_id；去掉 layout_scene
-- ---------------------------------------------------------------------------
DROP INDEX IF EXISTS dynamicbusiness.uk_dm_data_tab_layout_scope;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_data_tab_layout_layout_scope
    ON dynamicbusiness.dm_data_tab_layout (
        tenant_id, layout_id, column_kind, COALESCE(tab_id, '')
    )
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dm_data_tab_layout_layout
    ON dynamicbusiness.dm_data_tab_layout (tenant_id, layout_id)
    WHERE deleted = FALSE;

ALTER TABLE dynamicbusiness.dm_data_tab_layout
    DROP COLUMN IF EXISTS layout_scene;

-- ---------------------------------------------------------------------------
-- 8) 栏间关系：layout_scene → layout_id
-- ---------------------------------------------------------------------------
ALTER TABLE dynamicbusiness.dm_data_tab_column_relation
    ADD COLUMN IF NOT EXISTS layout_id BIGINT;

UPDATE dynamicbusiness.dm_data_tab_column_relation r
SET layout_id = e.data_layout_id
FROM dynamicbusiness.dynamic_entity_type e
WHERE r.layout_id IS NULL
  AND e.tenant_id = r.tenant_id
  AND e.code = r.entity_type_code
  AND e.deleted = FALSE
  AND e.data_layout_id IS NOT NULL;

DROP INDEX IF EXISTS dynamicbusiness.uk_dm_data_tab_column_relation_edge;
DROP INDEX IF EXISTS dynamicbusiness.idx_dm_data_tab_column_relation_scope;

-- 无 layout_id 的旧关系行先软删，避免 NOT NULL
UPDATE dynamicbusiness.dm_data_tab_column_relation
SET deleted = TRUE,
    updater = 'flyway-v66',
    update_time = CURRENT_TIMESTAMP
WHERE layout_id IS NULL AND deleted = FALSE;

ALTER TABLE dynamicbusiness.dm_data_tab_column_relation
    ALTER COLUMN layout_id SET NOT NULL;

ALTER TABLE dynamicbusiness.dm_data_tab_column_relation
    DROP COLUMN IF EXISTS layout_scene;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_data_tab_column_relation_edge
    ON dynamicbusiness.dm_data_tab_column_relation (
        tenant_id, layout_id, edge_id
    )
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dm_data_tab_column_relation_layout
    ON dynamicbusiness.dm_data_tab_column_relation (
        tenant_id, layout_id
    )
    WHERE deleted = FALSE;

COMMENT ON TABLE dynamicbusiness.dm_data_tab_column_relation IS
    '栏间关系声明：按工作台布局实例（layout_id）配置';
