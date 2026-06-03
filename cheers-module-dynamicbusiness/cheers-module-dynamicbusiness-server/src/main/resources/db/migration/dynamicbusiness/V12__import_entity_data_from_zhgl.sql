-- Import entity instance data from zhgl (local dev). Flyway V12 — runs after metadata migrations (V7–V11).
-- Requires: CREATE EXTENSION dblink; zhgl DB on same host with biz_* tables.
-- Flyway placeholders: zhglHost, zhglDb, zhglUser, zhglPassword (see application-local.yaml).
-- Idempotent: skips each table if target already has rows.

CREATE EXTENSION IF NOT EXISTS dblink;
SET search_path TO dynamicbusiness, public;


-- biz_equipment
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_equipment') IS NULL THEN
    RAISE NOTICE 'skip biz_equipment: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_equipment;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_equipment: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_equipment') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_equipment: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_equipment: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_equipment (id, business_type_code, model_id, name, custom_fields, _deprecated_status, parent_id, tree_path, region_id, _deprecated_manufacturer, model_number, serial_number, purchase_date, warranty_expiry, health_score, last_maintenance_time, equipment_status, creator, create_time, updater, update_time, deleted, tenant_id, _deprecated_modelnumber, _deprecated_serialnumber, _deprecated_regionid, _deprecated_purchasedate, _deprecated_warrantyexpiry, _deprecated_equipmentstatus, _deprecated_lastmaintenancetime, _deprecated_healthscore, _deprecated_equipment_code, _deprecated_equipment_name, _deprecated_install_date, _deprecated_equipment_model, equipment_code, equipment_name, status, install_date, manufacturer, equipment_model, guid, device_type, device_code, coordinate_3d, coordinate_gis, code, rel_region, di_zuo_lei_xing, rel_ke_hu, rel_spare_part, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, business_type_code, model_id, name, custom_fields, _deprecated_status, parent_id, tree_path, region_id, _deprecated_manufacturer, model_number, serial_number, purchase_date, warranty_expiry, health_score, last_maintenance_time, equipment_status, creator, create_time, updater, update_time, deleted, tenant_id, _deprecated_modelnumber, _deprecated_serialnumber, _deprecated_regionid, _deprecated_purchasedate, _deprecated_warrantyexpiry, _deprecated_equipmentstatus, _deprecated_lastmaintenancetime, _deprecated_healthscore, _deprecated_equipment_code, _deprecated_equipment_name, _deprecated_install_date, _deprecated_equipment_model, equipment_code, equipment_name, status, install_date, manufacturer, equipment_model, guid, device_type, device_code, coordinate_3d, coordinate_gis, code, rel_region, di_zuo_lei_xing, rel_ke_hu, rel_spare_part, sort
  FROM dblink(
    conn,
    'SELECT id, business_type_code, model_id, name, custom_fields, _deprecated_status, parent_id, tree_path, region_id, _deprecated_manufacturer, model_number, serial_number, purchase_date, warranty_expiry, health_score, last_maintenance_time, equipment_status, creator, create_time, updater, update_time, deleted, tenant_id, _deprecated_modelnumber, _deprecated_serialnumber, _deprecated_regionid, _deprecated_purchasedate, _deprecated_warrantyexpiry, _deprecated_equipmentstatus, _deprecated_lastmaintenancetime, _deprecated_healthscore, _deprecated_equipment_code, _deprecated_equipment_name, _deprecated_install_date, _deprecated_equipment_model, equipment_code, equipment_name, status, install_date, manufacturer, equipment_model, guid, device_type, device_code, coordinate_3d, coordinate_gis, code, rel_region, di_zuo_lei_xing, rel_ke_hu, rel_spare_part, sort FROM biz_equipment'
  ) AS t(id bigint, business_type_code varchar, model_id bigint, name varchar, custom_fields jsonb, _deprecated_status integer, parent_id bigint, tree_path varchar, region_id bigint, _deprecated_manufacturer varchar, model_number varchar, serial_number varchar, purchase_date date, warranty_expiry date, health_score numeric, last_maintenance_time timestamp, equipment_status varchar, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint, _deprecated_modelnumber varchar, _deprecated_serialnumber varchar, _deprecated_regionid numeric, _deprecated_purchasedate date, _deprecated_warrantyexpiry date, _deprecated_equipmentstatus varchar, _deprecated_lastmaintenancetime timestamp, _deprecated_healthscore numeric, _deprecated_equipment_code varchar, _deprecated_equipment_name varchar, _deprecated_install_date date, _deprecated_equipment_model varchar, equipment_code varchar, equipment_name varchar, status varchar, install_date date, manufacturer varchar, equipment_model varchar, guid varchar, device_type numeric, device_code varchar, coordinate_3d varchar, coordinate_gis varchar, code varchar, rel_region varchar, di_zuo_lei_xing varchar, rel_ke_hu varchar, rel_spare_part varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_equipment', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_equipment), 1)
  );

  RAISE NOTICE 'imported biz_equipment: % rows', remote_cnt;
END $$;


-- biz_region
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_region') IS NULL THEN
    RAISE NOTICE 'skip biz_region: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_region;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_region: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_region') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_region: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_region: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_region (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, _deprecated_region_code, _deprecated_region_name, _deprecated_region_type, region_code, region_name, region_type, rel_equipment, rel_dian_wei, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, _deprecated_region_code, _deprecated_region_name, _deprecated_region_type, region_code, region_name, region_type, rel_equipment, rel_dian_wei, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, _deprecated_region_code, _deprecated_region_name, _deprecated_region_type, region_code, region_name, region_type, rel_equipment, rel_dian_wei, tree_path, sort FROM biz_region'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, _deprecated_region_code varchar, _deprecated_region_name varchar, _deprecated_region_type varchar, region_code varchar, region_name varchar, region_type varchar, rel_equipment varchar, rel_dian_wei varchar, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_region', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_region), 1)
  );

  RAISE NOTICE 'imported biz_region: % rows', remote_cnt;
