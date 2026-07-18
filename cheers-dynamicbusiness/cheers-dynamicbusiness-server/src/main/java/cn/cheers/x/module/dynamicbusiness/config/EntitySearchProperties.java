package cn.cheers.x.module.dynamicbusiness.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Entity 扩展字段查询配置属性
 * 
 * <p>配置项说明：</p>
 * <ul>
 *   <li>type: 查询引擎类型（postgresql/mysql/es）</li>
 *   <li>maxResults: 单次查询最大返回记录数</li>
 *   <li>maxConditions: 单次查询最大条件数</li>
 *   <li>queryTimeout: 查询超时时间（秒）</li>
 *   <li>sync: 数据同步相关配置</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-ENG-001: 查询引擎通过配置 cheers.entity.search.type 切换</li>
 *   <li>BR-QRY-002: 单次查询最多返回 1000 条记录</li>
 *   <li>BR-QRY-003: 查询条件最多 10 个</li>
 *   <li>BR-QRY-004: 统计查询超时时间 30 秒</li>
 * </ul>
 * 
 * <h3>配置示例</h3>
 * <pre>
 * yudao:
 *   entity:
 *     search:
 *       type: postgresql
 *       max-results: 1000
 *       max-conditions: 10
 *       query-timeout: 30
 *       sync:
 *         enabled: true
 *         async: true
 *         max-retry-count: 3
 *         alert-threshold: 10
 * </pre>
 * 
 * @author 扩展字段查询服务
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "cheers.entity.search")
public class EntitySearchProperties {

    /**
     * 查询引擎类型
     * 
     * <p>支持的值：</p>
     * <ul>
     *   <li>postgresql - PostgreSQL JSONB + GIN 索引方案</li>
     *   <li>mysql - MySQL entity_field_index 索引表方案</li>
     *   <li>es - Elasticsearch 方案</li>
     * </ul>
     * 
     * @see EngineType
     */
    @NotNull(message = "查询引擎类型不能为空")
    private EngineType type = EngineType.POSTGRESQL;

    /**
     * 单次查询最大返回记录数
     * 
     * <p>业务规则 BR-QRY-002：单次查询最多返回 1000 条记录</p>
     */
    @Min(value = 1, message = "最大返回记录数不能小于 1")
    @Max(value = 10000, message = "最大返回记录数不能超过 10000")
    private Integer maxResults = 1000;

    /**
     * 单次查询最大条件数
     * 
     * <p>业务规则 BR-QRY-003：查询条件最多 10 个</p>
     */
    @Min(value = 1, message = "最大条件数不能小于 1")
    @Max(value = 50, message = "最大条件数不能超过 50")
    private Integer maxConditions = 10;

    /**
     * 查询超时时间（秒）
     * 
     * <p>业务规则 BR-QRY-004：统计查询超时时间 30 秒</p>
     */
    @Min(value = 1, message = "查询超时时间不能小于 1 秒")
    @Max(value = 300, message = "查询超时时间不能超过 300 秒")
    private Integer queryTimeout = 30;

    /**
     * 数据同步配置
     */
    private SyncProperties sync = new SyncProperties();

    /**
     * 索引配置
     */
    private IndexProperties index = new IndexProperties();

    /**
     * 查询引擎类型枚举
     */
    public enum EngineType {
        /**
         * PostgreSQL JSONB + GIN 索引方案
         * 
         * <p>特点：</p>
         * <ul>
         *   <li>等值/包含查询使用 JSONB + GIN 索引</li>
         *   <li>范围查询、排序使用 entity_field_index 索引表</li>
         * </ul>
         */
        POSTGRESQL("postgresql"),

        /**
         * MySQL entity_field_index 索引表方案
         * 
         * <p>特点：</p>
         * <ul>
         *   <li>所有查询统一使用 entity_field_index 索引表</li>
         * </ul>
         */
        MYSQL("mysql"),

        /**
         * Elasticsearch 方案
         * 
         * <p>特点：</p>
         * <ul>
         *   <li>支持全文搜索</li>
         *   <li>支持单节点部署</li>
         * </ul>
         */
        ES("es");

        private final String value;

        EngineType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * 根据值获取枚举
         */
        public static EngineType fromValue(String value) {
            for (EngineType type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("不支持的查询引擎类型: " + value);
        }
    }

    /**
     * 数据同步配置
     */
    @Data
    public static class SyncProperties {

        /**
         * 是否启用数据同步
         */
        private Boolean enabled = true;

        /**
         * 是否异步同步
         * 
         * <p>业务规则 BR-SYN-001：数据同步采用异步方式，不阻塞主业务</p>
         */
        private Boolean async = true;

        /**
         * 最大重试次数
         * 
         * <p>业务规则 BR-SYN-002：同步失败自动重试</p>
         */
        @Min(value = 0, message = "最大重试次数不能小于 0")
        @Max(value = 10, message = "最大重试次数不能超过 10")
        private Integer maxRetryCount = 3;

        /**
         * 告警阈值（连续失败次数）
         * 
         * <p>业务规则 BR-SYN-003：连续失败超过 10 次触发告警</p>
         */
        @Min(value = 1, message = "告警阈值不能小于 1")
        @Max(value = 100, message = "告警阈值不能超过 100")
        private Integer alertThreshold = 10;

        /**
         * 初始重试间隔（毫秒）
         */
        private Long initialRetryInterval = 1000L;

        /**
         * 最大重试间隔（毫秒）
         */
        private Long maxRetryInterval = 30000L;

        /**
         * 重试间隔乘数
         */
        private Double retryMultiplier = 5.0;
    }

    /**
     * 索引配置
     */
    @Data
    public static class IndexProperties {

        /**
         * 索引清理延迟天数
         * 
         * <p>业务规则 BR-IDX-002：字段取消可查询标记时延迟清理索引（保留 7 天）</p>
         */
        @Min(value = 0, message = "索引清理延迟天数不能小于 0")
        @Max(value = 30, message = "索引清理延迟天数不能超过 30")
        private Integer cleanupDelayDays = 7;

        /**
         * 索引重建批次大小
         */
        @Min(value = 100, message = "索引重建批次大小不能小于 100")
        @Max(value = 10000, message = "索引重建批次大小不能超过 10000")
        private Integer rebuildBatchSize = 1000;

        /**
         * 是否在重建期间允许查询
         * 
         * <p>业务规则 BR-IDX-003：索引重建不影响正常查询</p>
         */
        private Boolean allowQueryDuringRebuild = true;
    }

    // ==================== 便捷方法 ====================

    /**
     * 是否使用 PostgreSQL 引擎
     */
    public boolean isPostgresql() {
        return EngineType.POSTGRESQL.equals(type);
    }

    /**
     * 是否使用 MySQL 引擎
     */
    public boolean isMysql() {
        return EngineType.MYSQL.equals(type);
    }

    /**
     * 是否使用 Elasticsearch 引擎
     */
    public boolean isElasticsearch() {
        return EngineType.ES.equals(type);
    }

    /**
     * 是否启用数据同步
     */
    public boolean isSyncEnabled() {
        return sync != null && Boolean.TRUE.equals(sync.getEnabled());
    }

    /**
     * 是否异步同步
     */
    public boolean isAsyncSync() {
        return sync != null && Boolean.TRUE.equals(sync.getAsync());
    }
}
