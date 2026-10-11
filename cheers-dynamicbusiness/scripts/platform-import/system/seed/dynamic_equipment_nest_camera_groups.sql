SET search_path TO dynamicbusiness;

-- 机身相机、云台相机放进同一型号上已有的设备指标。没有设备指标的型号不动。
UPDATE dynamic_model AS model
SET field_groups_config = nested.config
FROM (
  SELECT
    id,
    jsonb_set(
      cfg,
      '{groups}',
      (
        SELECT COALESCE(jsonb_agg(item.group_json ORDER BY item.ord), '[]'::jsonb)
        FROM (
          SELECT
            elem.ord,
            CASE
              WHEN elem.group_json->>'name' IN ('机身相机', '云台相机')
                THEN elem.group_json || jsonb_build_object('parentId', source.parent_id)
              ELSE elem.group_json
            END AS group_json
          FROM jsonb_array_elements(cfg->'groups') WITH ORDINALITY AS elem(group_json, ord)
        ) AS item
      )
    )::text AS config
  FROM (
    SELECT
      id,
      field_groups_config::jsonb AS cfg,
      (
        SELECT parent_group->>'id'
        FROM jsonb_array_elements(field_groups_config::jsonb->'groups') AS parent_group
        WHERE parent_group->>'name' = '设备指标'
        LIMIT 1
      ) AS parent_id
    FROM dynamic_model
    WHERE field_groups_config IS NOT NULL
      AND field_groups_config <> ''
      AND jsonb_typeof(field_groups_config::jsonb->'groups') = 'array'
  ) AS source
  WHERE source.parent_id IS NOT NULL
    AND EXISTS (
      SELECT 1
      FROM jsonb_array_elements(source.cfg->'groups') AS child
      WHERE child->>'name' IN ('机身相机', '云台相机')
        AND COALESCE(child->>'parentId', '') IS DISTINCT FROM source.parent_id
    )
) AS nested
WHERE model.id = nested.id;