END $$;


-- biz_task
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_task') IS NULL THEN
    RAISE NOTICE 'skip biz_task: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_task;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_task: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_task') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_task: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_task: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_task (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, f_f_b035eeaa884243269178a06374ae8438, f_f_ed97f195ccbe495d8766026635e4f5e3, f_f_e8289a51a3dd41c6b7f1539efd003258, f_f_8e92b92f84e74cfd853cdd8cf4f6361a, f_f_c367374d455f4024b29560be44f7c031, f_f_4f3b40defe104f1b94c3462c568bfb79, f_f_1d3bcec7a5df4ff985610db69a2c467b, f_f_3071c55d68ac4e03872d9758512aa17e, f_f_443c8879f02b479e8af045a80c9fdcca, f_f_0436a31b31dc4b4d8a5314177e254312, f_f_7e858ec039604fd5841722ddf3d3176a, f_f_8bf33d80787a411cb398f74c54b7843b, f_f_40617730a826424faa4b40ccd8a58414, f_f_1d9efb587ce0496aa8a967ebd7216627, f_f_209f806919c0492b97ee9a8aa98aaee9, f_f_6e001ac5256943b6840f5b9a3a17ac0b, f_f_524fd7c27947494384391f83aabbb9e6, f_f_224b4af4e1474d5d85002c03d0bdf89a, f_f_3c6a384288b84d4e9086206ff2673eb3, f_f_d9b5d2f7cb524d17bb2223a9e5a56f4c, f_f_ec6b7dec58434d76a6b0e9aeb79c197b, f_f_92d768ce991649fca38c345cf9959e05, f_f_39ab7f71e1d945fcbe6af9c4fbae1b25, f_f_6116465add0949b593f1dcd3f2b6e29d, f_f_cefb517417c643cdbd044929d1fe03f5, f_f_3dc0dc43a3894d698a38c2550ce47fdb, f_f_31d7dc332d2846c9837fb0d7f9e2ec51, f_f_e30a052673b6438d86ee0a99585cb9bb, f_f_c8dc3d61b1494b61a1197065fa41501d, f_f_65e1d5ebc4e4442d9483aa3f4313f7a9, f_f_981ca5abfa2840eeb5b4a97918029e9f, f_f_dc7aaac23a384e4b85ea70d2c35efe4a, f_f_9d05b9b3a3d24a6da5a41c8143bd448e, f_f_3d37caf40b634e3c9527bba283d92622, f_f_0b1047d4121b4b23af936cf6e6f06b7b, f_f_7c6e9af941d04fcfbb36dbe80134f119, f_f_5c4fbd6a48ca457ba09a97c4ccea8e71, f_f_b8a976c165824f848daac3c7bbe0fa1f, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, f_f_b035eeaa884243269178a06374ae8438, f_f_ed97f195ccbe495d8766026635e4f5e3, f_f_e8289a51a3dd41c6b7f1539efd003258, f_f_8e92b92f84e74cfd853cdd8cf4f6361a, f_f_c367374d455f4024b29560be44f7c031, f_f_4f3b40defe104f1b94c3462c568bfb79, f_f_1d3bcec7a5df4ff985610db69a2c467b, f_f_3071c55d68ac4e03872d9758512aa17e, f_f_443c8879f02b479e8af045a80c9fdcca, f_f_0436a31b31dc4b4d8a5314177e254312, f_f_7e858ec039604fd5841722ddf3d3176a, f_f_8bf33d80787a411cb398f74c54b7843b, f_f_40617730a826424faa4b40ccd8a58414, f_f_1d9efb587ce0496aa8a967ebd7216627, f_f_209f806919c0492b97ee9a8aa98aaee9, f_f_6e001ac5256943b6840f5b9a3a17ac0b, f_f_524fd7c27947494384391f83aabbb9e6, f_f_224b4af4e1474d5d85002c03d0bdf89a, f_f_3c6a384288b84d4e9086206ff2673eb3, f_f_d9b5d2f7cb524d17bb2223a9e5a56f4c, f_f_ec6b7dec58434d76a6b0e9aeb79c197b, f_f_92d768ce991649fca38c345cf9959e05, f_f_39ab7f71e1d945fcbe6af9c4fbae1b25, f_f_6116465add0949b593f1dcd3f2b6e29d, f_f_cefb517417c643cdbd044929d1fe03f5, f_f_3dc0dc43a3894d698a38c2550ce47fdb, f_f_31d7dc332d2846c9837fb0d7f9e2ec51, f_f_e30a052673b6438d86ee0a99585cb9bb, f_f_c8dc3d61b1494b61a1197065fa41501d, f_f_65e1d5ebc4e4442d9483aa3f4313f7a9, f_f_981ca5abfa2840eeb5b4a97918029e9f, f_f_dc7aaac23a384e4b85ea70d2c35efe4a, f_f_9d05b9b3a3d24a6da5a41c8143bd448e, f_f_3d37caf40b634e3c9527bba283d92622, f_f_0b1047d4121b4b23af936cf6e6f06b7b, f_f_7c6e9af941d04fcfbb36dbe80134f119, f_f_5c4fbd6a48ca457ba09a97c4ccea8e71, f_f_b8a976c165824f848daac3c7bbe0fa1f, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, f_f_b035eeaa884243269178a06374ae8438, f_f_ed97f195ccbe495d8766026635e4f5e3, f_f_e8289a51a3dd41c6b7f1539efd003258, f_f_8e92b92f84e74cfd853cdd8cf4f6361a, f_f_c367374d455f4024b29560be44f7c031, f_f_4f3b40defe104f1b94c3462c568bfb79, f_f_1d3bcec7a5df4ff985610db69a2c467b, f_f_3071c55d68ac4e03872d9758512aa17e, f_f_443c8879f02b479e8af045a80c9fdcca, f_f_0436a31b31dc4b4d8a5314177e254312, f_f_7e858ec039604fd5841722ddf3d3176a, f_f_8bf33d80787a411cb398f74c54b7843b, f_f_40617730a826424faa4b40ccd8a58414, f_f_1d9efb587ce0496aa8a967ebd7216627, f_f_209f806919c0492b97ee9a8aa98aaee9, f_f_6e001ac5256943b6840f5b9a3a17ac0b, f_f_524fd7c27947494384391f83aabbb9e6, f_f_224b4af4e1474d5d85002c03d0bdf89a, f_f_3c6a384288b84d4e9086206ff2673eb3, f_f_d9b5d2f7cb524d17bb2223a9e5a56f4c, f_f_ec6b7dec58434d76a6b0e9aeb79c197b, f_f_92d768ce991649fca38c345cf9959e05, f_f_39ab7f71e1d945fcbe6af9c4fbae1b25, f_f_6116465add0949b593f1dcd3f2b6e29d, f_f_cefb517417c643cdbd044929d1fe03f5, f_f_3dc0dc43a3894d698a38c2550ce47fdb, f_f_31d7dc332d2846c9837fb0d7f9e2ec51, f_f_e30a052673b6438d86ee0a99585cb9bb, f_f_c8dc3d61b1494b61a1197065fa41501d, f_f_65e1d5ebc4e4442d9483aa3f4313f7a9, f_f_981ca5abfa2840eeb5b4a97918029e9f, f_f_dc7aaac23a384e4b85ea70d2c35efe4a, f_f_9d05b9b3a3d24a6da5a41c8143bd448e, f_f_3d37caf40b634e3c9527bba283d92622, f_f_0b1047d4121b4b23af936cf6e6f06b7b, f_f_7c6e9af941d04fcfbb36dbe80134f119, f_f_5c4fbd6a48ca457ba09a97c4ccea8e71, f_f_b8a976c165824f848daac3c7bbe0fa1f, tree_path, sort FROM biz_task'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, f_f_b035eeaa884243269178a06374ae8438 varchar, f_f_ed97f195ccbe495d8766026635e4f5e3 varchar, f_f_e8289a51a3dd41c6b7f1539efd003258 varchar, f_f_8e92b92f84e74cfd853cdd8cf4f6361a varchar, f_f_c367374d455f4024b29560be44f7c031 varchar, f_f_4f3b40defe104f1b94c3462c568bfb79 varchar, f_f_1d3bcec7a5df4ff985610db69a2c467b timestamp, f_f_3071c55d68ac4e03872d9758512aa17e text, f_f_443c8879f02b479e8af045a80c9fdcca varchar, f_f_0436a31b31dc4b4d8a5314177e254312 timestamp, f_f_7e858ec039604fd5841722ddf3d3176a varchar, f_f_8bf33d80787a411cb398f74c54b7843b varchar, f_f_40617730a826424faa4b40ccd8a58414 varchar, f_f_1d9efb587ce0496aa8a967ebd7216627 varchar, f_f_209f806919c0492b97ee9a8aa98aaee9 varchar, f_f_6e001ac5256943b6840f5b9a3a17ac0b varchar, f_f_524fd7c27947494384391f83aabbb9e6 varchar, f_f_224b4af4e1474d5d85002c03d0bdf89a text, f_f_3c6a384288b84d4e9086206ff2673eb3 text, f_f_d9b5d2f7cb524d17bb2223a9e5a56f4c timestamp, f_f_ec6b7dec58434d76a6b0e9aeb79c197b timestamp, f_f_92d768ce991649fca38c345cf9959e05 timestamp, f_f_39ab7f71e1d945fcbe6af9c4fbae1b25 numeric, f_f_6116465add0949b593f1dcd3f2b6e29d text, f_f_cefb517417c643cdbd044929d1fe03f5 text, f_f_3dc0dc43a3894d698a38c2550ce47fdb text, f_f_31d7dc332d2846c9837fb0d7f9e2ec51 text, f_f_e30a052673b6438d86ee0a99585cb9bb text, f_f_c8dc3d61b1494b61a1197065fa41501d varchar, f_f_65e1d5ebc4e4442d9483aa3f4313f7a9 varchar, f_f_981ca5abfa2840eeb5b4a97918029e9f numeric, f_f_dc7aaac23a384e4b85ea70d2c35efe4a text, f_f_9d05b9b3a3d24a6da5a41c8143bd448e varchar, f_f_3d37caf40b634e3c9527bba283d92622 text, f_f_0b1047d4121b4b23af936cf6e6f06b7b text, f_f_7c6e9af941d04fcfbb36dbe80134f119 text, f_f_5c4fbd6a48ca457ba09a97c4ccea8e71 varchar, f_f_b8a976c165824f848daac3c7bbe0fa1f text, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_task', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_task), 1)
  );

  RAISE NOTICE 'imported biz_task: % rows', remote_cnt;
