package cn.cheers.x.module.dynamicbusiness.service.entity.core;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

import java.util.List;
import java.util.Set;

/**
 * Entity Core Service
 *
 * <p>仅负责实体本体（实体表）相关的标准能力：</p>
 * <ul>
 *   <li>基础写入：create / update / delete</li>
 *   <li>基础读取：get / list / page（仅实体表条件）</li>
 * </ul>
 *
 * <p>不负责：</p>
 * <ul>
 *   <li>分类/模型/关系等跨主语编排</li>
 *   <li>树构建、跨表聚合、场景化查询路由</li>
 * </ul>
 */
public interface EntityCoreService {

    // ==================== Existence ====================

    /**
     * 检查指定业务类型下实体是否存在。
     *
     * @param entityId 实体ID
     * @param businessTypeCode 业务类型编码（用于路由动态表）
     * @return true=存在；false=不存在
     */
    boolean existsById(Long entityId, String businessTypeCode);

    /**
     * 批量过滤出“真实存在”的实体ID集合。
     *
     * @param entityIds 待校验实体ID列表
     * @param businessTypeCode 业务类型编码（用于路由动态表）
     * @return 存在的实体ID集合
     */
    Set<Long> filterExistingEntityIds(List<Long> entityIds, String businessTypeCode);

    // ==================== CRUD ====================

    /**
     * 创建实体（仅实体本体，不处理分类/关联关系）。
     *
     * @param entity 实体DO
     * @return 新增实体ID
     */
    Long create(EntityDO entity);

    /**
     * 更新实体（仅实体本体字段）。
     *
     * @param entity 实体DO
     */
    void update(EntityDO entity);

    /**
     * 批量更新实体（仅实体本体字段）。
     *
     * @param entities 实体DO列表
     */
    void updateBatch(List<EntityDO> entities);

    /**
     * 删除实体（仅实体本体）。
     *
     * @param id 实体ID
     * @param businessTypeCode 业务类型编码
     */
    void delete(Long id, String businessTypeCode);

    /**
     * 获取单个实体详情（本体）。
     *
     * @param id 实体ID
     * @param businessTypeCode 业务类型编码
     * @return 实体DO，不存在时返回 null
     */
    EntityDO get(Long id, String businessTypeCode);

    /**
     * 按条件查询实体列表（本体）。
     *
     * @param businessTypeCode 业务类型编码
     * @param modelId 模型ID（可选）
     * @param status 状态（可选）
     * @return 实体DO列表
     */
    List<EntityDO> listEntities(String businessTypeCode, Long modelId, Integer status);

    /**
     * 按ID批量查询实体（本体）。
     *
     * @param ids 实体ID列表
     * @param businessTypeCode 业务类型编码
     * @return 实体DO列表
     */
    List<EntityDO> listByIds(List<Long> ids, String businessTypeCode);

    /**
     * 按实体本体条件分页查询。
     *
     * <p>仅作用于实体表本体字段（businessTypeCode / modelId / status / keyword），
     * 不涉及分类、关系或聚合逻辑。</p>
     *
     * @param businessTypeCode 业务类型编码
     * @param modelId 模型ID（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<EntityDO> pageEntities(String businessTypeCode, Long modelId, Integer status, String keyword, Integer pageNo, Integer pageSize);

    /**
     * 按多个模型ID进行实体本体分页查询。
     *
     * <p>使用完整的 modelIds 作为过滤条件，分页落在实体表侧。
     * 适用于“分类 -> 模型 -> 实体”的场景，避免在 model 层分页导致实体丢失。</p>
     *
     * @param businessTypeCode 业务类型编码
     * @param modelIds 模型ID列表（不能为空）
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<EntityDO> pageEntitiesByModelIds(String businessTypeCode, List<Long> modelIds, Integer status,
                                                    String keyword, Integer pageNo, Integer pageSize);

    // ==================== Tree / Path ====================

    /**
     * 移动实体到新的父节点下，并维护树路径。
     *
     * @param entityId 实体ID
     * @param businessTypeCode 业务类型编码
     * @param newParentId 新父实体ID，null/0 表示移动为根节点
     */
    void moveEntity(Long entityId, String businessTypeCode, Long newParentId);

    /**
     * 查询实体树原始节点列表（按业务类型/模型过滤）。
     *
     * <p>返回的是扁平节点集合，树结构组装由上层完成。</p>
     *
     * @param businessTypeCode 业务类型编码
     * @param modelId 模型ID（可选）
     * @return 实体DO扁平列表
     */
    List<EntityDO> listTreeEntities(String businessTypeCode, Long modelId);

    /**
     * 按父节点查询直接子实体；parentId 为 null 时仅返回根节点（parent_id IS NULL）。
     */
    List<EntityDO> listEntitiesByParentId(String businessTypeCode, Long parentId);

    /**
     * 按父节点分页查询直接子实体；parentId 为 null 时仅返回根节点（parent_id IS NULL）。
     */
    PageResult<EntityDO> pageEntitiesByParentId(String businessTypeCode, Long parentId, Integer pageNo, Integer pageSize);

    /**
     * 获取实体路径（从根到当前实体的名称路径）。
     * 前提是实体有数型结构的情况下使用  （todo:这个待分析是否要优化）
     *
     * @param entityId 实体ID
     * @param businessTypeCode 业务类型编码
     * @return 路径名称列表
     */
    List<String> getEntityPath(Long entityId, String businessTypeCode);

    // ==================== Precompute ====================

    /**
     * 按模型获取实体ID列表（用于预计算或批处理）。
     *
     * @param modelId 模型ID
     * @param businessTypeCode 业务类型编码
     * @return 实体ID列表
     */
    List<Long> getEntityIdsByModelId(Long modelId, String businessTypeCode);

    /**
     * 判断指定实体是否存在直接子实体。
     *
     * @param parentId 父实体ID
     * @param businessTypeCode 业务类型编码
     * @return 是否存在子实体
     */
    boolean existsByParentId(Long parentId, String businessTypeCode);
}

