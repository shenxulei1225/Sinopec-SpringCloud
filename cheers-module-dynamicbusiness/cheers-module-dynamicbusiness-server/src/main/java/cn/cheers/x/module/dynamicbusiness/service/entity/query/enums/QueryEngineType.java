package cn.cheers.x.module.dynamicbusiness.service.entity.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查询引擎类型枚举
 *
 * 定义系统支持的查询引擎类型
 *
 * @author 系统
 */
@Getter
@AllArgsConstructor
public enum QueryEngineType {

    /**
     * PostgreSQL 查询引擎
     * 使用 JSONB + GIN 索引进行等值查询
     * 使用 entity_field_index 索引表进行范围查询和排序
     */
    POSTGRESQL("postgresql", "PostgreSQL", 1),

    /**
     * MySQL 查询引擎
     * 使用 entity_field_index 索引表进行所有查询
     */
    MYSQL("mysql", "MySQL", 2),

    /**
     * Elasticsearch 查询引擎
     * 使用 ES 索引进行全文搜索和复杂查询
     */
    ELASTICSEARCH("elasticsearch", "Elasticsearch", 3);

    /**
     * 引擎类型编码
     */
    private final String code;

    /**
     * 引擎类型名称
     */
    private final String name;

    /**
     * 默认优先级
     * 数值越小优先级越高
     */
    private final int defaultPriority;

    /**
     * 根据编码获取引擎类型
     *
     * @param code 引擎类型编码
     * @return 引擎类型枚举，如果未找到返回 POSTGRESQL（默认值）
     */
    public static QueryEngineType getByCode(String code) {
        if (code == null) {
            return POSTGRESQL;
        }
        for (QueryEngineType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return POSTGRESQL;
    }

    /**
     * 判断是否支持 JSONB 原生查询
     *
     * @return 是否支持 JSONB 原生查询
     */
    public boolean supportsJsonbQuery() {
        return this == POSTGRESQL;
    }

    /**
     * 判断是否需要索引表
     *
     * @return 是否需要索引表
     */
    public boolean requiresIndexTable() {
        return this == POSTGRESQL || this == MYSQL;
    }

    /**
     * 判断是否支持全文搜索
     *
     * @return 是否支持全文搜索
     */
    public boolean supportsFullTextSearch() {
        return this == ELASTICSEARCH;
    }
}