END $$;


-- biz_inspection_item
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_inspection_item') IS NULL THEN
    RAISE NOTICE 'skip biz_inspection_item: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_inspection_item;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_inspection_item: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_inspection_item') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_inspection_item: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_inspection_item: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_inspection_item (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_inspection_item'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_inspection_item', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_inspection_item), 1)
  );

  RAISE NOTICE 'imported biz_inspection_item: % rows', remote_cnt;
END $$;


-- biz_emergency_resource
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_emergency_resource') IS NULL THEN
    RAISE NOTICE 'skip biz_emergency_resource: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_emergency_resource;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_emergency_resource: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_emergency_resource') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_emergency_resource: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_emergency_resource: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_emergency_resource (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_emergency_resource'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_emergency_resource', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_emergency_resource), 1)
  );

  RAISE NOTICE 'imported biz_emergency_resource: % rows', remote_cnt;
END $$;


-- biz_customer
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_customer') IS NULL THEN
    RAISE NOTICE 'skip biz_customer: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_customer;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_customer: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_customer') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_customer: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_customer: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_customer (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_customer'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_customer', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_customer), 1)
  );

  RAISE NOTICE 'imported biz_customer: % rows', remote_cnt;
END $$;


-- biz_inspection_point
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_inspection_point') IS NULL THEN
    RAISE NOTICE 'skip biz_inspection_point: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_inspection_point;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_inspection_point: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_inspection_point') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_inspection_point: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_inspection_point: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_inspection_point (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, rel_region, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, rel_region, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, rel_region, tree_path, sort FROM biz_inspection_point'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, rel_region varchar, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_inspection_point', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_inspection_point), 1)
  );

  RAISE NOTICE 'imported biz_inspection_point: % rows', remote_cnt;
