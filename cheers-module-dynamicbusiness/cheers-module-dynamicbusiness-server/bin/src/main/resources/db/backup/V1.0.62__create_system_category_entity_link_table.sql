-- =====================================================
-- V1.0.62: 创建分类与实体链接表 system_category_entity_link
-- 描述：
-- 1) 支撑“分类即实体”的能力,为分类与实体建立一对一/一对多链接关系；
-- 2) 与 CategoryEntityLinkDO / CategoryEntityLinkMapper 对应；
-- 3) 使用逻辑删除 + tenant_id,多租户兼容。
-- =====================================================

-- 分类与实体链接表
CREATE TABLE IF NOT EXISTS system_category_entity_link (
    id              BIGSERIAL PRIMARY KEY,
    category_id     BIGINT      NOT NULL,      -- 分类ID
    entity_id       BIGINT      NOT NULL,      -- 关联的实体ID
    entity_model_id BIGINT,                    -- 关联的实体模型ID（可空）
    creator         VARCHAR(64) NOT NULL,
    create_time     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64),
    update_time     TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN              DEFAULT FALSE,
    tenant_id       BIGINT               DEFAULT 0
);

-- 索引：按分类、实体、租户维度查询
CREATE INDEX IF NOT EXISTS idx_category_entity_link_category_id
    ON system_category_entity_link(category_id);

CREATE INDEX IF NOT EXISTS idx_category_entity_link_entity_id
    ON system_category_entity_link(entity_id);

CREATE INDEX IF NOT EXISTS idx_category_entity_link_tenant_id
    ON system_category_entity_link(tenant_id);

-- 唯一约束：同一租户下,一个分类只关联一个实体
CREATE UNIQUE INDEX IF NOT EXISTS uk_category_entity_link
    ON system_category_entity_link(category_id, tenant_id)
    WHERE deleted = FALSE;

-- 表及字段注释
COMMENT ON TABLE system_category_entity_link IS '分类与实体链接表（分类即实体场景）';
COMMENT ON COLUMN system_category_entity_link.id IS '主键ID';
COMMENT ON COLUMN system_category_entity_link.category_id IS '分类ID';
COMMENT ON COLUMN system_category_entity_link.entity_id IS '关联的实体ID';
COMMENT ON COLUMN system_category_entity_link.entity_model_id IS '关联的实体模型ID';
COMMENT ON COLUMN system_category_entity_link.tenant_id IS '租户ID';

