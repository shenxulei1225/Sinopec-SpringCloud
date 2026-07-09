package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;

import java.util.Map;

/**
 * Entity 关联同步服务接口
 *
 * <p>负责在 Entity 创建/更新/删除时，自动同步 ENTITY_REF 字段的关联关系到 EntityRelationDO 表。</p>
 *
 * <h3>核心功能</h3>
 * <ul>
 *   <li>Entity 创建时：解析 ENTITY_REF 字段，创建对应的 EntityRelationDO 记录</li>
 *   <li>Entity 更新时：检测 ENTITY_REF 字段变更，删除旧关联、创建新关联</li>
 *   <li>Entity 删除时：清理所有相关的 EntityRelationDO 记录</li>
 * </ul>
 *
 * <h3>设计说明</h3>
 * <ul>
 *   <li>EntityRelationDO 作为关联索引表，支持高效的反向查询</li>
 *   <li>同步时填充 fieldCode、sourceModelCode、targetModelCode 等字段，优化查询性能</li>
 *   <li>支持单选关联（ENTITY_REF）和多选关联（ENTITY_REF_MULTI，未来扩展）</li>
 * </ul>
 *
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-BDA-074: 系统必须在建立关联后自动提供反向查询能力</li>
 *   <li>FR-BDA-090: 系统必须自动发现所有关联关系</li>
 * </ul>
 *
 * @author 基础服务模块
 * @see EntityRelationService
 */
public interface EntityRelationSyncService {

    /**
     * Entity 创建时同步关联关系
     *
     * <p>解析 Entity 的 customFields 中的 ENTITY_REF 字段，
     * 为每个有值的关联字段创建 EntityRelationDO 记录。</p>
     *
     * <h4>处理流程</h4>
     * <ol>
     *   <li>获取 Model 的所有 ENTITY_REF 类型字段</li>
     *   <li>遍历每个字段，检查 customFields 中是否有值</li>
     *   <li>如果有值，创建 EntityRelationDO 记录</li>
     *   <li>填充 fieldCode、sourceModelCode、targetModelCode 等元数据</li>
     * </ol>
     *
     * @param entity 新创建的 Entity
     * @param model Entity 所属的 Model
     * @param customFields 解析后的自定义字段 Map
     */
    void syncRelationsOnCreate(EntityDO entity, ModelDO model, Map<String, Object> customFields);

    /**
     * Entity 更新时同步关联关系
     *
     * <p>检测 ENTITY_REF 字段的变更，删除旧关联、创建新关联。</p>
     *
     * <h4>处理流程</h4>
     * <ol>
     *   <li>获取 Model 的所有 ENTITY_REF 类型字段</li>
     *   <li>遍历每个字段，比较新旧值</li>
     *   <li>如果值发生变化：
     *     <ul>
     *       <li>删除旧的 EntityRelationDO 记录（如果旧值不为空）</li>
     *       <li>创建新的 EntityRelationDO 记录（如果新值不为空）</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param entity 更新后的 Entity
     * @param model Entity 所属的 Model
     * @param newCustomFields 新的自定义字段 Map
     * @param oldCustomFields 旧的自定义字段 Map（更新前的值）
     */
    void syncRelationsOnUpdate(EntityDO entity, ModelDO model,
                                Map<String, Object> newCustomFields,
                                Map<String, Object> oldCustomFields);

    /**
     * Entity 删除时清理关联关系
     *
     * <p>删除 Entity 作为源实体的所有 EntityRelationDO 记录。</p>
     *
     * <h4>注意事项</h4>
     * <ul>
     *   <li>只删除 sourceEntityId = entityId 的记录</li>
     *   <li>targetEntityId = entityId 的记录由 EntityRelationService 处理</li>
     * </ul>
     *
     * @param entityId Entity ID
     * @param entityTypeCode 业务类型编码
     */
    void syncRelationsOnDelete(Long entityId, String entityTypeCode);

    /**
     * 批量同步 Entity 的关联关系（用于数据迁移）
     *
     * <p>扫描 Entity 的 customFields，同步所有 ENTITY_REF 字段到 EntityRelationDO。</p>
     *
     * <h4>使用场景</h4>
     * <ul>
     *   <li>历史数据迁移：将现有 Entity 的关联关系同步到 EntityRelationDO</li>
     *   <li>数据修复：重建 EntityRelationDO 索引</li>
     * </ul>
     *
     * @param entity 需要同步的 Entity
     * @param model Entity 所属的 Model
     * @param customFields 解析后的自定义字段 Map
     * @param clearExisting 是否先清除现有关联（true = 全量同步，false = 增量同步）
     */
    void syncRelationsForMigration(EntityDO entity, ModelDO model,
                                    Map<String, Object> customFields,
                                    boolean clearExisting);
}
