-- 导入站场示例数据
INSERT INTO sys_site (site_id, site_code, site_name, parent_id, node_type, level, path, status, owner_user_id, creator, updater) VALUES
(1, 'SiteReagion_1', '国家管网集团控制中心', 0, 1, 1, '/1', 0, 1, 'system', 'system'),
(2, 'SiteReagion_6', '东部储运控制中心', 1, 1, 2, '/1/2', 0, 1, 'system', 'system'),
(4, 'DEPT_121228', '山东分公司控制中心', 2, 1, 2, '/1/2/4', 0, 1, 'system', 'system'),
(5, 'SiteReagion_2', '西南分公司控制中心', 1, 1, 2, '/1/5', 0, 1, 'system', 'system'),
(6, '杭州分公司', '杭州分公司', 5, 1, 3, '/1/5/6', 0, 1, 'system', 'system'),
(12, 'SiteReagion_3', '绵羊库', 6, 2, 3, '/1/5/6/12', 0, 1, 'system', 'system'),
(14, 'SiteReagion_4', '眉山库', 6, 2, 3, '/1/5/6/14', 0, 1, 'system', 'system'),
(17, 'SiteReagion_5', '仪征站', 6, 2, 3, '/1/5/6/17', 0, 1, 'system', 'system'),
(121212, 'DEPT_121212', '金桥厂区', 6, 2, 3, '/1/5/6/121212', 0, 1, 'system', 'system'),
(121229, 'DEPT_121229', '黄岛油库', 6, 2, 3, '/1/5/6/121229', 0, 1, 'system', 'system');
