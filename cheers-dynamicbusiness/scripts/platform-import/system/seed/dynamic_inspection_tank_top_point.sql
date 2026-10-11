-- 每台原油储罐的到达位置，写成点位台账里名称对上的「顶」点。
-- 北罐组 1#、2# 对西罐 1、2 顶；6#、7#、8# 对东罐 6、7、8 顶。
-- 南罐组 3#、4#、5# 对西罐 3、4、5 顶；9#、10#、11# 对东罐 9、10、11 顶。
-- 写在已有检查步骤的到达那一步：人工、机器人、无人机。没有检查步骤的检查项不新编步骤。
-- 飞向目标点编号跟着这个点位编号写。原先记在别的步骤上的位置（例如目视检查上的罐底）从那一步拿掉。
-- 不改拍摄角度、悬停时间、最大速度。不改固定摄像机选的摄像机。

SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE tank_top (
  equipment_id bigint PRIMARY KEY,
  point_id bigint NOT NULL,
  point_name text NOT NULL
);

INSERT INTO tank_top (equipment_id, point_id, point_name) VALUES
  (900104, 70, '北罐组西罐1顶'),
  (900105, 71, '北罐组西罐2顶'),
  (900107, 75, '北罐组东罐6顶'),
  (900108, 76, '北罐组东罐7顶'),
  (900109, 77, '北罐组东罐8顶'),
  (900110, 72, '南罐组西罐3顶'),
  (900111, 73, '南罐组西罐4顶'),
  (900112, 74, '南罐组西罐5顶'),
  (900113, 78, '南罐组东罐9顶'),
  (900114, 79, '南罐组东罐10顶'),
  (900115, 80, '南罐组东罐11顶');

DO $$
DECLARE
  bad text;
BEGIN
  SELECT string_agg(t.point_name || '→' || COALESCE(p.name, '缺失'), '，')
    INTO bad
  FROM tank_top t
  LEFT JOIN ent_point_t1 p
    ON p.id = t.point_id AND p.deleted = false AND p.tenant_id = 1 AND p.facility_id = 44
  WHERE p.name IS DISTINCT FROM t.point_name;
  IF bad IS NOT NULL THEN
    RAISE EXCEPTION '罐顶点位对不上：%', bad;
  END IF;
END $$;

CREATE OR REPLACE FUNCTION pg_temp.set_arrival_point(
  pack jsonb,
  item_id bigint,
  means text,
  node_key text,
  point_id bigint
) RETURNS jsonb
LANGUAGE plpgsql AS $$
DECLARE
  entries jsonb;
  found int := NULL;
  i int;
  entry jsonb;
  nodes jsonb;
  new_nodes jsonb := '{}'::jsonb;
  node_name text;
  node jsonb;
  loc_key constant text := 'F-e49f76bdca3e4e1393e8e2f1cc0e7867';
  point_node jsonb;
BEGIN
  IF pack IS NULL OR jsonb_typeof(pack) <> 'object' THEN
    pack := '{"version":1,"entries":[]}'::jsonb;
  END IF;
  IF pack->'version' IS NULL THEN
    pack := pack || '{"version":1}'::jsonb;
  END IF;
  entries := COALESCE(pack->'entries', '[]'::jsonb);
  IF jsonb_typeof(entries) <> 'array' THEN
    entries := '[]'::jsonb;
  END IF;

  point_node := jsonb_build_object(
    loc_key, to_jsonb(point_id),
    'fly_to_id', to_jsonb(point_id::text)
  );

  FOR i IN 0 .. jsonb_array_length(entries) - 1 LOOP
    entry := entries->i;
    IF COALESCE(entry->>'subjectId', '') <> ''
       AND (entry->>'subjectId')::bigint = item_id
       AND upper(COALESCE(entry->>'dimensionValue', '')) = upper(means)
       AND COALESCE(entry->>'dimensionKey', 'execution_means') = 'execution_means' THEN
      found := i;
      EXIT;
    END IF;
  END LOOP;

  IF found IS NULL THEN
    entry := jsonb_build_object(
      'enabled', true,
      'subjectType', 'inspection_item',
      'subjectId', item_id,
      'targetType', 'inspection_item',
      'targetId', item_id,
      'dimensionKey', 'execution_means',
      'dimensionValue', means,
      'paramsByNode', jsonb_build_object(node_key, point_node)
    );
    entries := entries || jsonb_build_array(entry);
  ELSE
    nodes := entry->'paramsByNode';
    IF jsonb_typeof(nodes) <> 'object' THEN
      nodes := '{}'::jsonb;
    END IF;
    FOR node_name, node IN SELECT key, value FROM jsonb_each(nodes) LOOP
      IF node_name = node_key THEN
        IF jsonb_typeof(node) <> 'object' THEN
          node := '{}'::jsonb;
        END IF;
        node := node || point_node;
      ELSIF jsonb_typeof(node) = 'object' AND (node ? loc_key OR node ? 'fly_to_id') THEN
        node := (node - loc_key) - 'fly_to_id';
      END IF;
      new_nodes := new_nodes || jsonb_build_object(node_name, node);
    END LOOP;
    IF NOT (new_nodes ? node_key) THEN
      new_nodes := new_nodes || jsonb_build_object(node_key, point_node);
    END IF;
    entry := jsonb_set(entry, '{paramsByNode}', new_nodes);
    entries := jsonb_set(entries, ARRAY[found::text], entry);
  END IF;

  RETURN jsonb_set(pack, '{entries}', entries);
