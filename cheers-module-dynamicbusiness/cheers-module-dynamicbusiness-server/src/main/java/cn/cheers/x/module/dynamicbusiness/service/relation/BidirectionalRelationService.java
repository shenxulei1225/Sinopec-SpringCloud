package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.bidirectional.*;

import java.util.List;
import java.util.Map;

/**
 * 双向关联查询服务接口
 * 
 * <p>实现自动发现关联关系，提供正向和反向查询能力。</p>
 * 
 * <h3>核心功能</h3>
 * <ul>
 *   <li>正向关联查询：获取当前实体引用的其他实体</li>
 *   <li>反向关联查询：获取引用当前实体的其他实体</li>
 *   <li>关联统计：按关联字段分组统计</li>
 *   <li>关联发现：自动发现所有关联关系</li>
 * </ul>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-BDA-052: 双向自动 - 系统自动发现并提供正向和反向查询</li>
 *   <li>BR-BDA-053: 无需配置 - 用户无需额外配置即可使用关联查询功能</li>
 * </ul>
 * 
 * <h3>需求</h3>
 * <ul>
 *   <li>FR-BDA-090: 系统必须自动发现所有关联关系</li>
 *   <li>FR-BDA-091: 系统必须提供反向查询 API</li>
 *   <li>FR-BDA-092: 系统必须提供关联统计 API</li>
 *   <li>FR-BDA-093: 双向关联查询无需用户额外配置</li>
 * </ul>
 * 
 * @author yudao
 */
public interface BidirectionalRelationService {

    /**
     * 获取实体的正向关联（当前实体引用的其他实体）
     * 
     * <p>查询当前实体的所有 ENTITY_REF 类型字段，返回引用的实体信息。</p>
     * 
     * @param entityId 实体 ID
     * @return 正向关联实体列表
     * @deprecated 使用 {@link #getForwardRelations(Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    List<RelatedEntityVO> getForwardRelations(Long entityId);

    /**
     * 获取实体的正向关联（当前实体引用的其他实体）
     * 
     * <p>查询当前实体的所有 ENTITY_REF 类型字段，返回引用的实体信息。</p>
     * 
     * @param entityId 实体 ID
     * @param entityTypeCode 业务类型编码
     * @return 正向关联实体列表
     */
    List<RelatedEntityVO> getForwardRelations(Long entityId, String entityTypeCode);

    /**
     * 获取实体的反向关联（引用当前实体的其他实体）
     * 
     * <p>系统自动发现所有关联关系，查询引用当前实体的所有记录。</p>
     * 
     * @param entityId 实体 ID
     * @return 反向关联实体列表（按 Model 分组）
     * @deprecated 使用 {@link #getReverseRelations(Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    List<RelatedEntityVO> getReverseRelations(Long entityId);

    /**
     * 获取实体的反向关联（引用当前实体的其他实体）
     * 
     * <p>系统自动发现所有关联关系，查询引用当前实体的所有记录。</p>
     * 
     * @param entityId 实体 ID
     * @param entityTypeCode 业务类型编码
     * @return 反向关联实体列表（按 Model 分组）
     */
    List<RelatedEntityVO> getReverseRelations(Long entityId, String entityTypeCode);

    /**
     * 获取实体的反向关联（按 Model 过滤）
     * 
     * <p>查询指定 Model 中引用当前实体的所有记录，支持分页。</p>
     * 
     * @param entityId 实体 ID
     * @param modelCode Model 编码
     * @param pageParam 分页参数
     * @return 分页的实体列表
     * @deprecated 使用 {@link #getReverseRelationsByModel(Long, String, String, PageParam)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    PageResult<EntitySimpleVO> getReverseRelationsByModel(Long entityId, String modelCode, PageParam pageParam);

    /**
     * 获取实体的反向关联（按 Model 过滤）
     * 
     * <p>查询指定 Model 中引用当前实体的所有记录，支持分页。</p>
     * 
     * @param entityId 实体 ID
     * @param entityTypeCode 业务类型编码
     * @param modelCode Model 编码
     * @param pageParam 分页参数
     * @return 分页的实体列表
     */
    PageResult<EntitySimpleVO> getReverseRelationsByModel(Long entityId, String entityTypeCode, String modelCode, PageParam pageParam);

