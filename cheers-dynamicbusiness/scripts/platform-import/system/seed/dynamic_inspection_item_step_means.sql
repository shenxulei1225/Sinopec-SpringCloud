-- 纠正检查步骤里放错手段的动作。
-- 人工：走到跟前，看，或核对读数。不转云台，不飞，不替设备拍照。
-- 无人机：飞到点、悬停、定点拍照。读表再开关仪表识别。不让飞机填核对表。
-- 机器人：走到位置、停留看、拍照。读表再开关仪表识别。
-- 固定摄像机：选摄像机、转云台、拍照。读表再开关仪表识别。
-- 不负责：给还没写步骤的检查项新编步骤；不把动作参数名单写进步骤。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

-- 旋转云台不再提供给人工。无人机、机器人、固定摄像机仍可用。
UPDATE ent_action_t1 a
SET execution_means = (
      SELECT COALESCE(jsonb_agg(elem), '[]'::jsonb)
      FROM jsonb_array_elements(a.execution_means) elem
      WHERE elem #>> '{}' <> 'MANUAL'
    ),
    param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}'::text[],
      (
        SELECT COALESCE(jsonb_agg(method.value ORDER BY method.ordinal), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        WHERE upper(COALESCE(method.value->>'executionMeans', method.value->>'methodKey', '')) <> 'MANUAL'
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'gimbalRotate';

UPDATE ent_inspection_item_t1
SET step_tree_json = $step2354${"version":1,"methods":[{"methodKey":"MANUAL","label":"人工","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-manual-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"act-mig-788d41abf5bbd55c","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"UAV","label":"无人机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-065340437bcb46788b7dc261fab81c63","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"hover","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"orientedShoot","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"ROBOT","label":"机器人","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-robot-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"act-robot-hold","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"FIXED_CAMERA","label":"固定摄像机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-262f1fa49d004e2bb48e9877037125c3","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"gimbalRotate","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]}]}$step2354$::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND id = 2354;

UPDATE ent_inspection_item_t1
SET step_tree_json = $step2357${"version":1,"methods":[{"methodKey":"MANUAL","label":"人工","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-manual-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"act-mig-788d41abf5bbd55c","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"UAV","label":"无人机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-065340437bcb46788b7dc261fab81c63","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"hover","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"orientedShoot","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"ROBOT","label":"机器人","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-robot-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"act-robot-hold","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"FIXED_CAMERA","label":"固定摄像机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-262f1fa49d004e2bb48e9877037125c3","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"gimbalRotate","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]}]}$step2357$::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND id = 2357;

UPDATE ent_inspection_item_t1
SET step_tree_json = $step2017${"version":1,"methods":[{"methodKey":"MANUAL","label":"人工","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-manual-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"act-mig-788d41abf5bbd55c","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"UAV","label":"无人机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-065340437bcb46788b7dc261fab81c63","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"hover","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"orientedShoot","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"ROBOT","label":"机器人","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-robot-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"act-robot-hold","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"FIXED_CAMERA","label":"固定摄像机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-262f1fa49d004e2bb48e9877037125c3","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"gimbalRotate","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]}]}$step2017$::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND id = 2017;

UPDATE ent_inspection_item_t1
SET step_tree_json = $step2355${"version":1,"methods":[{"methodKey":"MANUAL","label":"人工","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-manual-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"act-mig-824eab6187339394","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"action-cf214375fd8b441fb918c84902f8c0e6","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"UAV","label":"无人机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-065340437bcb46788b7dc261fab81c63","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"hover","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"orientedShoot","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":4,"nodeKey":"n4","actionId":"action-990b11dac60e489d89813bae8c8c80da","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":5,"nodeKey":"n5","actionId":"action-59d4ad5dff49463592f5b79227213a82","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"ROBOT","label":"机器人","stepTree":[{"order":1,"nodeKey":"n1","actionId":"act-robot-arrive","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"action-990b11dac60e489d89813bae8c8c80da","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":4,"nodeKey":"n4","actionId":"act-robot-hold","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":5,"nodeKey":"n5","actionId":"action-59d4ad5dff49463592f5b79227213a82","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]},{"methodKey":"FIXED_CAMERA","label":"固定摄像机","stepTree":[{"order":1,"nodeKey":"n1","actionId":"action-262f1fa49d004e2bb48e9877037125c3","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":2,"nodeKey":"n2","actionId":"gimbalRotate","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":3,"nodeKey":"n3","actionId":"takePhoto","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":4,"nodeKey":"n4","actionId":"action-990b11dac60e489d89813bae8c8c80da","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null},{"order":5,"nodeKey":"n5","actionId":"action-59d4ad5dff49463592f5b79227213a82","nodeType":"STEP","required":true,"dependsOn":[],"paramSlots":[],"executionMode":"SEQUENTIAL","parentNodeKey":null}]}]}$step2355$::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND id = 2355;

-- 跑冒滴漏的机器人档，关掉的应是跑冒滴漏检测，不是禁穿与违章。
UPDATE ent_inspection_item_t1 item
SET step_tree_json = jsonb_set(
      item.step_tree_json,
      '{methods}'::text[],
      (
        SELECT COALESCE(jsonb_agg(rewritten.method ORDER BY rewritten.ordinal), '[]'::jsonb)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN COALESCE(method.value->>'methodKey', method.value->>'executionMeans') = 'ROBOT'
                     THEN jsonb_set(
                       method.value,
                       CASE WHEN method.value ? 'stepTree' THEN '{stepTree}'::text[] ELSE '{actionTree}'::text[] END,
                       (
                         SELECT COALESCE(jsonb_agg(
                           CASE
                             WHEN node.value->>'actionId' = 'action-c1748f516dfc4cd5934b373062740cd3'
                               THEN jsonb_set(node.value, '{actionId}', '"action-fa4e3366ba554a73a7cd50234aaf78d8"')
                             ELSE node.value
                           END
                           ORDER BY node.ordinal
                         ), '[]'::jsonb)
                         FROM jsonb_array_elements(COALESCE(method.value->'stepTree', method.value->'actionTree'))
                           WITH ORDINALITY AS node(value, ordinal)
                       )
                     )
                   ELSE method.value
                 END AS method
          FROM jsonb_array_elements(item.step_tree_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        ) rewritten
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE item.deleted = false
  AND item.tenant_id = 1
  AND item.id = 3146;
