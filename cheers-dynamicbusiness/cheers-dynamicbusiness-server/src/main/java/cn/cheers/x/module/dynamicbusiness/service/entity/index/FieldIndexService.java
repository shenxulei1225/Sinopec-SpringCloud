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
 * <p>进索引表的权威：可搜索、可筛选、可排序任一为真。
 * 关键词仍认可搜索；条件筛选认可筛选。禁止用可搜索挡筛选命中。</p>
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
     * 检查字段是否可筛选（分配上明确打开）。
     * 筛选查询门禁用这个，不用可搜索。
     */
    boolean isFieldFilterable(Long modelId, String fieldCode);

    /**
     * 可搜索 / 可筛选 / 可排序变化后，按「现在该不该进索引表」建或清索引。
     *
     * @param shouldIndex true 则按该型号已有实体批量写入该字段索引；false 则按型号+字段编码整批删除
     */
    void onIndexMembershipChanged(Long fieldId, Long modelId, String fieldCode, boolean shouldIndex);

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
