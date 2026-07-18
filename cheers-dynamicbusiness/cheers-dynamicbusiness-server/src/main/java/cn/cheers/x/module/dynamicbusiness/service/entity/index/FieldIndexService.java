package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;

import java.util.List;

/**
 * 字段索引管理服务接口
 *
 * <p>负责管理扩展字段的索引配置，包括：</p>
 * <ul>
 *   <li>获取可查询字段列表</li>
 *   <li>处理字段可查询标记变更</li>
 *   <li>管理字段索引的创建和清理</li>
 * </ul>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-019: 系统必须支持标记字段为"可查询"（is_searchable）</li>
 *   <li>FR-020: 系统必须在字段标记为可查询时自动创建索引</li>
 *   <li>FR-021: 系统必须在字段取消可查询标记时清理索引</li>
 *   <li>FR-022: 系统必须支持查询某个 Model 的所有可查询字段列表</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
public interface FieldIndexService {

    /**
     * 获取 Model 的可查询字段列表
     *
     * <p>返回指定 Model 下所有标记为 is_searchable=true 的字段。</p>
     *
     * @param modelId Model ID
     * @return 可查询字段列表
     */
    List<FieldDO> getSearchableFields(Long modelId);

    /**
     * 获取 Model 的可查询字段列表（通过 Model 编码）
     *
     * @param modelCode Model 编码
     * @return 可查询字段列表
     */
    List<FieldDO> getSearchableFieldsByModelCode(String modelCode);

    /**
     * 获取 Model 的可排序字段列表
     *
     * @param modelId Model ID
     * @return 可排序字段列表
     */
    List<FieldDO> getSortableFields(Long modelId);

    /**
     * 检查字段是否可查询
     *
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @return 是否可查询
     */
    boolean isFieldSearchable(Long modelId, String fieldCode);

    /**
     * 检查字段是否可排序
     *
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @return 是否可排序
     */
    boolean isFieldSortable(Long modelId, String fieldCode);

    /**
     * 处理字段可查询标记变更
     *
     * <p>当字段的 is_searchable 标记发生变更时调用此方法：</p>
     * <ul>
     *   <li>从 false 变为 true：为该字段创建索引（同步现有 Entity 数据）</li>
     *   <li>从 true 变为 false：清理该字段的索引数据</li>
     * </ul>
     *
     * @param fieldId 字段 ID
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param newSearchable 新的可查询标记
     */
    void onSearchableChanged(Long fieldId, Long modelId, String fieldCode, boolean newSearchable);

    /**
     * 为字段创建索引
     *
     * <p>将指定 Model 下所有 Entity 的该字段值同步到索引表。</p>
     *
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param fieldType 字段类型
     */
    void createFieldIndex(Long modelId, String fieldCode, String fieldType);

    /**
     * 清理字段索引
     *
     * <p>删除索引表中该字段的所有数据。</p>
     *
     * @param modelId Model ID
     * @param fieldCode 字段编码
     */
    void removeFieldIndex(Long modelId, String fieldCode);

    /**
     * 获取字段的索引策略
     *
     * @param fieldType 字段类型
     * @return 索引策略
     */
    IndexStrategy getIndexStrategy(String fieldType);

    /**
     * 判断字段类型是否默认可查询
     *
     * <p>智能默认规则：</p>
     * <ul>
     *   <li>STRING、INTEGER、DECIMAL、DATE、DATETIME、BOOLEAN、SELECT、ENTITY_REF 默认 true</li>
     *   <li>TEXT、FILE、IMAGE 默认 false</li>
     * </ul>
     *
     * @param fieldType 字段类型
     * @return 是否默认可查询
     */
    boolean isDefaultSearchable(String fieldType);

    /**
     * 索引策略枚举
     */
    enum IndexStrategy {
        /**
         * 使用 GIN 索引（JSONB 等值/包含查询）
         */
        GIN_INDEX,

        /**
         * 使用索引表（范围查询、排序）
         */
        FIELD_INDEX_TABLE,

        /**
         * 不索引
         */
        NONE
    }
}
