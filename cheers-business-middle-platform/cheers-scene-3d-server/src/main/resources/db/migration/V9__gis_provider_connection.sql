-- 地图服务连接：GIS 管理页里每个客户各配一份的天地图、Cesium、GeoServer、超图、ArcGIS 连接。
-- 读写由 cheers-gis-server 负责；scene_platform 这套表的迁移统一放在本服务（见 gis-server application-local.yaml 过渡说明）。
-- 每个客户每种地图服务最多一行。清空某种服务就逻辑删除这一行。

CREATE TABLE IF NOT EXISTS scene_platform.gis_provider_connection (
    id                 BIGINT        NOT NULL PRIMARY KEY,
    provider_code      VARCHAR(32)   NOT NULL,
    token              VARCHAR(1024),
    url_template       VARCHAR(1024),
    token_query_param  VARCHAR(64),
    label              VARCHAR(64),
    imagery_asset_id   INTEGER,
    tenant_id          BIGINT        NOT NULL DEFAULT 0,
    creator            VARCHAR(64),
    create_time        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater            VARCHAR(64),
    update_time        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted            BOOLEAN       NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE scene_platform.gis_provider_connection IS '地图服务连接（按客户），GIS 管理页保存';
COMMENT ON COLUMN scene_platform.gis_provider_connection.provider_code IS '地图服务：tianditu / cesium-ion / geoserver / supermap / arcgis';
COMMENT ON COLUMN scene_platform.gis_provider_connection.token IS '访问令牌。天地图、Cesium 必填；本地瓦片服务可选';
COMMENT ON COLUMN scene_platform.gis_provider_connection.url_template IS '瓦片地址模板，含 {z}/{x}/{y}。只用于 GeoServer、超图、ArcGIS';
COMMENT ON COLUMN scene_platform.gis_provider_connection.token_query_param IS '本地瓦片服务的令牌参数名';
COMMENT ON COLUMN scene_platform.gis_provider_connection.label IS '界面显示名';
COMMENT ON COLUMN scene_platform.gis_provider_connection.imagery_asset_id IS 'Cesium Ion 影像资源编号。只用于 cesium-ion';

CREATE UNIQUE INDEX IF NOT EXISTS uk_gis_provider_connection_tenant_provider
    ON scene_platform.gis_provider_connection (tenant_id, provider_code)
    WHERE deleted = FALSE;
