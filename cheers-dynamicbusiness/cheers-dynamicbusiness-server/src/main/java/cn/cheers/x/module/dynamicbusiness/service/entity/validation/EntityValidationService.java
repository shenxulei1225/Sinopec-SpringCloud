package cn.cheers.x.module.dynamicbusiness.service.entity.validation;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception.CircularReferenceException;
import cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception.EntityValidationException;
import cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception.InvalidEntityRefException;
import cn.cheers.x.module.dynamicbusiness.service.entity.validation.exception.InvalidParentRefException;

import java.util.Map;

/**
 * Entity 验证服务接口
 * 
 * <p>提供系统级的 Entity 引用验证能力，确保数据完整性和一致性。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-VAL-003: parent_id 必须指向同 Model 的 Entity（系统强制）</li>
 *   <li>BR-VAL-004: parent_id 不能形成循环引用（系统强制）</li>
 *   <li>BR-VAL-005: ENTITY_REF 引用目标须存在，且能按模型字段（关联字段库/模型关系/目标业务类型）解析存储</li>
 *   <li>BR-VAL-006: 所有 Entity 引用验证在保存时执行，不依赖前端验证</li>
 * </ul>
 * 
 * <h3>验证流程</h3>
 * <pre>
 * Entity 保存请求
 *     │
 *     ▼
 * validateEntity()
 *     │ *     │
 *     ├── validateParentRef()   (如果 enable_tree=true 且 parent_id > 0)
 *     │
 *     └── validateEntityRef()   (对每个 ENTITY_REF 类型的扩展字段)
 *     │
 *     ▼
 * 验证通过 → 保存 Entity
 * 验证失败 → 抛出 EntityValidationException
 * </pre>
 * 
 * @author 扩展字段查询服务
 */
public interface EntityValidationService {

    /**
     * 验证 Entity 的所有引用字段
     * 
     * <p>执行完整的 Entity 验证，包括：</p>
     * <ul>
         *   <li>parent_id 验证（如果 Model 启用树形结构）</li>
     *   <li>ENTITY_REF 类型扩展字段验证</li>
     * </ul>
     * 
     * @param entity 待验证的 Entity
     * @param model Entity 所属的 Model
     * @throws EntityValidationException 验证失败时抛出
     */
    void validateEntity(EntityDO entity, ModelDO model);

    /**
     * 验证 Entity 的所有引用字段（带扩展字段数据）
     * 
     * <p>与 {@link #validateEntity(EntityDO, ModelDO)} 类似，但接受解析后的扩展字段数据。</p>
     * 
     * @param entity 待验证的 Entity
     * @param model Entity 所属的 Model
     * @param customFields 解析后的扩展字段数据（字段编码 → 字段值）
     * @throws EntityValidationException 验证失败时抛出
     */
    void validateEntity(EntityDO entity, ModelDO model, Map<String, Object> customFields);



    /**
     * 验证 parent_id 引用
     * 
     * <p>验证规则：</p>
     * <ul>
     *   <li>BR-VAL-003: parent_id 必须指向同 Model 的 Entity</li>
     *   <li>BR-VAL-004: parent_id 不能形成循环引用</li>
     * </ul>
     * 
     * @param parentId 父节点 ID
     * @param entityId 当前 Entity ID（用于循环检测，新建时可为 null）
     * @param modelId Model ID
     * @throws InvalidParentRefException parent_id 指向的 Entity 不存在或不属于同一 Model
     * @throws CircularReferenceException 检测到循环引用
     */
    void validateParentRef(Long parentId, Long entityId, Long modelId);

    /**
     * 验证 ENTITY_REF 类型字段引用
     * 
     * <p>验证规则：</p>
     * <ul>
     *   <li>BR-VAL-005: 引用目标存在且可解析</li>
     * </ul>
     * 
     * @param refEntityId 引用的 Entity ID
     * @param field 字段定义
     * @param entityId 当前 Entity ID（用于错误信息）
     * @param modelId 当前 Model ID（用于错误信息）
     * @throws InvalidEntityRefException 验证失败时抛出
     */
    void validateEntityRef(Long refEntityId, FieldDO field, Long entityId, Long modelId);

    /**
     * 检测循环引用
     * 
     * <p>从指定的父节点开始，沿着 parent_id 链向上遍历，检测是否会形成循环。</p>
     * 
     * @param parentId 父节点 ID
     * @param entityId 当前 Entity ID
     * @param modelId Model ID
     * @return 如果检测到循环，返回循环路径（Entity ID 列表）；否则返回 null
     */
    java.util.List<Long> detectCircularReference(Long parentId, Long entityId, Long modelId);
}
