-- 数据管理里的车辆目录分成两组。
-- 车辆登记：个人车辆、公司车辆、车辆调度。
-- 车辆进出管理：车辆进出、车道。
-- 不新建目录。外来车仍写在车辆进出账上。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_entity_type
SET group_name = '车辆登记',
    name = '个人车辆',
    alias = '个人车辆',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'vehicle_list';

UPDATE dynamicbusiness.dynamic_entity_type
SET group_name = '车辆登记',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code IN ('company_vehicle', 'vehicle_dispatch');

UPDATE dynamicbusiness.dynamic_entity_type
SET group_name = '车辆进出管理',
    name = '车辆进出',
    alias = '车辆进出',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'vehicle_pass';

UPDATE dynamicbusiness.dynamic_entity_type
SET group_name = '车辆进出管理',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'vehicle_lane';
