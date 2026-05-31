-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.71
-- 日期: 2026-03-17
-- 描述: 初始化 Ref 约束器库
-- =====================================================

-- 人员业务：角色 + 部门
INSERT INTO dynamic_ref_constraint_library
(business_type_code, ref_target_type, constraint_type, constraint_name, status, sort, tenant_id)
SELECT 'personnel', 'personnel', 'ROLE_DEPT', '角色+部门', 0, 0, 0
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_ref_constraint_library
    WHERE business_type_code = 'personnel'
      AND ref_target_type = 'personnel'
      AND constraint_type = 'ROLE_DEPT'
      AND deleted = FALSE
);

-- 设备业务：设备类型（Category）
INSERT INTO dynamic_ref_constraint_library
(business_type_code, ref_target_type, constraint_type, constraint_name, status, sort, tenant_id)
SELECT 'equipment', 'equipment', 'EQUIPMENT_CATEGORY', '设备类型', 0, 0, 0
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_ref_constraint_library
    WHERE business_type_code = 'equipment'
      AND ref_target_type = 'equipment'
      AND constraint_type = 'EQUIPMENT_CATEGORY'
      AND deleted = FALSE
);

-- 组织业务：无约束器（NONE）
INSERT INTO dynamic_ref_constraint_library
(business_type_code, ref_target_type, constraint_type, constraint_name, status, sort, tenant_id)
SELECT 'dept', 'dept', 'NONE', '无', 0, 0, 0
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_ref_constraint_library
    WHERE business_type_code = 'dept'
      AND ref_target_type = 'dept'
      AND constraint_type = 'NONE'
      AND deleted = FALSE
);
