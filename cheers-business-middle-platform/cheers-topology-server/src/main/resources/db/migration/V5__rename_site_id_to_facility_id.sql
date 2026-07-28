-- site_id → facility_id（设施编号）；索引随列重命名
ALTER TABLE platform_topology_graph RENAME COLUMN site_id TO facility_id;
