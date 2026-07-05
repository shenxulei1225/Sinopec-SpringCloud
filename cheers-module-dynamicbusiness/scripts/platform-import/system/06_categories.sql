-- ============================================================================
-- 系统共用 · 06 分类（equipment 常用分类）
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：system/05_models.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_category_type: 3 row(s), upsert by category_type_code

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'equipment', '设备分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'equipment_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'pipeline', '管线分类',
  '从 system_category 同步的分类维度', 1,
  NULL, 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'region', '区域分类',
  '油库站场 Region 分类树（Pattern C）', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'region_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- dynamic_category: 54 row(s), upsert by code; parent by parent_code

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '电力管线', 'CAT-7e0b3f23b59d4ec6baca7b58b16bc89a',
  'pipeline', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '按钮箱', 'CAT-84a8075ee569476aba6b24617c06d119',
  'equipment', 15,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '设备类', 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b',
  'equipmentt', 3,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '温湿度检测仪', 'CAT-9ea830ae31e84d10bbc016996dae3f98',
  'equipment', 11,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '控制箱', 'CAT-ab0b8858ff474ece95b59fa45c39bcbd',
  'equipment', 13,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '氧气检测仪', 'CAT-da780bb53e264b469803bbaa6dd6eddd',
  'equipment', 10,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, 'equipment_root', 'equipment_root',
  'equipment', 0,
  1, 'auto recovered missing parent', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, 'region_root', 'region_root',
  'region', 0,
  1, 'auto recovered missing parent', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '光纤收发器', 'CAT-04d9a56142114da1bbf7be1f014033e3',
  'equipment', 3,
  1, 'DW5OLtD607ooM0IwEzmFUQAv9XOWnlu2yI4HQeR0v84RkRZlnQuPSTZ/2OD+oybBYfFJEg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '安防系统', 'CAT-0e10a00013d1474da7fb182eaef57f99',
  'equipment', 1,
  1, '7lpOBWaveSBHB5zywj2WKJpLT0X78m1nU24loG1smy8xVGSzTgOq1MJ/Teo3mxRozM6bxiTaC3LsKr34sHOoe6uguvaG/kUrDrBPTDxekA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '电力与配电系统', 'CAT-1cb18a0a06d347b28e0d140e6bbdfcf3',
  'equipment', 10,
  1, 'hXi34ckvCyYsjStkSyRVD7LVR6pX8u+iKY7VCq0ZSeOYoY5bX9lh7LMbaZBzNcEE3QXfmuW7nZ8daw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '消防系统', 'CAT-2856506514844d17900faaa00376cb56',
  'equipment', 12,
  1, 'gWJ5ro+iodu6gsk8XlgjR9mYL5oLd/UCvw5fPTZAGbXcHuspiXGW9Si87cvYIT/VlwsSFpOlupAPnoxg6XnxsCuzdA51Gg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '人员定位系统', 'CAT-335c10c8157f48248bf72de3cd4870e8',
  'equipment', 5,
  1, '7tuub2/Scy5g+Gq3S446PphMw0xp7V0CRINdo7v3romtpSHavNgnNvfvbo7nQPOIoru6EA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '无线对讲系统', 'CAT-830d64d0d51440bdb0206722d9718532',
  'equipment', 4,
  1, '2YOhto6nwxz9P9KYEhoORoKhhTOtc9VmfiReKXuQeZ/xYNlv9QlxYPSqbqYQog==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '视频监控与安防', 'CAT-85feb1443e8f42afb1a84754e7d7ce5f',
  'equipment', 13,
  1, '9oHoG8mt8CrGPj6LHvMf5lBWp4q40dpTc4iDX4IUXJReLcpcKP8d0TcW9QQ5FOAZHS2wZ3rHNTj9IjPw6A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '环控系统', 'CAT-a61e8a70173249e28002b05eaec9f438',
  'equipment', 6,
  1, 'I7hdUBpTh763+yapHlt9RLrrm3h8+B6qewl2vOBb7NjScV31AWeQdbBISiBoWA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '通信与网络', 'CAT-e0cd4f3eecb64831b7f99e166be95bf6',
  'equipment', 14,
  1, 'qPFA4+LV+ENcYOcEdVYqL7Qqwd2/T6fNe6B/iCHuQ3llFJhUCUKQh5IplWU7ookfGIzHSI8LYnRbmkICTrhYVg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '环境与检测', 'CAT-eb4575c0015b405891a8d45565598a80',
  'equipment', 15,
  1, 'BeNThMjULIAPmhgoJc8wKMO7hkswVkh01uAcXht32+tsJX6QSxQcYpUT0KqSNk0USA3t8lwpvYg3rTZMgSJFJs/phQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), 'IP电话系统', 'CAT-eea3d5ebafd543b0b6f72a2f76a42cd6',
  'equipment', 2,
  1, 'SFjnvKNLjq+upL1qC8eGQSL6VI/iOL1+tMWOyq3xCvGUeJvx8z38HhePPW7qVs3VA/JApw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '光谷五路北', 'CAT-f2861f0d6ac24813bcc35be1269538d9',
  'region', 1,
  1, 'GdAFDR9FkD4mBs6hVPFnNXR9z93gqJEU5WZM8AShj71efl+kEYxvVgsOHMzyoWmmqVsC3VzBMg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '通风与给排水系统', 'CAT-f80e2048b994430a936090f9d3e1df23',
  'equipment', 11,
  1, 'Hc8e/gTwCku3w4PVvKfEzGyRI4m7O5EMPAKTa5O6tTfxWOoO23g045WyQou30tvYAHtMUdorpjjMog==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), 'LED显示屏', 'CAT-0588c6878c894a62b541f1159ae0a021',
  'equipment', 5,
  1, 'BPpmppLthrKdJR/x/YDBfiw2A7nPBWz6LeE4osIxK0aLgejvgSM3Wu5bCfCuKFDGlQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '网络摄像机', 'CAT-0e61fad294274a4ea28e863d0cd97517',
  'equipment', 3,
  1, 'e7xALxYzBYlTzLC1gPXOV1J+gEkh04VZoqxu4fPsKA96gHenHQu+klrW8Hpb7EPBQqUUHA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '监控控制柜', 'CAT-185a3ec231b745aa865bb57d1407cc55',
  'equipment', 4,
  1, 'CfPPUwYHXc+4uaDpruCE3kMgVkPSvsTmNCnC0DCo7/pyvwtjeVLwLMhc6vAwXWAhxw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-830d64d0d51440bdb0206722d9718532' LIMIT 1), '无线远端机', 'CAT-1c7a22b5811d4059a6ed562b028f0c9f',
  'equipment', 2,
  1, 'yaVkkS8B64qnLLz9l+mc47Wq+kTSnUrpTexgdHc23S6XQq2QXn9Ng20xN7aq68/F0Pfw2PoqWRP04w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-eb4575c0015b405891a8d45565598a80' LIMIT 1), '检修箱', 'CAT-1ca47692b77749fbb03abfe8fe15d23b',
  'equipment', 2,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '红外探测器', 'CAT-1d053d73e166452cb2e118c5f74e1d02',
  'equipment', 6,
  1, 'fyhihs3BQJkQOXQTgOzxmKTc6LVFuLO5A9bhze9l/s//LLGK5lQipM4VEZM+IABes6aVqg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), '温湿度传感器', 'CAT-1dba544cfa114d5d98513c92280cd58a',
  'equipment', 2,
  1, '/i0PxW044ZLlQ5mjzJaAri85QrcqU9KhsMWZNxKkdW/q0f3YU56p+YG0rkYeqJ3P7w6bM9Dg3A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-830d64d0d51440bdb0206722d9718532' LIMIT 1), '定向天线', 'CAT-4b2affb3ea004405b48a723eed987835',
  'equipment', 3,
  1, 'HuPbsS8HgoTO5Q3vEYc5TUmwUaYPRYmiLfcNFq2mRYnPv11ZMNX+J0cqj2YauONE5BShtQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-eb4575c0015b405891a8d45565598a80' LIMIT 1), '传感器类', 'CAT-8b78a09ac93047919711c911c7bfb706',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1cb18a0a06d347b28e0d140e6bbdfcf3' LIMIT 1), '照明', 'CAT-a7052a7e383b4c0d93548a12a8963246',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-335c10c8157f48248bf72de3cd4870e8' LIMIT 1), '人员定位主机', 'CAT-b7edb01fec184cb38bcd1a47e2aa7552',
  'equipment', 1,
  1, 'JOMYSPKUqtm/YFV/vNdbkjz5obMaw+WwRCkp28M1EsSZr9Lsu8I7ryhP7k5TtHIZgp9DvA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-2856506514844d17900faaa00376cb56' LIMIT 1), '气溶胶', 'CAT-b8f4fbb9236549dca1dc05d637c77cf8',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '门禁控制器', 'CAT-bb4e3d114d69414bb0583c3ad88ce8da',
  'equipment', 1,
  1, 'GoWKRF3LLdfwrqj21hRjWtPm6uNAxrsgEupg9aAF0V3tCVPHXmCLu7lgFVb5bgjsKUyujg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), '二氧化碳传感器', 'CAT-c116ab6c252b41fbb0cb8d593fb1cc5c',
  'equipment', 4,
  1, '10rOUmrusDMK4oTM1ZY/Du7rAuJpnBn3o17lG3Qe0WXOYiPRqUZcGhI3ywGcFsX1lh17mm7YIiOUauVf0A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-335c10c8157f48248bf72de3cd4870e8' LIMIT 1), '移动终端', 'CAT-c3826e005fd34127a0c10ac16c988cb8',
  'equipment', 2,
  1, '3lNBawQRUFcmwTYZmlvpKh7/rK6GUMU9VKWEaMeq5uXxk38ExSQKQLlXHgLqssJRHiRd9zBgrDpgjg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-830d64d0d51440bdb0206722d9718532' LIMIT 1), '功分器', 'CAT-d13851574c684b91af4a2e641cd352be',
  'equipment', 1,
  1, '4/3noQfE6yuMMqSDDRuIzxWo+ZHmVf8KZwk2TQU0Y5cyeOP/1ZxHXZExkRFAreaaxg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-e0cd4f3eecb64831b7f99e166be95bf6' LIMIT 1), '网络设备', 'CAT-df84bc4e4c3d48be9ed1e4a1f075de56',
  'equipment', 1,
  1, 'ZvPc8V/zzLwmD/oHkTwkCHpU0tVjOHnMQN/8qCOyKK6oaSbWHquk4AQv82BpeSS2Cu8B8fH6WkQfmeBRwA2g3g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), 'ACU柜', 'CAT-e2f08e3a32bc43a9afb9a2207b3d62f6',
  'equipment', 1,
  1, 'tZ6AZUbpOW5A/hKsluJLwxuHM5r+sddoxPsPXENOk31BsvWtM7kjC5ijHNZvtsg6kg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f80e2048b994430a936090f9d3e1df23' LIMIT 1), '水泵', 'CAT-ee1126c3a9814693b048d195da938fde',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '报警主机', 'CAT-ee163d552060451380f778c6f74b7b18',
  'equipment', 2,
  1, 'Vn/hY+BtrbfGUxRgQcnB0Zx7KKTxwx2A0fzrQAvHN0bAagCUfExNPOkZcd6FctQIhNVO1Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), '氧气传感器', 'CAT-f1640b756fc2427595b93b21f89ec99d',
  'equipment', 3,
  1, 'MB5xoQZ4N58L9QpF8HRFUWXnrW5Cg/CWBz7B5wuH6KCo8Ktk7KdqkpY7zg58VDdK6a1AOe0wiw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '1#防火区', 'Region.GG5LB.01',
  'region', 1,
  1, 'tFyK2fdgnXbtxN4gc2am3hfUykT10FDI8aelaeN5XbaLT4IMFq77/O7ltq3rJbxRj5oY', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '2#防火区', 'Region.GG5LB.02',
  'region', 2,
  1, 'BX1bx73VcYjPnJf2127Jid0/SY6bZsXH1szaNELmPeQpnDcHvMEMEM2c9b4VbDe31l0t', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑3', '441D9A0546A534DC6CF8F5A66D18281F',
  'region', 4,
  1, 'zxCmojI2bGVLsTlSqinb1NOls1YcJZqhK0ieHUtc2TpV0Z98tEq5qv5wNmBVH//Yc6I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑4', '673CEA074537EE75356C72A2F418B00E',
  'region', 5,
  1, 'aAxjdD2T4Y1kNToZFJtemeMXcH177+S94OQdtFY8r8bpULKcLk7Iw858J6ZDZqQ3wkE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '管道舱', 'CAT-0bc59e07fc2a40f9aa7f3e09958c408e',
  'region', 2,
  1, 'ERvrNyFK4RfLara4JyiqCqw6hVIfWPYEHMoEfkiSMcdE5iGxgb16ZrygLts7BQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '高压电力舱', 'CAT-5227d7cd16be406dae106bfd0b693c43',
  'region', 3,
  1, 'LCu65EG9XmycpLehBA71XBEGSTs+USPE+gGL7twGNvZInSOx79oOSQK/YqIc3UvYxN9oxg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '高压电力舱', 'CAT-6bd7aee06b5d449cbee8cdd72825ed5b',
  'region', 3,
  1, 'P6VbKKO9sWJto1vALdAs0UmykaVkVCpwedRav0nTiRGiJ/UDRuwipLkk9IymLvNPibwh1Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '管道舱', 'CAT-7a291d756d4c47609c9c0a0b8c8988d5',
  'region', 2,
  1, 'NdrqSDpPa1DNHRoyyVWuG8X8tb2gzh4/js7xXC0Y3HjSQZgPcgpaBrRxaPSGxQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '电力信息舱', 'CAT-8c7626c3866f41cbb9593b2a11aaa96a',
  'region', 1,
  1, 'rwx2XDw9euF75Im17XKJrKdCC/11ua1ddKv/zFlGgGkfXahlRAJB5jvkdjpX7pZa3d9e+g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '电力信息舱', 'CAT-c38542aa94f545d6a814413a62dbb0f4',
  'region', 1,
  1, 'HlbsjWrX+tIuFGxhdXwS5xco7qgqX2MyclJ6MGWezgkhJJw8IRtwqo1TndXbQ18LNhep5A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-df84bc4e4c3d48be9ed1e4a1f075de56' LIMIT 1), '环网交换机', 'CAT-e8ceb6ea3eba4b7399f654174ac1c5fa',
  'equipment', 1,
  1, 'FBIs+ICepypK5COWFBbvrXf1AxbrWxBNExK15Y25TJCWfnlbc1Srbr03oDDx/v0cUw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑5', 'F524197C4F760718A5A5B88853D5529B',
  'region', 6,
  1, 'rD5gGzVP8BiodDf26XyFke02i9lQHyqmE9TfgQ9zWdCE7/zPbj5GfVjqGIgpdF/9D0E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- rebuild tree_path / level after category upsert (id-agnostic)
