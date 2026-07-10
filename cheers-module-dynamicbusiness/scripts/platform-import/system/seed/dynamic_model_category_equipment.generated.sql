-- equipment model ↔ standard category library
-- Source MD: F:/XProject/参考资料/弱电集成设备清单-武汉理工光科.md
-- Relations: 97

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ACU'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-b1442741c47f926f'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SENSOR-CO2'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6c57190b28a46057'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 3, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SERVER-GIS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-d264f01e700c7d20'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-IP-PHONE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-b070a5657ad133ab'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 5, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VOIP-CTRL'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-16de91ca89897431'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 6, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-IP-PHONE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-1fa3e2fd0c5fb3dd'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 7, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VOIP-SOFTWARE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-80350db0b05b9a4d'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 8, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VOIP-SOFTWARE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-bbdd7bd0d7cfbc3a'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 9, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VOIP-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-cb1269157176295b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 10, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-KVM'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-9597b8ece068f6d9'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 11, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-LED-PANEL'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-195cbd1d1d421043'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 12, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-LED-PANEL'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-804736552fb88c8d'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 13, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SENSOR-O2'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6ccef78e4f0c5091'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 14, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-UPS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-e550ca3e1829c5fa'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 15, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-UPS-CABINET'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-32bb4b9b4d571db1'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 16, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POWER-METER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-70c02f4b3027abe3'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 17, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-NET-UTM'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-35bc92a7327667f0'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 18, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-ANTENNA'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-d0e9abcf3626c59f'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 19, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SENSOR-CO2'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-aa98fa746b322feb'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 20, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-CT'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-d918458c7a03f584'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 21, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-READER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-93a4f69c7cc31077'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 22, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-READER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-53cdf81bf0f216c6'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 23, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-SOFTWARE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-aefd22c278a2fba0'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 24, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-WS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-017cdd4b1ecacfb3'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 25, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-871afc1fefcafea9'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 26, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-SOFTWARE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-b40633b2751387be'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 27, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SW-CORE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-9f09370988167c1e'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 28, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-STORAGE-DISK'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-dba95e5328a788f2'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 29, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-LAPTOP'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-bc68232ca7da9c99'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 30, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-SPLITTER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-45866bf269164142'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 31, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-OPTICAL-MODULE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-2896d6f3e19d0d25'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 32, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-MEDIA-CONVERTER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-9393cff74f6ec775'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 33, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-NMS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-65a25f8ecf1df71f'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 34, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-REMOTE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6b0399b9bd9513cf'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 35, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-INTRUSION-WS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-ce93046f5041439c'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 36, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-INTRUSION-SOFTWARE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-7ea0c802548b19d4'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 37, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ACU'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-755078aa7dfb541b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 38, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-SPLITTER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-94b81b84ca2b7b9b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 39, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ACCESS-SINGLE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-637a9cfb3b25c85c'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 40, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-DUPLEXER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-1afd687e6bc21755'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 41, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ACCESS-DOUBLE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-41c311cc67bed2ba'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 42, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VIDEO-SENDER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6959c7a26d9608fb'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 43, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SOUNDER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-a290cbb76ad097b6'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 44, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DISPLAY-MOUNT'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-dabc2c853e32865f'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 45, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-NET-ROUTER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-04fca772459212a2'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 46, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-ANTENNA'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-1530b29ad576c094'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 47, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-ANTENNA'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-100542002dec8b72'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 48, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-WS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-377d7ee3350e9b66'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 49, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SERVER-APP'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-58eec4087a92e5eb'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 50, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-PRINTER-COLOR'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-9b56903df958dfdc'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 51, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ALARM-HOST'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6dc4abc3b89d2bac'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 52, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ALARM-HOST'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-b80d7b8c5adc4eaa'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 53, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VIDEO-WALL-CTRL'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-87ff40a56ac3f461'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 54, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SW-ACCESS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-0445b371f7401e3b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 55, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-ACCESSORY'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-d7bf0fd8e0801a9d'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 56, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VOIP-GW'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-fc1a506efbe6b9de'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 57, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-REPEATER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-518bc7240d12e8ae'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 58, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SERVER-DB'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-f2dda8570a79ce1a'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 59, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-FRESH-AIR'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-acf9ca5f8079e490'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 60, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-HANDSET'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-2f7f90648ff9a2ee'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 61, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-REMOTE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-e8c7a010de171113'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 62, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RACK-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-d1349f3314122c0b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 63, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RACK-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-97c5d88b468cdb63'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 64, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-844a4dcc1c3de7c9'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 65, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-CONSOLE-LCD'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-91013dd60872d3fc'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 66, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SENSOR-O2'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-bad6ce090800b5fe'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 67, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-STREAM-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-2ee0687b44b35798'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 68, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SENSOR-TH'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-e6900cea94d17e7c'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 69, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SENSOR-TH'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6f06efcfd1cd2635'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 70, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SENSOR-LEAK'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-1f2c95093caf10e9'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 71, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-SOFTWARE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-850626138d6f6e1f'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 72, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SW-RING'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-75aef1bb4f2200b6'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 73, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SW-RING'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-10c9c5766f9b9dba'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 74, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-ACCESSORY'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-99202b60c398d30e'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 75, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-SECURITY-CABINET'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-c2108b63b671fd8e'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 76, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-SMS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-3afd58393cf95b5d'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 77, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-POS-TERMINAL'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-28e52efbecda00f1'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 78, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-HVAC'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-4ae41b3638a16f5d'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 79, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-TERMINAL'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6f9364efaed005fa'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 80, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-PDU-PRECISION'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-e19263288192561d'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 81, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ALARM-HOST'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-75d60b77b50eb489'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 82, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-CAMERA-DOME'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-829d372b286ad175'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 83, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-IR-DETECTOR'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-6b2c34aedd0144d7'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 84, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-CAMERA-IP'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-1d60b35024bbdcec'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 85, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RACK-NET'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-a5bf96067dbed1ba'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 86, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-RADIO-COUPLER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-797f0230493e2614'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 87, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-BATTERY-BANK'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-dcbe6b4940b488f3'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 88, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-STORAGE-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-7acb830b705688c6'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 89, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VIDEO-WORKSTATION'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-e72bf355dd09e036'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 90, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VIDEO-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-93cd38235ea76a82'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 91, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VOIP-WS'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-1c4ccb99d132c84b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 92, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ACCESS-CTRL'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-7e617f072030935b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 93, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ACCESS-SERVER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-cb364c23bb0d58e9'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 94, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-ACCESS-SOFTWARE'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-0d535dee7940828b'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 95, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-DCIM-HOST'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-c7e516234b0ecc91'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 96, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-VIDEO-DECODER'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-2dbf59aaf008127a'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id, c.id, 'equipment', m.code, c.code, 97, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'EQCAT-DEV-PRINTER-MONO'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'eqm-inv-bdc506ede65534ff'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