    /**
     * 获取关联统计
     * 
     * <p>统计引用当前实体的记录数量，按 Model 分组。</p>
     * 
     * @param entityId 实体 ID
     * @return 关联统计信息
     * @deprecated 使用 {@link #getRelationStatistics(Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    RelationStatisticsVO getRelationStatistics(Long entityId);

    /**
     * 获取关联统计
     * 
     * <p>统计引用当前实体的记录数量，按 Model 分组。</p>
     * 
     * @param entityId 实体 ID
     * @param entityTypeCode 业务类型编码
     * @return 关联统计信息
     */
    RelationStatisticsVO getRelationStatistics(Long entityId, String entityTypeCode);

    /**
     * 按关联字段分组统计
     * 
     * <p>对指定 Model 的数据按关联字段进行聚合统计。</p>
     * 
     * @param reqVO 聚合查询请求
     * @return 聚合结果列表
     */
    List<AggregateResultVO> aggregateByRelation(AggregateQueryReqVO reqVO);

    /**
     * 发现所有关联到指定 Model 的关联关系
     * 
     * <p>扫描所有 Model 的 ENTITY_REF 类型字段，找出引用指定 Model 的字段。</p>
     * 
     * @param modelCode Model 编码
     * @return 关联发现结果列表
     */
    List<RelationDiscoveryVO> discoverRelations(String modelCode);

    /**
     * 发现所有关联到指定 Model 的关联关系（带缓存）
     * 
     * <p>使用缓存优化性能，缓存过期时间可配置。</p>
     * 
     * @param modelCode Model 编码
     * @param useCache 是否使用缓存
     * @return 关联发现结果列表
     */
    List<RelationDiscoveryVO> discoverRelations(String modelCode, boolean useCache);

    /**
     * 清除关联发现缓存
     * 
     * <p>当字段定义变更时调用，清除相关缓存。</p>
     * 
     * @param modelCode Model 编码（为 null 时清除所有缓存）
     */
    void clearDiscoveryCache(String modelCode);

    /**
     * 获取实体的所有关联信息（正向 + 反向）
     * 
     * <p>一次性获取实体的所有关联信息，包括正向和反向关联。</p>
     * 
     * @param entityId 实体 ID
     * @return 完整的关联信息
     * @deprecated 使用 {@link #getEntityRelationInfo(Long, String)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    EntityRelationInfoVO getEntityRelationInfo(Long entityId);

    /**
     * 获取实体的所有关联信息（正向 + 反向）
     * 
     * <p>一次性获取实体的所有关联信息，包括正向和反向关联。</p>
     * 
     * @param entityId 实体 ID
     * @param entityTypeCode 业务类型编码
     * @return 完整的关联信息
     */
    EntityRelationInfoVO getEntityRelationInfo(Long entityId, String entityTypeCode);

    /**
     * 批量获取实体的反向关联统计
     * 
     * <p>批量查询多个实体的反向关联数量，用于列表展示。</p>
     * 
     * @param entityIds 实体 ID 列表
     * @return 实体 ID -> 关联数量 的映射
     * @deprecated 使用 {@link #batchGetReverseRelationCounts(String, List)} 代替，需要传递 entityTypeCode 以支持动态表路由
     */
    @Deprecated
    Map<Long, Long> batchGetReverseRelationCounts(List<Long> entityIds);

    /**
     * 批量获取实体的反向关联统计
     * 
     * <p>批量查询多个实体的反向关联数量，用于列表展示。</p>
     * 
     * @param entityTypeCode 业务类型编码
     * @param entityIds 实体 ID 列表
     * @return 实体 ID -> 关联数量 的映射
     */
    Map<Long, Long> batchGetReverseRelationCounts(String entityTypeCode, List<Long> entityIds);

    /**
     * 清除统计结果缓存
     * 
     * <p>当 Entity 变更时调用，清除相关的统计缓存。</p>
     * <p>缓存失效机制：Entity 创建/更新/删除时应调用此方法。</p>
     * 
     * @param entityId 实体 ID（为 null 时清除所有缓存）
     */
    void clearStatisticsCache(Long entityId);
}
