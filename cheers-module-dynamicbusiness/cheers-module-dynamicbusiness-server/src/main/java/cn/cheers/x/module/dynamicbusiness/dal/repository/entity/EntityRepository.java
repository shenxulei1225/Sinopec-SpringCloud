package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

import java.util.List;

/**
 * 实体 Repository 接口
 *
 * <p>统一的数据访问接口，内部使用动态表名机制。</p>
 *
 * <h3>设计说明</h3>
 * <ul>
 *   <li>所有方法都需要传入 businessTypeCode 用于动态表名路由</li>
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
     * @param entity 实体对象（必须包含 businessTypeCode）
     * @return 保存后的实体ID
     */
    Long save(EntityDO entity);

    /**
     * 批量保存实体
     *
     * @param entities 实体列表（所有实体必须属于同一个 businessTypeCode）
     */
    void saveBatch(List<EntityDO> entities);

    // ==================== 读取操作 ====================

    /**
     * 根据ID查询实体
     *
     * @param id 实体ID
     * @param businessTypeCode 业务类型编码
     * @return 实体对象，不存在返回 null
     */
    EntityDO findById(Long id, String businessTypeCode);

    /**
     * 根据ID列表批量查询实体
     *
     * @param ids 实体ID列表
     * @param businessTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByIds(List<Long> ids, String businessTypeCode);

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
     * @param businessTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByModelId(Long modelId, String businessTypeCode);

    /**
     * 根据模型ID列表查询实体列表
     *
     * @param modelIds 模型ID列表
     * @param businessTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByModelIds(List<Long> modelIds, String businessTypeCode);

    /**
     * 根据模型ID列表分页查询实体
     *
     * @param modelIds 模型ID列表
     * @param businessTypeCode 业务类型编码
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @param pageNo 页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<EntityDO> findPageByModelIds(List<Long> modelIds, String businessTypeCode,
                                            Integer status, String keyword, Integer pageNo, Integer pageSize);

    /**
     * 根据树路径查询所有子孙实体
     *
     * @param treePath 树路径
     * @param businessTypeCode 业务类型编码
     * @return 实体列表
     */
    List<EntityDO> findByTreePathStartsWith(String treePath, String businessTypeCode);

    // ==================== 更新操作 ====================

    /**
     * 更新实体
     * 
     * @param entity 实体对象（必须包含 id 和 businessTypeCode）
     */
    void update(EntityDO entity);

    /**
     * 批量更新实体
     * 
     * @param entities 实体列表
     */
    void updateBatch(List<EntityDO> entities);

    // ==================== 删除操作 ====================

    /**
     * 删除实体
     * 
     * @param id 实体ID
     * @param businessTypeCode 业务类型编码
     */
    void delete(Long id, String businessTypeCode);

    /**
     * 批量删除实体
     * 
     * @param ids 实体ID列表
     * @param businessTypeCode 业务类型编码
     */
    void deleteBatch(List<Long> ids, String businessTypeCode);

    // ==================== 统计操作 ====================

    /**
     * 检查实体是否存在
     * 
     * @param id 实体ID
     * @param businessTypeCode 业务类型编码
     * @return 是否存在
     */
    boolean exists(Long id, String businessTypeCode);

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
     * @param businessTypeCode 业务类型编码
     * @return 数量
     */
    long countByBusinessTypeCode(String businessTypeCode);

    /**
     * 判断是否存在指定父实体的直接子实体。
     *
     * @param parentId 父实体ID
     * @param businessTypeCode 业务类型编码
     * @return 是否存在子实体
     */
    boolean existsByParentId(Long parentId, String businessTypeCode);

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
        private String businessTypeCode;
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
        /** 页码（从1开始） */
        private Integer pageNo;
        /** 每页条数 */
        private Integer pageSize;
    }
}

















