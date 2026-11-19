-- 金桥厂区的两起应急事件没有中心点。位置用这座场站已经记下的经纬度。
-- 不改所属管线段。管线段由地图按坐标去对最近的场站。
-- 打开页面不再补坐标。
SET search_path TO dynamicbusiness;

UPDATE ent_emergency_t1 AS event
SET longitude = facility.longitude,
    latitude = facility.latitude,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
FROM ent_facility_t1 AS facility
WHERE event.deleted = false
  AND facility.deleted = false
  AND event.facility_id = facility.id
  AND facility.id = 44
  AND event.longitude IS NULL
  AND event.latitude IS NULL
  AND facility.longitude IS NOT NULL
  AND facility.latitude IS NOT NULL;
