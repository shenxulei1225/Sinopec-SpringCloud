-- ============================================================
-- 设施管理模块示例数据
-- ============================================================

-- 站场类型基础数据 (tenant_id = 1)
INSERT INTO fac_site_type (id, type_code, type_name, description, sort_no, status, remark, create_time, update_time, creator, updater, deleted, tenant_id) VALUES
(1, 'CR_FSC', '原油分输站', '原油管道分输站，用于原油的分输和转运', 1, 0, '原油类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(2, 'CR_FWS', '原油阀室', '原油管道阀室，用于管道截断和监控', 2, 0, '原油类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(3, 'CR_JSZ', '原油接收站', '原油接收站，用于接收和储存原油', 3, 0, '原油类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(4, 'CP_FSC', '成品油分输站', '成品油管道分输站，用于成品油的分输和转运', 4, 0, '成品油类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(5, 'CP_FWS', '成品油阀室', '成品油管道阀室，用于管道截断和监控', 5, 0, '成品油类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(6, 'CP_JSZ', '成品油接收站', '成品油接收站，用于接收和储存成品油', 6, 0, '成品油类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(7, 'NG_FSC', '天然气分输站', '天然气管道分输站，用于天然气的分输和调压', 7, 0, '天然气类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(8, 'NG_FWS', '天然气阀室', '天然气管道阀室，用于管道截断和监控', 8, 0, '天然气类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1),
(9, 'NG_JSZ', '天然气接收站', '天然气接收站，用于接收和处理天然气', 9, 0, '天然气类站场', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', false, 1);

-- 站场示例数据（node_type=1分组不需要站场类型，node_type=2站场需要分配站场类型）
INSERT INTO fac_site (site_id, site_code, site_name, parent_id, node_type, site_type_id, level, path, status, owner_user_id, creator, updater, create_time, update_time, tenant_id) VALUES
(1, 'SiteReagion_1', '国家管网集团控制中心', 0, 1, NULL, 1, '/1', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(2, 'SiteReagion_6', '东部储运控制中心', 1, 1, NULL, 2, '/1/2', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(4, 'DEPT_121228', '山东分公司控制中心', 2, 1, NULL, 2, '/1/2/4', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(5, 'SiteReagion_2', '西南分公司控制中心', 1, 1, NULL, 2, '/1/5', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(6, 'HZ_FGS', '杭州分公司', 5, 1, NULL, 3, '/1/5/6', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(12, 'SiteReagion_3', '绵羊库', 6, 2, 3, 3, '/1/5/6/12', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(14, 'SiteReagion_4', '眉山库', 6, 2, 3, 3, '/1/5/6/14', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(17, 'SiteReagion_5', '仪征站', 6, 2, 1, 3, '/1/5/6/17', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(121212, 'JQ_FACTORY', '金桥厂区', 6, 2, 3, 3, '/1/5/6/121212', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(121229, 'QD_OIL_DEPOT', '黄岛油库', 6, 2, 3, 3, '/1/5/6/121229', 0, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

