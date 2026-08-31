package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

import java.util.List;
import java.util.Map;

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
     * 保存实体；若 {@code physicalColumns} 非空，则一条 INSERT 同时写入专用表固定列。
     *
     * @param entity 实体对象（必须包含 entityTypeCode）
     * @param physicalColumns 列名 → 库值；空则等价于 {@link #save(EntityDO)}
     * @return 保存后的实体ID
     */
    Long save(EntityDO entity, Map<String, Object> physicalColumns);

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
     * 按有序 id 一次 SELECT 核心列 + 专用表基础字段列（含 custom_fields）。
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
     * 同 {@link #findByIdsWithDedicatedBaseFields(List, String)}；
     * {@code includeCustomFields=false} 时列表热路径不取 {@code custom_fields}（单选 REF 已在专用列）。
     */
    List<EntityDO> findByIdsWithDedicatedBaseFields(List<Long> orderedIds, String entityTypeCode,
                                                    boolean includeCustomFields);

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
     * 分页只取实体 id（库内 ORDER BY + LIMIT），供列表热路径再一次装 VO。
     */
    PageResult<Long> findPageIds(EntityQuery query);

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
     * 按模型 ID 列表分页；可选库内排序（核心列或已校验的专用表物理列名）。
     *
     * @param orderByColumn 核心列或物理列名；空则按 sort
     * @param orderAsc      是否升序；orderByColumn 为空时忽略
     */
    PageResult<EntityDO> findPageByModelIds(List<Long> modelIds, String entityTypeCode,
                                            Integer status, String keyword, String domain,
                                            Integer pageNo, Integer pageSize,
                                            String orderByColumn, Boolean orderAsc);

    /**
     * 按模型 ID 列表分页只取实体 id（列表热路径，避免本页双载）。
     */
    PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                           Integer status, String keyword, String domain,
                                           Integer pageNo, Integer pageSize,
                                           String orderByColumn, Boolean orderAsc);

    /**
     * 同 {@link #findPageIdsByModelIds}，可叠加已校验的物理列 EQ·IN。
     */
    PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                           Integer status, String keyword, String domain,
                                           Integer pageNo, Integer pageSize,
                                           String orderByColumn, Boolean orderAsc,
                                           java.util.List<PhysicalColumnFilter> physicalFilters);

    /**
     * 同 {@link #findPageIdsByModelIds}，关键词按 {@link KeywordSearchSpec} 多列 OR。
     */
    PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                           Integer status, String keyword, String domain,
                                           Integer pageNo, Integer pageSize,
                                           String orderByColumn, Boolean orderAsc,
                                           java.util.List<PhysicalColumnFilter> physicalFilters,
                                           KeywordSearchSpec keywordSearch);

    /**
     * 同上一重载，可叠加划分成员 EXISTS（与分类直查同一套成员表收窄）。
     *
     * @param scopeRegistryCode 划分注册编码；空则不限成员
     */
    PageResult<Long> findPageIdsByModelIds(List<Long> modelIds, String entityTypeCode,
                                           Integer status, String keyword, String domain,
                                           Integer pageNo, Integer pageSize,
                                           String orderByColumn, Boolean orderAsc,
                                           java.util.List<PhysicalColumnFilter> physicalFilters,
                                           KeywordSearchSpec keywordSearch,
                                           String scopeRegistryCode);

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
     * 当前实体类型物理表内是否存在相同编码（精确匹配，未删除；用于编码唯一校验 / 提交兜底）。
     * 作用域与库条件唯一索引一致：同表 + deleted=false + code 非空。
     */
    boolean existsByExactCode(String entityTypeCode, String code, Long excludeId);

    /**
     * 按业务编码精确取一条未删除实体（同表；用于系统同步 upsert，避免误 create 撞唯一索引）。
     *
     * @return 命中行；不存在返回 null
     */
    EntityDO findByExactCode(String entityTypeCode, String code);

    /**
     * 型号下是否仍存在未删除实体（删型号前护栏）。
     */
    boolean existsByModelId(Long modelId, String entityTypeCode);

    /**
     * 统计型号实体行上非空 {@code facility_id} 的去重数量。
     *
     * <p>这是型号设施覆盖范围的权威读取；不得改为读取型号发起设施，也不得从其它字段补值。</p>
     */
    long countDistinctFacilityIdsByModelId(Long modelId, String entityTypeCode);

    /**
     * 判断实体物理表是否具备所属场站权威列，避免覆盖范围统计直接触发原生 SQL 列不存在错误。
     */
    boolean hasFacilityIdColumn(String entityTypeCode);

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
     * 按实体 id 出现顺序去重收集 model_id（跳过空 / 非法）。
     * <p>用于多栏分类求交后派生型号列，只查 id+model_id，不装完整实体。</p>
     */
    List<Long> listDistinctModelIdsPreservingEntityOrder(List<Long> orderedEntityIds, String entityTypeCode);

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
        /**
         * 关键词多列搜索计划；空则 keyword 仅按 name LIKE（历史行为）。
         */
        private KeywordSearchSpec keywordSearch;
        /** 业务域（Domain），可选 */
        private String domain;
        /**
         * 划分注册编码（SCOPE）：有值时只保留成员表中的实体。
         * SQL 用 EXISTS dynamic_entity_type_scope，与分类直查路径同一套标准收窄。
         */
        private String scopeRegistryCode;
        /** 页码（从1开始） */
        private Integer pageNo;
        /** 每页条数 */
        private Integer pageSize;
        /**
         * 库内排序列：核心列名或已校验的专用表物理列名；空则按 sort。
         * 禁止传入任意 SQL 片段。
         */
        private String orderByColumn;
        /** 与 orderByColumn 配套；null 视为升序 */
        private Boolean orderAsc;
        /**
         * 已校验的专用/核心物理列 EQ·IN 筛选；由上层保证列名安全。
         */
        private java.util.List<PhysicalColumnFilter> physicalFilters;
    }
}

















