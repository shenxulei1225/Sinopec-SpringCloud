-- 一键起飞、一键返航只登记大疆无人机，只保留大疆机场对照。
-- 指令起飞、指令降落只登记东古无人机。
-- 东古起飞 200101 挂在指令起飞。东古降落 200103 挂在指令降落。
SET search_path TO dynamicbusiness;
BEGIN;

UPDATE ent_action_t1
SET name = '指令起飞',
    deleted = false
WHERE code = 'act-uav-takeoff';

-- 动作曾经停用，停用时「无人机」分类关系一并停了。动作恢复后把这条关系恢复，列表里才看得到。
UPDATE dynamic_entity_category_relation_t1 r
SET deleted = false
FROM ent_action_t1 a
WHERE a.id = r.entity_id
  AND a.code = 'act-uav-takeoff'
  AND r.category_id = 2104745
  AND r.entity_type_code = 'action';

UPDATE ent_action_t1
SET name = '指令降落'
WHERE code = 'act-uav-land';

UPDATE ent_protocol_mapping_t1 m
SET name = '指令起飞 · 无人机对接协议',
    deleted = false,
    custom_fields = jsonb_set(
        m.custom_fields,
        '{action_ref,id}',
        to_jsonb(a.id),
        false
    )
FROM ent_action_t1 a
WHERE m.code = 'map-takeoff-uav-ws'
  AND a.code = 'act-uav-takeoff';

UPDATE ent_protocol_mapping_t1
SET deleted = true
WHERE code = 'map-land-uav-ws';

UPDATE ent_protocol_mapping_t1
SET name = '指令降落 · 无人机对接协议',
    deleted = false
WHERE code = 'map-uav-land-uav-ws';

-- 适用型号：一键起飞、一键返航只留大疆。指令起飞、指令降落只留东古。
DO $$
DECLARE
    dji jsonb := '[1867,1868,1872,1873,1874,1875,1865,1869,1870,1871,1864,1866,1876,1877]'::jsonb;
    dong jsonb := '[1912,1913]'::jsonb;
    slots jsonb;
    methods jsonb;
    row jsonb;
    kept jsonb;
    i int;
    model_id text;
BEGIN
    SELECT param_slots_json INTO slots
    FROM ent_action_t1
    WHERE code = 'action-2b44a1f882f54438b6a1864e3636c889';
    methods := COALESCE(slots->'methods', '[]'::jsonb);
    kept := '[]'::jsonb;
    FOR i IN 0 .. jsonb_array_length(methods) - 1 LOOP
        row := methods->i;
        model_id := row->>'equipmentModelId';
        IF model_id IN ('1912', '1913') THEN
            CONTINUE;
        END IF;
        IF COALESCE(row->>'executionMeans', '') = 'UAV' AND model_id IS NULL THEN
            row := jsonb_set(row, '{applicableModelIds}', dji, true);
        END IF;
        kept := kept || jsonb_build_array(row);
    END LOOP;
    slots := jsonb_set(slots, '{methods}', kept, true);
    UPDATE ent_action_t1 SET param_slots_json = slots WHERE code = 'action-2b44a1f882f54438b6a1864e3636c889';

    SELECT param_slots_json INTO slots
    FROM ent_action_t1
    WHERE code = 'action-1022d966da2b45f7b73e776112e6884b';
    methods := COALESCE(slots->'methods', '[]'::jsonb);
    kept := '[]'::jsonb;
    FOR i IN 0 .. jsonb_array_length(methods) - 1 LOOP
        row := methods->i;
        IF COALESCE(row->>'executionMeans', '') = 'UAV' AND row->>'equipmentModelId' IS NULL THEN
            row := jsonb_set(row, '{applicableModelIds}', dji, true);
            row := jsonb_set(row, '{applicableCategoryId}', '2104808'::jsonb, true);
        END IF;
        kept := kept || jsonb_build_array(row);
    END LOOP;
    IF NOT EXISTS (
        SELECT 1 FROM jsonb_array_elements(kept) e
        WHERE e->>'executionMeans' = 'UAV' AND e->>'equipmentModelId' IS NULL
    ) THEN
        kept := kept || jsonb_build_array(jsonb_build_object(
            'executionMeans', 'UAV',
            'fields', '[]'::jsonb,
            'applicableModelIds', dji,
            'applicableCategoryId', 2104808,
            'paramsPerEquipmentModel', false,
            'paramsSharedAcrossModels', true
        ));
    END IF;
    slots := jsonb_set(COALESCE(slots, '{}'::jsonb), '{methods}', kept, true);
    UPDATE ent_action_t1 SET param_slots_json = slots WHERE code = 'action-1022d966da2b45f7b73e776112e6884b';

    SELECT param_slots_json INTO slots FROM ent_action_t1 WHERE code = 'act-uav-takeoff';
    methods := COALESCE(slots->'methods', '[]'::jsonb);
    kept := '[]'::jsonb;
    FOR i IN 0 .. jsonb_array_length(methods) - 1 LOOP
        row := methods->i;
        IF COALESCE(row->>'executionMeans', '') = 'UAV' AND row->>'equipmentModelId' IS NULL THEN
            row := jsonb_set(row, '{applicableModelIds}', dong, true);
            row := jsonb_set(row, '{applicableCategoryId}', '2104808'::jsonb, true);
            row := jsonb_set(row, '{paramsPerEquipmentModel}', 'false'::jsonb, true);
            row := jsonb_set(row, '{paramsSharedAcrossModels}', 'true'::jsonb, true);
        END IF;
        kept := kept || jsonb_build_array(row);
    END LOOP;
    slots := jsonb_set(slots, '{methods}', kept, true);
    UPDATE ent_action_t1 SET param_slots_json = slots WHERE code = 'act-uav-takeoff';

    SELECT param_slots_json INTO slots FROM ent_action_t1 WHERE code = 'act-uav-land';
    methods := COALESCE(slots->'methods', '[]'::jsonb);
    IF NOT EXISTS (
        SELECT 1 FROM jsonb_array_elements(methods) e
        WHERE e->>'executionMeans' = 'UAV' AND e->>'equipmentModelId' IS NULL
    ) THEN
        methods := methods || jsonb_build_array(jsonb_build_object(
            'executionMeans', 'UAV',
            'fields', COALESCE(slots->'fields', '[]'::jsonb),
            'applicableModelIds', dong,
            'applicableCategoryId', 2104808,
            'paramsPerEquipmentModel', false,
            'paramsSharedAcrossModels', true
        ));
    ELSE
        kept := '[]'::jsonb;
        FOR i IN 0 .. jsonb_array_length(methods) - 1 LOOP
            row := methods->i;
            IF COALESCE(row->>'executionMeans', '') = 'UAV' AND row->>'equipmentModelId' IS NULL THEN
                row := jsonb_set(row, '{applicableModelIds}', dong, true);
                row := jsonb_set(row, '{applicableCategoryId}', '2104808'::jsonb, true);
            END IF;
            kept := kept || jsonb_build_array(row);
        END LOOP;
        methods := kept;
    END IF;
    slots := jsonb_set(COALESCE(slots, '{}'::jsonb), '{methods}', methods, true);
    UPDATE ent_action_t1 SET param_slots_json = slots WHERE code = 'act-uav-land';
END $$;

COMMIT;