END $$;

DO $$
DECLARE
  tank record;
  arrival record;
  pack jsonb;
BEGIN
  FOR tank IN
    SELECT t.equipment_id, t.point_id, e.custom_fields
    FROM tank_top t
    JOIN ent_equipment_t1 e ON e.id = t.equipment_id AND e.deleted = false AND e.facility_id = 44
  LOOP
    pack := tank.custom_fields->'host_sop_param_pack';
    FOR arrival IN
      SELECT item.id AS item_id,
             upper(method.value->>'methodKey') AS means,
             step.value->>'nodeKey' AS node_key
      FROM ent_inspection_item_t1 item
      CROSS JOIN LATERAL jsonb_array_elements(item.step_tree_json->'methods') method(value)
      CROSS JOIN LATERAL jsonb_array_elements(method.value->'stepTree') step(value)
      WHERE item.deleted = false
        AND item.tenant_id = 1
        AND step.value->>'actionId' IN (
          'act-manual-arrive',
          'act-robot-arrive',
          'action-065340437bcb46788b7dc261fab81c63'
        )
        AND (
          item.name LIKE '%原油储罐%'
          OR item.id IN (2354, 2355, 3146, 2357)
        )
    LOOP
      pack := pg_temp.set_arrival_point(pack, arrival.item_id, arrival.means, arrival.node_key, tank.point_id);
    END LOOP;

    UPDATE ent_equipment_t1
    SET custom_fields = jsonb_set(
          COALESCE(custom_fields, '{}'::jsonb),
          '{host_sop_param_pack}',
          pack
        ),
        updater = 'seed',
        update_time = CURRENT_TIMESTAMP
    WHERE id = tank.equipment_id;
  END LOOP;
END $$;

DO $$
DECLARE
  missing text;
BEGIN
  SELECT string_agg(e.name || '/' || item.name || '/' || arrival.means, '，')
    INTO missing
  FROM tank_top t
  JOIN ent_equipment_t1 e ON e.id = t.equipment_id
  JOIN (
    SELECT item.id AS item_id,
           item.name,
           upper(method.value->>'methodKey') AS means,
           step.value->>'nodeKey' AS node_key
    FROM ent_inspection_item_t1 item
    CROSS JOIN LATERAL jsonb_array_elements(item.step_tree_json->'methods') method(value)
    CROSS JOIN LATERAL jsonb_array_elements(method.value->'stepTree') step(value)
    WHERE item.deleted = false
      AND item.tenant_id = 1
      AND item.id IN (2354, 2355, 2357, 3146)
      AND step.value->>'actionId' IN (
        'act-manual-arrive',
        'act-robot-arrive',
        'action-065340437bcb46788b7dc261fab81c63'
      )
  ) arrival ON true
  JOIN ent_inspection_item_t1 item ON item.id = arrival.item_id
  LEFT JOIN LATERAL (
    SELECT entry->'paramsByNode'->arrival.node_key->'F-e49f76bdca3e4e1393e8e2f1cc0e7867' AS loc
    FROM jsonb_array_elements(e.custom_fields->'host_sop_param_pack'->'entries') entry
    WHERE (entry->>'subjectId')::bigint = arrival.item_id
      AND upper(entry->>'dimensionValue') = arrival.means
    LIMIT 1
  ) got ON true
  WHERE got.loc IS DISTINCT FROM to_jsonb(t.point_id);
  IF missing IS NOT NULL THEN
    RAISE EXCEPTION '到达位置没有写成罐顶：%', missing;
  END IF;
END $$;

COMMIT;
