-- 已经按型号分开的步骤，删掉写在步骤外面的旧参数。
-- 负责：步骤上有 byEquipmentModel 或 byCameraModel 时，只留下这两层。
-- 不负责：没有型号层的步骤（人工、摄像机，以及还没按型号分开的机器人拍照）；
--         把飞向目标点编号抄进到达作业位置。飞向目标点编号只属于飞向目标点这一步。
--         把飞机型号下的定点拍照改挂到相机类型。
SET search_path TO dynamicbusiness;

CREATE OR REPLACE FUNCTION pg_temp.step_keep_model_layers(node jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  kept jsonb := '{}'::jsonb;
BEGIN
  IF jsonb_typeof(node) <> 'object' THEN
    RETURN node;
  END IF;
  IF NOT (node ? 'byEquipmentModel' OR node ? 'byCameraModel') THEN
    RETURN node;
  END IF;

  IF jsonb_typeof(node->'byEquipmentModel') = 'object' THEN
    kept := kept || jsonb_build_object('byEquipmentModel', node->'byEquipmentModel');
  END IF;

  IF jsonb_typeof(node->'byCameraModel') = 'object' THEN
    kept := kept || jsonb_build_object('byCameraModel', node->'byCameraModel');
  END IF;

  RETURN kept;
END;
$$;

UPDATE ent_equipment_t1 equipment
SET custom_fields = jsonb_set(
      equipment.custom_fields,
      '{host_sop_param_pack,entries}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN jsonb_typeof(entry->'paramsByNode') = 'object' THEN
              jsonb_set(
                entry,
                '{paramsByNode}',
                (
                  SELECT COALESCE(jsonb_object_agg(
                    node.key,
                    pg_temp.step_keep_model_layers(node.value)
                  ), '{}'::jsonb)
                  FROM jsonb_each(entry->'paramsByNode') node
                )
              )
            ELSE entry
          END
        ), '[]'::jsonb)
        FROM jsonb_array_elements(equipment.custom_fields->'host_sop_param_pack'->'entries') entry
      ),
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE equipment.deleted = false
  AND jsonb_typeof(equipment.custom_fields->'host_sop_param_pack'->'entries') = 'array';