END $$;


-- biz_emergency_team
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_emergency_team') IS NULL THEN
    RAISE NOTICE 'skip biz_emergency_team: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_emergency_team;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_emergency_team: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_emergency_team') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_emergency_team: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_emergency_team: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_emergency_team (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_emergency_team'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_emergency_team', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_emergency_team), 1)
  );

  RAISE NOTICE 'imported biz_emergency_team: % rows', remote_cnt;
END $$;


-- biz_route
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_route') IS NULL THEN
    RAISE NOTICE 'skip biz_route: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_route;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_route: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_route') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_route: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_route: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_route (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_route'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_route', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_route), 1)
  );

  RAISE NOTICE 'imported biz_route: % rows', remote_cnt;
END $$;


-- biz_billing
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_billing') IS NULL THEN
    RAISE NOTICE 'skip biz_billing: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_billing;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_billing: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_billing') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_billing: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_billing: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_billing (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_billing'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_billing', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_billing), 1)
  );

  RAISE NOTICE 'imported biz_billing: % rows', remote_cnt;
END $$;


-- biz_spare_part
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_spare_part') IS NULL THEN
    RAISE NOTICE 'skip biz_spare_part: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_spare_part;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_spare_part: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_spare_part') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_spare_part: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_spare_part: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_spare_part (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, spare_part_code, spare_part_name, stock_quantity, unit, min_stock, rel_equipment, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, spare_part_code, spare_part_name, stock_quantity, unit, min_stock, rel_equipment, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, spare_part_code, spare_part_name, stock_quantity, unit, min_stock, rel_equipment, tree_path, sort FROM biz_spare_part'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, spare_part_code varchar, spare_part_name varchar, stock_quantity numeric, unit varchar, min_stock numeric, rel_equipment varchar, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_spare_part', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_spare_part), 1)
  );

  RAISE NOTICE 'imported biz_spare_part: % rows', remote_cnt;
