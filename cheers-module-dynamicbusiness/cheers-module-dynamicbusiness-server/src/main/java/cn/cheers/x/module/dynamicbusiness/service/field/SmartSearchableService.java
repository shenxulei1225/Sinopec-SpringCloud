package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.service.field.vo.SmartDefaultConfigVO;

import java.util.List;
import java.util.Map;

/**
 * 智能默认可查询服务接口
 * 
 * <p>根据字段类型自动设置可查询属性和索引策略，实现零代码平台的智能默认功能。</p>
 * 
 * <h3>智能默认规则（需求：FR-BDA-080~083）</h3>
 * <ul>
 *   <li>常用字段类型（文本、数字、日期、布尔、单选、关联）默认可查询</li>
 *   <li>大文本字段（LONG_TEXT）默认不可查询，但支持手动开启</li>
 *   <li>文件类字段（FILE、IMAGE）默认不可查询</li>
 *   <li>系统根据字段类型自动选择合适的索引策略</li>
 * </ul>
 * 
 * <h3>业务规则（BR-BDA-060~063）</h3>
 * <ul>
 *   <li>BR-BDA-060: 常用可查询 - 文本、数字、日期、布尔、单选、关联字段默认可查询</li>
 *   <li>BR-BDA-061: 大文本不索引 - LONG_TEXT 类型默认不可查询，避免性能问题</li>
 *   <li>BR-BDA-062: 文件不索引 - FILE、IMAGE 类型不可查询</li>
 *   <li>BR-BDA-063: 智能索引 - 系统根据字段类型自动选择索引策略</li>
 * </ul>
 * 
 * @author yudao
 * @since 2026-01-07
 */
public interface SmartSearchableService {

    /**
     * 获取字段类型的默认可查询设置
     * 
     * <p>根据字段类型返回是否默认可查询：</p>
     * <ul>
     *   <li>TEXT、NUMBER、INTEGER、DATE、DATETIME、BOOLEAN、ENUM、ENTITY_REF、REFERENCE → true</li>
     *   <li>LONG_TEXT、JSON、FILE、IMAGE → false</li>
     *   <li>未知类型 → false</li>
     * </ul>
     * 
     * @param fieldType 字段类型编码
     * @return true 表示默认可查询，false 表示默认不可查询
     */
    boolean getDefaultSearchable(String fieldType);

    /**
     * 获取字段类型的默认可排序设置
     * 
     * <p>根据字段类型返回是否默认可排序：</p>
     * <ul>
     *   <li>NUMBER、INTEGER、DATE、DATETIME、TEXT → true</li>
     *   <li>其他类型 → false</li>
     * </ul>
     * 
     * @param fieldType 字段类型编码
     * @return true 表示默认可排序，false 表示默认不可排序
     */
    boolean getDefaultSortable(String fieldType);

    /**
     * 获取字段类型的默认索引策略
     * 
     * <p>根据字段类型返回推荐的索引策略：</p>
     * <ul>
     *   <li>TEXT、BOOLEAN、ENUM、ENTITY_REF、REFERENCE → GIN（等值/包含查询）</li>
     *   <li>NUMBER、INTEGER、DATE、DATETIME → BTREE（范围查询/排序）</li>
     *   <li>LONG_TEXT、JSON、FILE、IMAGE → NONE（不索引）</li>
     * </ul>
     * 
     * @param fieldType 字段类型编码
     * @return 索引策略：GIN、BTREE、NONE
     */
    IndexStrategy getDefaultIndexStrategy(String fieldType);

    /**
     * 应用智能默认设置到字段
     * 
     * <p>根据字段类型自动设置以下属性（仅当属性为 null 时）：</p>
     * <ul>
     *   <li>isSearchable - 是否可查询</li>
     *   <li>isSortable - 是否可排序</li>
     *   <li>indexStrategy - 索引策略（GIN/BTREE/NONE）</li>
     * </ul>
     * 
     * <p>此方法会修改传入的 FieldDO 对象。</p>
     * 
     * @param field 字段对象
     */
    void applySmartDefaults(FieldDO field);

    /**
     * 批量应用智能默认设置到字段列表
     * 
     * @param fields 字段列表
     */
    void applySmartDefaults(List<FieldDO> fields);

    /**
     * 获取所有字段类型的智能默认配置
     * 
     * <p>返回一个 Map，key 为字段类型编码，value 为该类型的智能默认配置。</p>
     * 
     * @return 字段类型 → 智能默认配置的映射
     */
    Map<String, SmartDefaultConfigVO> getSmartDefaultConfigs();

    /**
     * 获取指定字段类型的智能默认配置
     * 
     * @param fieldType 字段类型编码
     * @return 智能默认配置，如果类型不存在则返回默认配置
     */
    SmartDefaultConfigVO getSmartDefaultConfig(String fieldType);

    /**
     * 判断字段类型是否支持手动开启可查询
     * 
     * <p>某些类型（如 LONG_TEXT）虽然默认不可查询，但支持用户手动开启。</p>
     * <p>某些类型（如 FILE、IMAGE）不支持手动开启可查询。</p>
     * 
     * @param fieldType 字段类型编码
     * @return true 表示支持手动开启，false 表示不支持
     */
    boolean canManuallyEnableSearchable(String fieldType);

    /**
     * 索引策略枚举
     */
    enum IndexStrategy {
        /**
         * GIN 索引
         * 适用于等值查询和包含查询
         * 推荐用于：文本、布尔、枚举、关联字段
         */
        GIN("GIN", "GIN 索引"),

        /**
         * B-Tree 索引
         * 适用于范围查询和排序
         * 推荐用于：数字、日期字段
         */
        BTREE("BTREE", "B-Tree 索引"),

        /**
         * 不创建索引
         * 适用于：大文本、文件、复杂 JSON 等
         */
        NONE("NONE", "不索引");

        private final String code;
        private final String name;

        IndexStrategy(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        /**
         * 根据编码获取枚举
         */
        public static IndexStrategy getByCode(String code) {
            if (code == null) {
                return NONE;
            }
            for (IndexStrategy strategy : values()) {
                if (strategy.getCode().equalsIgnoreCase(code)) {
                    return strategy;
                }
            }
            return NONE;
        }
    }
}
