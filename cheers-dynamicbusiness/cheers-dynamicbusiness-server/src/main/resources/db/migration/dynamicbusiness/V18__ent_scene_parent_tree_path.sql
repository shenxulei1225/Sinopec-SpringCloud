-- V18: ent_scene / ent_scene_placement 补齐通用实体列 parent_id、tree_path
-- 根因：EntityDO / 动态实体查询固定 SELECT parent_id, tree_path；V17 建表遗漏导致 query-by-scene 500

ALTER TABLE dynamicbusiness.ent_scene
    ADD COLUMN IF NOT EXISTS parent_id bigint DEFAULT 0,
    ADD COLUMN IF NOT EXISTS tree_path character varying(500);

ALTER TABLE dynamicbusiness.ent_scene_placement
    ADD COLUMN IF NOT EXISTS parent_id bigint DEFAULT 0,
    ADD COLUMN IF NOT EXISTS tree_path character varying(500);

COMMENT ON COLUMN dynamicbusiness.ent_scene.parent_id IS '父实体ID（通用实体树；场景通常为 0）';
COMMENT ON COLUMN dynamicbusiness.ent_scene.tree_path IS '树路径（通用实体）';
COMMENT ON COLUMN dynamicbusiness.ent_scene_placement.parent_id IS '父实体ID（通用实体树）';
COMMENT ON COLUMN dynamicbusiness.ent_scene_placement.tree_path IS '树路径（通用实体）';