END $$;


-- biz_pipeline
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_pipeline') IS NULL THEN
    RAISE NOTICE 'skip biz_pipeline: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_pipeline;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_pipeline: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_pipeline') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_pipeline: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_pipeline: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_pipeline (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, pipeline_code, pipeline_name, install_date, manufacturer, pipeline_model, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, pipeline_code, pipeline_name, install_date, manufacturer, pipeline_model, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, pipeline_code, pipeline_name, install_date, manufacturer, pipeline_model, tree_path, sort FROM biz_pipeline'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, pipeline_code varchar, pipeline_name varchar, install_date date, manufacturer varchar, pipeline_model varchar, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_pipeline', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_pipeline), 1)
  );

  RAISE NOTICE 'imported biz_pipeline: % rows', remote_cnt;
END $$;


-- biz_personnel
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_personnel') IS NULL THEN
    RAISE NOTICE 'skip biz_personnel: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_personnel;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_personnel: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_personnel') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_personnel: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_personnel: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_personnel (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_personnel'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_personnel', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_personnel), 1)
  );

  RAISE NOTICE 'imported biz_personnel: % rows', remote_cnt;
END $$;


-- biz_maintenance
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_maintenance') IS NULL THEN
    RAISE NOTICE 'skip biz_maintenance: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_maintenance;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_maintenance: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_maintenance') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_maintenance: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_maintenance: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_maintenance (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, _deprecated_order_no, _deprecated_maintenance_type, _deprecated_plan_time, _deprecated_executor, _deprecated_order_status, order_no, maintenance_type, plan_time, executor, order_status, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, _deprecated_order_no, _deprecated_maintenance_type, _deprecated_plan_time, _deprecated_executor, _deprecated_order_status, order_no, maintenance_type, plan_time, executor, order_status, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, _deprecated_order_no, _deprecated_maintenance_type, _deprecated_plan_time, _deprecated_executor, _deprecated_order_status, order_no, maintenance_type, plan_time, executor, order_status, tree_path, sort FROM biz_maintenance'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, _deprecated_order_no varchar, _deprecated_maintenance_type varchar, _deprecated_plan_time timestamp, _deprecated_executor varchar, _deprecated_order_status varchar, order_no varchar, maintenance_type varchar, plan_time timestamp, executor varchar, order_status varchar, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_maintenance', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_maintenance), 1)
  );

  RAISE NOTICE 'imported biz_maintenance: % rows', remote_cnt;
