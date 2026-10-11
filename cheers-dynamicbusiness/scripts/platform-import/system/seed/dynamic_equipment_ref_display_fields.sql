SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_field
SET name = '机身相机'
WHERE code = 'body_camera_group'
  AND deleted = false
  AND name = '机身分组';

DO $$
DECLARE
  fid bigint;
  ids jsonb;
  rec record;
  next_groups jsonb;
BEGIN
  SELECT id,
         CASE
           WHEN options IS NOT NULL AND options ~ '^\s*\{'
             THEN COALESCE(options::jsonb -> 'sourceFieldIds', '[]'::jsonb)
           ELSE '[]'::jsonb
         END
    INTO fid, ids
  FROM dynamic_field
  WHERE code = 'body_camera_group'
    AND deleted = false
  LIMIT 1;

  IF fid IS NULL OR jsonb_typeof(ids) <> 'array' OR jsonb_array_length(ids) = 0 THEN
    RETURN;
  END IF;

  FOR rec IN
    SELECT id, field_groups_config::jsonb AS config
    FROM dynamic_model
    WHERE deleted = false
      AND field_groups_config IS NOT NULL
      AND field_groups_config ~ '^\s*\{'
      AND (field_groups_config::jsonb -> 'groups') @> jsonb_build_array(
            jsonb_build_object('fields', jsonb_build_array(jsonb_build_object('fieldId', fid)))
          )
  LOOP
    SELECT jsonb_agg(
             CASE
               WHEN grp -> 'fields' IS NULL THEN grp
               ELSE jsonb_set(
                 grp,
                 '{fields}',
                 (
                   SELECT jsonb_agg(
                            CASE
                              WHEN (fld ->> 'fieldId')::bigint = fid
                               AND (
                                 fld -> 'extra' -> 'displayFieldIds' IS NULL
                                 OR jsonb_typeof(fld -> 'extra' -> 'displayFieldIds') <> 'array'
                                 OR jsonb_array_length(COALESCE(fld -> 'extra' -> 'displayFieldIds', '[]'::jsonb)) = 0
                               )
                              THEN jsonb_set(
                                CASE WHEN fld ? 'extra' THEN fld ELSE jsonb_set(fld, '{extra}', '{}'::jsonb, true) END,
                                '{extra,displayFieldIds}',
                                ids,
                                true
                              )
                              ELSE fld
                            END
                            ORDER BY field_ord
                          )
                   FROM jsonb_array_elements(grp -> 'fields') WITH ORDINALITY AS field_row(fld, field_ord)
                 ),
                 true
               )
             END
             ORDER BY group_ord
           )
      INTO next_groups
    FROM jsonb_array_elements(rec.config -> 'groups') WITH ORDINALITY AS group_row(grp, group_ord);

    UPDATE dynamic_model
    SET field_groups_config = jsonb_set(rec.config, '{groups}', next_groups, true)::text
    WHERE id = rec.id;
  END LOOP;
END $$;

COMMIT;
