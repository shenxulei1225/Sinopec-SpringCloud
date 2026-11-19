-- 删除授权分配表和权限维度列。数据范围只由任职决定，管理单元目录由可管目录登记推出。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.1、2.2、2.5 节。
--
-- 前提：原 44 条授权已按管线段所属作业区通过任职接口迁成任职（职务「站长」），后端已不再读写这两处。
-- 本迁移只删结构，不迁数据。
SET search_path TO dynamicbusiness;

DROP TABLE IF EXISTS dynamic_grant_assignment;
DROP SEQUENCE IF EXISTS dynamic_grant_assignment_id_seq;

ALTER TABLE dynamic_entity_type DROP COLUMN IF EXISTS permission_dimension;
