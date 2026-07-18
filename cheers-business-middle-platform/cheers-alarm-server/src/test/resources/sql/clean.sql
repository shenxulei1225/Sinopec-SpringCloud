-- =====================================================
-- 告警管理模块测试数据清理脚本 (H2)
-- 每个测试方法执行后清理数据
-- =====================================================

DELETE FROM alarm_audit_log;
DELETE FROM linkage_execution;
DELETE FROM linkage_rule;
DELETE FROM alarm_rule;
DELETE FROM alarm_attachment;
DELETE FROM alarm;