END $$;


-- biz_patrol
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_patrol') IS NULL THEN
    RAISE NOTICE 'skip biz_patrol: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_patrol;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_patrol: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_patrol') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_patrol: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_patrol: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_patrol (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_patrol'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_patrol', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_patrol), 1)
  );

  RAISE NOTICE 'imported biz_patrol: % rows', remote_cnt;
END $$;


-- biz_fault
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_fault') IS NULL THEN
    RAISE NOTICE 'skip biz_fault: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_fault;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_fault: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_fault') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_fault: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_fault: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_fault (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, fault_no, fault_type, fault_level, phenomenon, occur_time, resolved, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, fault_no, fault_type, fault_level, phenomenon, occur_time, resolved, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, fault_no, fault_type, fault_level, phenomenon, occur_time, resolved, tree_path, sort FROM biz_fault'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, fault_no varchar, fault_type varchar, fault_level varchar, phenomenon varchar, occur_time timestamp, resolved boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_fault', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_fault), 1)
  );

  RAISE NOTICE 'imported biz_fault: % rows', remote_cnt;
END $$;


-- biz_emergency
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  IF to_regclass('dynamicbusiness.biz_emergency') IS NULL THEN
    RAISE NOTICE 'skip biz_emergency: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.biz_emergency;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip biz_emergency: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM biz_emergency') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip biz_emergency: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip biz_emergency: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.biz_emergency (id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort)
  OVERRIDING SYSTEM VALUE
  SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort
  FROM dblink(
    conn,
    'SELECT id, tenant_id, business_type_code, model_id, name, code, status, area_id, parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted, tree_path, sort FROM biz_emergency'
  ) AS t(id bigint, tenant_id bigint, business_type_code varchar, model_id bigint, name varchar, code varchar, status integer, area_id bigint, parent_id bigint, attrs jsonb, custom_fields text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tree_path varchar, sort integer);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.biz_emergency', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.biz_emergency), 1)
  );

  RAISE NOTICE 'imported biz_emergency: % rows', remote_cnt;
