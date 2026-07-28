-- 构筑物（structure）专用表：归属设施（facility_id），可选挂站内分区（zone_id）

SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS ent_structure_id_seq;

CREATE TABLE IF NOT EXISTS ent_structure (
  id               BIGINT PRIMARY KEY DEFAULT nextval('ent_structure_id_seq'::regclass),
  tenant_id        BIGINT NOT NULL DEFAULT 0,
  entity_type_code VARCHAR(64) DEFAULT 'structure',
  model_id         BIGINT NOT NULL,
  name             VARCHAR(255) NOT NULL,
  code             VARCHAR(100) NOT NULL,
  status           INTEGER DEFAULT 1,
  parent_id        BIGINT DEFAULT 0,
  attrs            JSONB DEFAULT '{}'::jsonb,
  custom_fields    JSONB DEFAULT '{}'::jsonb,
  creator          VARCHAR(64),
  create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater          VARCHAR(64),
  update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted          BOOLEAN DEFAULT FALSE,
  tree_path        VARCHAR(500),
  sort             INTEGER DEFAULT 0,
  facility_id      BIGINT NOT NULL,
  zone_id          BIGINT,
  structure_type   VARCHAR(100),
  description      VARCHAR(500),
  boundary_geojson JSONB,
  boundary_crs     VARCHAR(32) DEFAULT 'EPSG:4326',
  boundary_status  VARCHAR(32) DEFAULT 'none',
  min_height_m     NUMERIC(10, 3),
  max_height_m     NUMERIC(10, 3),
  centroid_lng     NUMERIC(12, 8),
  centroid_lat     NUMERIC(12, 8),
  domain           VARCHAR(128)
);

CREATE INDEX IF NOT EXISTS idx_ent_structure_tenant
  ON ent_structure (tenant_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_ent_structure_facility
  ON ent_structure (facility_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_ent_structure_zone
  ON ent_structure (zone_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_ent_structure_model
  ON ent_structure (model_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_ent_structure_parent
  ON ent_structure (parent_id) WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_ent_structure_domain
  ON ent_structure (domain) WHERE deleted = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_structure_code_tenant
  ON ent_structure (code, tenant_id) WHERE deleted = FALSE AND code IS NOT NULL;