WITH RECURSIVE cat_tree AS (
  SELECT c.id, c.code, c.parent_id, ARRAY[c.id] AS path_ids, 0 AS lvl
  FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1
    AND (c.parent_id IS NULL OR c.parent_id = 0
         OR NOT EXISTS (
           SELECT 1 FROM dynamic_category p
           WHERE p.id = c.parent_id AND p.deleted = false AND p.tenant_id = 1
         ))
  UNION ALL
  SELECT c.id, c.code, c.parent_id, ct.path_ids || c.id, ct.lvl + 1
  FROM dynamic_category c
  JOIN cat_tree ct ON c.parent_id = ct.id
  WHERE c.deleted = false AND c.tenant_id = 1
)
UPDATE dynamic_category c
SET
  tree_path = '/' || array_to_string(ct.path_ids, '/') || '/',
  level = ct.lvl + 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM cat_tree ct
WHERE c.id = ct.id;


-- dynamic_model_category_relation: 48 row(s), resolve by model_code + category_code

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND c.code = 'CAT-0e61fad294274a4ea28e863d0cd97517'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND c.code = 'CAT-ee163d552060451380f778c6f74b7b18'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 5, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND c.code = 'CAT-1d053d73e166452cb2e118c5f74e1d02'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 400, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND c.code = 'CAT-bb4e3d114d69414bb0583c3ad88ce8da'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND c.code = 'CAT-0588c6878c894a62b541f1159ae0a021'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND c.code = 'CAT-185a3ec231b745aa865bb57d1407cc55'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND c.code = 'CAT-eea3d5ebafd543b0b6f72a2f76a42cd6'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND c.code = '441D9A0546A534DC6CF8F5A66D18281F'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND c.code = '673CEA074537EE75356C72A2F418B00E'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND c.code = 'CAT-04d9a56142114da1bbf7be1f014033e3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND c.code = 'CAT-d13851574c684b91af4a2e641cd352be'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND c.code = 'CAT-1c7a22b5811d4059a6ed562b028f0c9f'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND c.code = 'CAT-4b2affb3ea004405b48a723eed987835'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND c.code = 'CAT-b7edb01fec184cb38bcd1a47e2aa7552'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND c.code = 'CAT-c3826e005fd34127a0c10ac16c988cb8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND c.code = 'CAT-e2f08e3a32bc43a9afb9a2207b3d62f6'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND c.code = 'CAT-1dba544cfa114d5d98513c92280cd58a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 400, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND c.code = 'CAT-8b78a09ac93047919711c911c7bfb706'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND c.code = 'CAT-f1640b756fc2427595b93b21f89ec99d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND c.code = 'F524197C4F760718A5A5B88853D5529B'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 7, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND c.code = 'CAT-c116ab6c252b41fbb0cb8d593fb1cc5c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 5, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND c.code = 'CAT-8c7626c3866f41cbb9593b2a11aaa96a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND c.code = 'CAT-7a291d756d4c47609c9c0a0b8c8988d5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND c.code = 'CAT-6bd7aee06b5d449cbee8cdd72825ed5b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND c.code = 'CAT-c38542aa94f545d6a814413a62dbb0f4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND c.code = 'CAT-0bc59e07fc2a40f9aa7f3e09958c408e'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND c.code = 'CAT-5227d7cd16be406dae106bfd0b693c43'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND c.code = 'CAT-e8ceb6ea3eba4b7399f654174ac1c5fa'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 3, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND c.code = 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND c.code = 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND c.code = 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 300, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND c.code = 'CAT-8b78a09ac93047919711c911c7bfb706'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'custom_6128', 3, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-0e171b9d21024fd183a1cb355e1b8c08'
  AND c.code = 'CAT-7e0b3f23b59d4ec6baca7b58b16bc89a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 6, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7fc9012b394749a3b30e1788e4057324'
  AND c.code = 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7fc9012b394749a3b30e1788e4057324'
  AND c.code = 'CAT-1ca47692b77749fbb03abfe8fe15d23b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 8, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1966e63fec48402f9b60280e66faea0f'
  AND c.code = 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1966e63fec48402f9b60280e66faea0f'
  AND c.code = 'CAT-a7052a7e383b4c0d93548a12a8963246'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3ae58694e5ab4dc4a078c6e4b548f617'
  AND c.code = 'CAT-b8f4fbb9236549dca1dc05d637c77cf8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 5, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a547e7b120434da0bc8d94f11447f75a'
  AND c.code = 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 200, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a547e7b120434da0bc8d94f11447f75a'
  AND c.code = 'CAT-a7052a7e383b4c0d93548a12a8963246'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e1e2ab00b9cb41dab46f48856374ebd4'
  AND c.code = 'CAT-85feb1443e8f42afb1a84754e7d7ce5f'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6671ca830f4742229c0a00ff531f974c'
  AND c.code = 'CAT-2856506514844d17900faaa00376cb56'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 100, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d11cb8bb47e74d73b5d4233814de2a39'
  AND c.code = 'CAT-ee1126c3a9814693b048d195da938fde'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3335affd4a814e4dba54d613d07bb0fa'
  AND c.code = 'CAT-da780bb53e264b469803bbaa6dd6eddd'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8ed7911c3664dafaf4073223792b5a4'
  AND c.code = 'CAT-9ea830ae31e84d10bbc016996dae3f98'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fd9456206abe46ae972a0dc15df082ec'
  AND c.code = 'CAT-ab0b8858ff474ece95b59fa45c39bcbd'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', -600, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bd13c167e66b46bca791fae607c72ad7'
  AND c.code = 'CAT-bb4e3d114d69414bb0583c3ad88ce8da'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9b557c9404684caab2820eb0aec2cf15'
  AND c.code = 'CAT-84a8075ee569476aba6b24617c06d119'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );


-- dynamic_page_config: (empty)