END $$;


-- field index

DO $$
DECLARE
  local_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_cnt FROM dynamic_entity_field_index;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_entity_field_index: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    INSERT INTO dynamic_entity_field_index
      (id, entity_id, model_id, field_code, value_string, value_number, value_date,
       value_datetime, value_boolean, creator, create_time, updater, update_time, deleted, tenant_id)
    OVERRIDING SYSTEM VALUE
    SELECT id, entity_id, model_id, field_code, value_string, value_number, value_date,
           value_datetime, value_boolean, creator, create_time, updater, update_time, deleted, tenant_id
    FROM dblink(
      conn,
      'SELECT id, entity_id, model_id, field_code, value_string, value_number, value_date,
              value_datetime, value_boolean, creator, create_time, updater, update_time, deleted, tenant_id
       FROM system_entity_field_index WHERE deleted = false'
    ) AS t(
      id bigint, entity_id bigint, model_id bigint, field_code varchar,
      value_string varchar, value_number numeric, value_date date,
      value_datetime timestamp, value_boolean boolean,
      creator varchar, create_time timestamp, updater varchar,
      update_time timestamp, deleted boolean, tenant_id bigint
    );
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_entity_field_index: %', SQLERRM;
    RETURN;
  END;

  PERFORM setval(
    pg_get_serial_sequence('dynamic_entity_field_index', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_entity_field_index), 1)
  );
END $$;


-- category relation

DO $$
DECLARE
  local_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_cnt FROM dynamic_entity_category_relation;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_entity_category_relation: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    INSERT INTO dynamic_entity_category_relation
      (id, entity_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id)
    OVERRIDING SYSTEM VALUE
    SELECT id, entity_id, category_id, business_type_code, COALESCE(sort, 0), creator, create_time, updater, update_time, deleted, tenant_id
    FROM dblink(
      conn,
      'SELECT id, entity_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id
       FROM system_entity_category_relation WHERE deleted = false'
    ) AS t(
      id bigint, entity_id bigint, category_id bigint, business_type_code varchar,
      sort integer, creator varchar, create_time timestamp, updater varchar,
      update_time timestamp, deleted boolean, tenant_id bigint
    );
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_entity_category_relation: %', SQLERRM;
    RETURN;
  END;

  PERFORM setval(
    pg_get_serial_sequence('dynamic_entity_category_relation', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_entity_category_relation), 1)
  );
END $$;


-- entity relation

DO $$
DECLARE
  local_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_cnt FROM dynamic_entity_relation;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_entity_relation: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    INSERT INTO dynamic_entity_relation
      (id, source_entity_id, target_entity_id, relation_type, relation_name, description,
       relation_attributes, status, field_code, source_model_code, target_model_code,
       source_business_type_code, target_business_type_code,
       creator, create_time, updater, update_time, deleted, tenant_id)
    OVERRIDING SYSTEM VALUE
    SELECT id, source_entity_id, target_entity_id, relation_type, relation_name, description,
           relation_attributes, status, field_code, source_model_code, target_model_code,
           source_business_type_code, target_business_type_code,
           creator, create_time, updater, update_time, deleted, tenant_id
    FROM dblink(
      conn,
      'SELECT id, source_entity_id, target_entity_id, relation_type, relation_name, description,
              relation_attributes, status, field_code, source_model_code, target_model_code,
              source_business_type_code, target_business_type_code,
              creator, create_time, updater, update_time, deleted, tenant_id
       FROM system_entity_relation WHERE deleted = false'
    ) AS t(
      id bigint, source_entity_id bigint, target_entity_id bigint, relation_type varchar,
      relation_name varchar, description text, relation_attributes text, status integer,
      field_code varchar, source_model_code varchar, target_model_code varchar,
      source_business_type_code varchar, target_business_type_code varchar,
      creator varchar, create_time timestamp, updater varchar,
      update_time timestamp, deleted boolean, tenant_id bigint
    );
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_entity_relation: %', SQLERRM;
    RETURN;
  END;

  PERFORM setval(
    pg_get_serial_sequence('dynamic_entity_relation', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_entity_relation), 1)
  );
END $$;

