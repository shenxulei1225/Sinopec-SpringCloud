-- ============================================================
-- 设施数据迁移脚本 V3
-- 从旧系统迁移设施数据到 PostgreSQL（预转换数据）
-- 旧系统: MySQL (pre-dev 数据库)
-- 新系统: PostgreSQL (Spring Cloud)
-- 执行顺序：在 V2__migrate_site_data.sql 之后执行
-- ============================================================

-- ============================================================
-- 分类ID说明：
-- 21: 物理压力表
-- 22: 阀门
-- 23: 罐顶
-- 24: 仪表
-- 25: 管道
-- 26: 罐体
-- 27: 连接处
-- ============================================================

-- ============================================================
-- 站场映射
-- 旧 dept_id=121212 -> 新 site_id=17 (仪征站)
-- ============================================================

-- ============================================================
-- 仪表类设施 (facilities_type = 1) -> category_id = 24 (仪表)
-- ============================================================
INSERT INTO fac_facility (id, facility_code, facility_name, category_id, category_name, site_id, site_name, status, manufacturer, model, location, remark, create_time, update_time, creator, updater, tenant_id, deleted) VALUES
(108, 'XJ-GXG1-L1-1', '仪表', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(109, 'YB-1000-10000-001', '仪表-1', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(111, 'YB-1000-10001-001', '仪表-2', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(113, 'YB-1000-10002-001', '仪表-3', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(115, 'YB-1000-10003-001', '仪表-4', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(117, 'YB-1000-10004-001', '仪表-5', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(119, 'YB-1000-10005-001', '仪表-6', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(121, 'YB-1000-10006-001', '仪表-7', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(123, 'YB-1000-10007-001', '仪表-8', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(125, 'YB-1000-10008-001', '仪表-9', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(127, 'YB-1000-10009-001', '仪表-10', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(129, 'YB-1000-10010-001', '仪表-11', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(131, 'YB-1000-10011-001', '仪表-12', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(135, 'YB-1000-10013-001', '仪表-14', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(137, 'YB-1000-10014-001', '仪表-15', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(139, 'YB-1000-10015-001', '仪表-16', 24, '仪表', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000 仪表类型:2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false);

-- ============================================================
-- 阀门类设施 (facilities_type = 2) -> category_id = 22 (阀门)
-- ============================================================
INSERT INTO fac_facility (id, facility_code, facility_name, category_id, category_name, site_id, site_name, status, manufacturer, model, location, remark, create_time, update_time, creator, updater, tenant_id, deleted) VALUES
(110, 'FM-1000-10000-001', '阀门-1', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(112, 'FM-1000-10001-001', '阀门-2', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(114, 'FM-1000-10002-001', '阀门-3', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(116, 'FM-1000-10003-001', '阀门-4', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(118, 'FM-1000-10004-001', '阀门-5', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(120, 'FM-1000-10005-001', '阀门-6', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(122, 'FM-1000-10006-001', '阀门-7', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(124, 'FM-1000-10007-001', '阀门-8', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(126, 'FM-1000-10008-001', '阀门-9', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(128, 'FM-1000-10009-001', '阀门-10', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(130, 'FM-1000-10010-001', '阀门-11', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(132, 'FM-1000-10011-001', '阀门-12', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(134, 'FM-1000-10012-001', '阀门-13', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(136, 'FM-1000-10013-001', '阀门-14', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(138, 'FM-1000-10014-001', '阀门-15', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(140, 'FM-1000-10015-001', '阀门-16', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(142, 'FM-1000-10016-001', '阀门-17', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(144, 'FM-1000-10017-001', '阀门-18', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(146, 'FM-1000-10018-001', '阀门-19', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(148, 'FM-1000-10019-001', '阀门-20', 22, '阀门', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false);

-- ============================================================
-- 罐体类设施 (facilities_type = 6) -> category_id = 26 (罐体)
-- ============================================================
INSERT INTO fac_facility (id, facility_code, facility_name, category_id, category_name, site_id, site_name, status, manufacturer, model, location, remark, create_time, update_time, creator, updater, tenant_id, deleted) VALUES
(161, 'G-10000', '罐体-1', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(162, 'G-10001', '罐体-2', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(163, 'G-10002', '罐体-3', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(164, 'G-10003', '罐体-4', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(165, 'G-10004', '罐体-5', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(166, 'G-10005', '罐体-6', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(167, 'G-10006', '罐体-7', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(168, 'G-10007', '罐体-8', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(169, 'G-10008', '罐体-9', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(170, 'G-10009', '罐体-10', 26, '罐体', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false);

-- ============================================================
-- 罐顶类设施 (facilities_type = 7) -> category_id = 23 (罐顶)
-- ============================================================
INSERT INTO fac_facility (id, facility_code, facility_name, category_id, category_name, site_id, site_name, status, manufacturer, model, location, remark, create_time, update_time, creator, updater, tenant_id, deleted) VALUES
(171, 'GXG1-100-SS-1001', '罐顶-1', 23, '罐顶', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(172, 'GXG1-100-SS-1002', '罐顶-2', 23, '罐顶', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(173, 'GXG1-100-SS-1003', '罐顶-3', 23, '罐顶', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(174, 'GXG1-100-SS-1004', '罐顶-4', 23, '罐顶', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(175, 'GXG1-100-SS-1005', '罐顶-5', 23, '罐顶', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(176, 'GXG1-100-SS-1006', '罐顶-6', 23, '罐顶', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false);

-- ============================================================
-- 管道类设施 (facilities_type = 8) -> category_id = 25 (管道)
-- ============================================================
INSERT INTO fac_facility (id, facility_code, facility_name, category_id, category_name, site_id, site_name, status, manufacturer, model, location, remark, create_time, update_time, creator, updater, tenant_id, deleted) VALUES
(177, 'GXG101-SS-1001', '管道-1', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(178, 'GXG101-SS-1002', '管道-2', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(179, 'GXG101-SS-1003', '管道-3', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(180, 'GXG101-SS-1004', '管道-4', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(181, 'GXG101-SS-1005', '管道-5', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(182, 'GXG101-SS-1006', '管道-6', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(183, 'GXG101-SS-1007', '管道-7', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(184, 'GXG101-SS-1008', '管道-8', 25, '管道', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(185, 'GXG101-SS-1009', '管道-9', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(186, 'GXG101-SS-1010', '管道-10', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(187, 'GXG101-SS-1011', '管道-11', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(188, 'GXG101-SS-1012', '管道-12', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(189, 'GXG101-SS-1013', '管道-13', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(190, 'GXG101-SS-1014', '管道-14', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(191, 'GXG101-SS-1015', '管道-15', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(192, 'GXG101-SS-1016', '管道-16', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(193, 'GXG101-SS-1017', '管道-17', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(194, 'GXG101-SS-1018', '管道-18', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(195, 'GXG101-SS-1019', '管道-19', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false),
(196, 'GXG101-SS-1020', '管道-20', 25, '管道', 17, '仪征站', 0, NULL, NULL, '北罐组', '分区:YZKQ-01 罐组:GZ-1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false);

-- ============================================================
-- 连接处类设施 (facilities_type = 3) -> category_id = 27 (连接处)
-- ============================================================
INSERT INTO fac_facility (id, facility_code, facility_name, category_id, category_name, site_id, site_name, status, manufacturer, model, location, remark, create_time, update_time, creator, updater, tenant_id, deleted) VALUES
(159, 'XJ-GXG1-100-cr', '连接处-1', 27, '连接处', 17, '仪征站', 0, NULL, NULL, '南罐组', '分区:YZKQ-01 罐组:GZ-1000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', 1, false);

-- ============================================================
-- 验证迁移结果
-- ============================================================
-- SELECT category_name, COUNT(*) AS count FROM fac_facility WHERE tenant_id = 1 AND deleted = false GROUP BY category_name ORDER BY count DESC;
-- SELECT COUNT(*) FROM fac_facility WHERE tenant_id = 1 AND deleted = false;
