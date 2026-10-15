-- 门户中文名下面的英文。智慧场站平台改成智慧场站。

ALTER TABLE system_tenant
    ADD COLUMN IF NOT EXISTS portal_name_en varchar(64);

COMMENT ON COLUMN system_tenant.portal_name_en IS '门户英文名。显示在中文名称下面';

UPDATE system_tenant
SET portal_name = '智慧场站',
    portal_name_en = 'INTELLIGENT STATION',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE id = 1
  AND deleted = false;

UPDATE system_tenant
SET portal_name = '智慧管廊',
    portal_name_en = 'INTELLIGENT GALLERY',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE id = 123
  AND deleted = false;
