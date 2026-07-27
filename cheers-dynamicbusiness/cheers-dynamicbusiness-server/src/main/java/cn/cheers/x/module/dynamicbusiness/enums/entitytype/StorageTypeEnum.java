package cn.cheers.x.module.dynamicbusiness.enums.entitytype;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务类型存储策略枚举
 * 
 * <h2>架构演进说明</h2>
 * <p>
 * 根据架构重构分析，DEDICATED_STATIC 和 DEDICATED_DYNAMIC 的本质区别仅在于：
 * <ul>
 *   <li>有 physicalColumnMapping 配置 → 原 DEDICATED_STATIC（高性能查询）</li>
 *   <li>无 physicalColumnMapping 配置 → 原 DEDICATED_DYNAMIC（灵活扩展）</li>
 * </ul>
 * 
 * <h2>目标架构</h2>
 * <ul>
 *   <li><b>DEDICATED</b>：唯一允许的实体存储；使用 {@code ent_*} 专用表，经 EntityRepository 访问</li>
 *   <li><b>GENERIC</b>：已废止（原 dynamic_entity 已删除）；创建/解析时拒绝</li>
 * </ul>
 * 
 * <h2>当前状态</h2>
 * <ul>
 *   <li>GENERIC：枚举值仅保留兼容读旧数据；写路径与表路由一律禁止</li>
 *   <li>DEDICATED_STATIC / DEDICATED_DYNAMIC：已废弃，请使用 DEDICATED</li>
 *   <li>DEDICATED：统一的专用表存储类型</li>
 * </ul>
 * 
 * @author yudao
 * @see <a href=".kiro/specs/extend-field-query/architecture-refactoring-analysis.md">架构重构分析文档</a>
 */
@Getter
@AllArgsConstructor
public enum StorageTypeEnum {

    /**
     * 通用表存储（已废止）
     * <p>原 {@code dynamic_entity} 已删除。枚举值仅兼容历史元数据；禁止新写入与表路由。</p>
     */
    GENERIC("GENERIC", "通用表存储（已废止）"),

    /**
     * 预制专用表存储
     * 
     * @deprecated 已废弃，请使用 {@link #DEDICATED} + physicalColumnMapping 配置替代。
     *             <p>
     *             迁移方式：
     *             <ol>
     *               <li>将 storageType 改为 DEDICATED</li>
     *               <li>配置 physicalColumnMapping 映射物理列</li>
     *               <li>删除专用的 Strategy 类（如 EquipmentEntityStorageStrategy）</li>
     *             </ol>
     *             </p>
     * @see #DEDICATED
     */
    @Deprecated
    DEDICATED_STATIC("DEDICATED_STATIC", "预制专用表存储"),

    /**
     * 动态专用表存储
     * 
     * @deprecated 已废弃，请使用 {@link #DEDICATED}（不配置 physicalColumnMapping）替代。
     *             <p>
     *             迁移方式：
     *             <ol>
     *               <li>将 storageType 改为 DEDICATED</li>
     *               <li>不配置 physicalColumnMapping（字段存储到 JSONB）</li>
     *             </ol>
     *             </p>
     * @see #DEDICATED
     */
    @Deprecated
    DEDICATED_DYNAMIC("DEDICATED_DYNAMIC", "动态专用表存储"),

    /**
     * 专用表存储（统一类型）
     * <p>
     * 合并了原 DEDICATED_STATIC 和 DEDICATED_DYNAMIC，通过配置区分行为：
     * </p>
     * 
     * <h3>配置驱动</h3>
     * <ul>
     *   <li><b>有 physicalColumnMapping</b>：字段存储到物理列（高性能查询，原 STATIC）</li>
     *   <li><b>无 physicalColumnMapping</b>：字段存储到 JSONB（灵活扩展，原 DYNAMIC）</li>
     * </ul>
     * 
     * <h3>特点</h3>
     * <ul>
     *   <li>使用专用表（如 ent_equipment）</li>
     *   <li>统一使用 JdbcTemplate 实现</li>
     *   <li>纯配置驱动，无需编写专用策略类</li>
     *   <li>支持渐进式优化：先用 JSONB，后续按需添加物理列映射</li>
     * </ul>
     * 
     * <h3>配置示例</h3>
     * <pre>
     * // 无物理列映射（原 DYNAMIC 行为）
     * {
     *   "storageType": "DEDICATED",
     *   "dedicatedTableName": "ent_task",
     *   "physicalColumnMapping": null
     * }
     * 
     * // 有物理列映射（原 STATIC 行为）
     * {
     *   "storageType": "DEDICATED",
     *   "dedicatedTableName": "ent_equipment",
     *   "physicalColumnMapping": {
     *     "code": {"column": "code", "type": "VARCHAR"},
     *     "manufacturer": {"column": "manufacturer", "type": "VARCHAR"}
     *   }
     * }
     * </pre>
     */
    DEDICATED("DEDICATED", "专用表存储");

    /**
     * 存储类型编码
     */
    private final String code;

    /**
     * 存储类型名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     * 
     * @param code 存储类型编码
     * @return 存储类型枚举，如果不存在返回 null
     */
    public static StorageTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (StorageTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断编码是否有效
     * 
     * @param code 存储类型编码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }

    /**
     * 判断是否为专用存储类型
     * <p>
     * 包括：DEDICATED、DEDICATED_STATIC（已废弃）、DEDICATED_DYNAMIC（已废弃）
     * </p>
     * 
     * @return 是否为专用存储类型
     */
    public boolean isDedicated() {
        return this == DEDICATED || this == DEDICATED_STATIC || this == DEDICATED_DYNAMIC;
    }

    /**
     * 判断是否需要代码开发
     * 
     * @deprecated 新架构下所有 DEDICATED 类型都不需要代码开发，通过配置驱动
     * @return 是否需要代码开发（新架构下始终返回 false）
     */
    @Deprecated
    public boolean requiresCodeDevelopment() {
        // 新架构：所有 DEDICATED 类型都是配置驱动，不需要代码开发
        // 保留此方法仅为向后兼容
        return false;
    }

    /**
     * 判断是否支持规则引擎
     * <p>
     * 新架构下，所有 DEDICATED 类型都支持规则引擎
     * </p>
     * 
     * @return 是否支持规则引擎
     */
    public boolean supportsRuleEngine() {
        return isDedicated();
    }

    /**
     * 判断是否为新架构的统一专用表类型
     * 
     * @return 是否为 DEDICATED 类型
     */
    public boolean isUnifiedDedicated() {
        return this == DEDICATED;
    }

    /**
     * 判断是否为已废弃的存储类型
     * 
     * @return 是否已废弃
     */
    public boolean isDeprecated() {
        return this == DEDICATED_STATIC || this == DEDICATED_DYNAMIC;
    }

    /**
     * 获取推荐的迁移目标类型
     * <p>
     * 用于提示用户将废弃类型迁移到新类型
     * </p>
     * 
     * @return 推荐的迁移目标类型，如果不需要迁移返回自身
     */
    public StorageTypeEnum getRecommendedMigrationTarget() {
        if (this == DEDICATED_STATIC || this == DEDICATED_DYNAMIC) {
            return DEDICATED;
        }
        return this;
    }
}
