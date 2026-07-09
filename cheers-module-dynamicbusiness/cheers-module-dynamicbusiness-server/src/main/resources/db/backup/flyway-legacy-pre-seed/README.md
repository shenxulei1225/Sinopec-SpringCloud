# 历史 Flyway 脚本（已停用）

本目录为 **dynamicbusiness** 旧版增量迁移与 zhgl 导入脚本归档，**不参与**应用启动时的 Flyway 执行。

当前有效链（classpath `db/migration/dynamicbusiness/`）仅：

- V1__init_dynamicbusiness_schema.sql
- V2__model_field_assignment_codes.sql
- V3__cross_platform_association_codes.sql

含 V2__seed_*、V4–V27 等文件仅供历史对照，勿放回 migration 目录。
