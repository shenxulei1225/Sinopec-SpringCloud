package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

import java.util.List;

/**
 * 实体 Repository 接口
 *
 * <p>统一的数据访问接口，内部使用动态表名机制。</p>
 *
 * <h3>设计说明</h3>
 * <ul>
 *   <li>所有方法都需要传入 entityTypeCode 用于动态表名路由</li>
 *   <li>接口只处理 EntityDO，业务逻辑在 Service 层</li>
 *   <li>使用 MyBatis-Plus 的动态表名拦截器实现表名切换</li>
 * </ul>
 *
 * @author 基础服务模块
 */
public interface EntityRepository {

    // ==================== 写入操作 ====================

    /**
     * 保存实体
     *
     * @param entity 实体对象（必须包含 entityTypeCode）
     * @return 保存后的实体ID
     */
    Long save(EntityDO entity);

    /**
     * 批量保存实体
     *
     * @param entities 实体列表（所有实体必须属于同一个 entityTypeCode）
     */
    void saveBatch(List<EntityDO> entities);

    // ==================== 读取操作 ====================

    /**
     * 根据ID查询实体
     *
     * @param id 实体ID
     * @param entityTypeCode 业务类型编码
     * @return 实体对象，不存在返回 null
     */
    EntityDO findById(Long id, String entityTypeCode);

    /**
     * 根据ID列表批量查询实体
     *
     * @param ids 实体ID列表
     * @param entityTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByIds(List<Long> ids, String entityTypeCode);

    /**
     * 按有序 id 一次 SELECT 核心列 + 专用表基础字段列。
     *
     * <p>空 {@code orderedIds} 返回空列表；结果按 {@code orderedIds} 保序；
     * 基础字段列值写入 {@link EntityDO#getDedicatedBaseFieldValues()}（键为字段编码）。</p>
     *
     * @param orderedIds 本页实体 id（保序）
     * @param entityTypeCode 业务类型编码
     * @return 实体列表（含 dedicatedBaseFieldValues）
     */
    List<EntityDO> findByIdsWithDedicatedBaseFields(List<Long> orderedIds, String entityTypeCode);

    /**
     * 根据条件查询实体列表
     *
     * @param query 查询条件
     * @return 实体列表
     */
    List<EntityDO> findAll(EntityQuery query);

    /**
     * 根据条件分页查询实体
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<EntityDO> findPage(EntityQuery query);

    /**
     * 根据模型ID查询实体列表
     *
     * @param modelId 模型ID
     * @param entityTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByModelId(Long modelId, String entityTypeCode);

    /**
     * 根据模型ID列表查询实体列表
     *
     * @param modelIds 模型ID列表
     * @param entityTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByModelIds(List<Long> modelIds, String entityTypeCode);

    /**
     * 根据模型ID列表分页查询实体
     *
     * @param modelIds 模型ID列表
     * @param entityTypeCode 业务类型编码
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @param pageNo 页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<EntityDO> findPageByModelIds(List<Long> modelIds, String entityTypeCode,
                                            Integer status, String keyword, String domain,
                                            Integer pageNo, Integer pageSize);

    /**
     * 根据树路径查询所有子孙实体
     *
     * @param treePath 树路径
     * @param entityTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByTreePathStartsWith(String treePath, String entityTypeCode);

    // ==================== 更新操作 ====================

    /**
     * 更新实体
     * 
     * @param entity 实体对象（必须包含 id 和 entityTypeCode）
     */
    void update(EntityDO entity);

    /**
     * 批量更新实体
     * 
     * @param entities 实体列表
     */
    void updateBatch(List<EntityDO> entities);

    /**
     * 把某个型号下全部实体的业务域改为目标值（型号跨业务域迁移用）。
     *
     * @param modelId 型号ID
     * @param entityTypeCode 实际存储类型编码
     * @param domain 目标业务域；null 表示清空
     * @return 受影响的实体行数
     */
    int updateDomainByModelId(Long modelId, String entityTypeCode, String domain);

    // ==================== 删除操作 ====================

    /**
     * 删除实体
     * 
     * @param id 实体ID
     * @param entityTypeCode 业务类型编码
     */
    void delete(Long id, String entityTypeCode);

    /**
     * 批量删除实体
     * 
     * @param ids 实体ID列表
     * @param entityTypeCode 业务类型编码
     */
    void deleteBatch(List<Long> ids, String entityTypeCode);

    // ==================== 统计操作 ====================

    /**
     * 检查实体是否存在
     * 
     * @param id 实体ID
     * @param entityTypeCode 业务类型编码
     * @return 是否存在
     */
    boolean exists(Long id, String entityTypeCode);

    /**
     * 统计实体数量
     * 
     * @param query 查询条件
     * @return 数量
     */
    long count(EntityQuery query);

    /**
     * 统计业务类型下的实体数量
     * 
     * @param entityTypeCode 业务类型编码
     * @return 数量
     */
    long countByEntityTypeCode(String entityTypeCode);

    /**
     * 判断是否存在指定父实体的直接子实体。
     *
     * @param parentId 父实体ID
     * @param entityTypeCode 业务类型编码
     * @return 是否存在子实体
     */
    boolean existsByParentId(Long parentId, String entityTypeCode);

    /**
     * 同模型下是否存在同名实体（精确匹配，用于 CRUD 异步校验）。
     */
    boolean existsByExactName(String entityTypeCode, Long modelId, String name, Long excludeId);

    /**
     * 型号下是否仍存在未删除实体（删型号前护栏）。
     */
    boolean existsByModelId(Long modelId, String entityTypeCode);

    /**
     * 按状态聚合计数（须经本 Repository，禁止业务层直接打 Mapper）。
     */
    java.util.List<cn.cheers.x.module.dynamicbusiness.service.entity.dto.EntityAggregationCountDTO<Integer>>
            countGroupByStatus(String entityTypeCode, Long modelId, Integer status, String keyword,
                               java.util.List<Long> entityIds);

    /**
     * 按型号聚合计数。
     */
    java.util.List<cn.cheers.x.module.dynamicbusiness.service.entity.dto.EntityAggregationCountDTO<Long>>
            countGroupByModelId(String entityTypeCode, Long modelId, Integer status, String keyword,
                                java.util.List<Long> entityIds);

    /**
     * 解析存储类型编码对应的物理表名（供需原生 SQL 的查询引擎使用）。
     * <p>业务层不得自行拼 {@code ent_*} 或回落已废止表；一律经本方法。</p>
     */
    String resolvePhysicalTableName(String entityTypeCode);

    // ==================== 查询条件类 ====================

    /**
     * 实体查询条件
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class EntityQuery {
        /** 业务类型编码（必填） */
        private String entityTypeCode;
        /** 模型ID */
        private Long modelId;
        /** 父实体ID */
        private Long parentId;
        /** 为 true 时仅查询 parent_id IS NULL 的根节点（与 parentId 互斥） */
        private Boolean rootOnly;
        /** 状态 */
        private Integer status;
        /** 关键词搜索 */
        private String keyword;
        /** 业务域（Domain），可选 */
        private String domain;
        /** 页码（从1开始） */
        private Integer pageNo;
        /** 每页条数 */
        private Integer pageSize;
    }
}

















