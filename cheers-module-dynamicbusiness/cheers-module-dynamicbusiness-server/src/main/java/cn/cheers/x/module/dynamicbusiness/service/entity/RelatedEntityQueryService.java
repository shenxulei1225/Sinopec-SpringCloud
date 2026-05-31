package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.RelatedEntityRespVO;

import java.util.List;

/**
 * 关联 Entity 查询服务接口
 * 
 * <p>提供反向查询能力，查询所有关联到指定 Entity 的其他 Entity。</p>
 * 
 * <h3>业务场景</h3>
 * <p>例如：查询某个"计划"被哪些"任务"关联</p>
 * <ul>
 *   <li>计划 Entity ID = 123</li>
 *   <li>任务 Model 有 plan_id 字段关联到计划</li>
 *   <li>反向查询返回所有 plan_id = 123 的任务</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-078: 系统必须支持反向查询（查询关联到指定 Entity 的所有 Entity）</li>
 *   <li>FR-086: 系统必须提供反向查询 API（GET /api/entity/{id}/related）</li>
 *   <li>BR-REL-005: 反向查询通过 GET /api/entity/{id}/related API 提供</li>
 * </ul>
 * 
 * @author yudao
 */
public interface RelatedEntityQueryService {

    /**
     * 查询关联到指定 Entity 的所有 Entity
     * 
     * <p>反向查询：查询所有通过关联字段引用了指定 Entity 的其他 Entity。</p>
     * 
     * @param entityId 目标 Entity ID
     * @param modelCode 过滤条件：只返回指定 Model 的关联 Entity（可选）
     * @return 关联 Entity 列表
     */
    List<RelatedEntityRespVO> getRelatedEntities(Long entityId, String modelCode);

    /**
     * 查询关联到指定 Entity 的所有 Entity（支持指定业务类型）
     * 
     * <p>反向查询：查询所有通过关联字段引用了指定 Entity 的其他 Entity。
     * 通过 businessTypeCode 参数可以直接路由到对应的存储策略（动态表或通用表）。</p>
     * 
     * @param entityId 目标 Entity ID
     * @param modelCode 过滤条件：只返回指定 Model 的关联 Entity（可选）
     * @param businessTypeCode 目标 Entity 的业务类型编码（可选，用于路由到正确的存储策略）
     * @return 关联 Entity 列表
     */
    List<RelatedEntityRespVO> getRelatedEntities(Long entityId, String modelCode, String businessTypeCode);

    /**
     * 统计关联到指定 Entity 的 Entity 数量
     * 
     * @param entityId 目标 Entity ID
     * @param modelCode 过滤条件：只统计指定 Model 的关联 Entity（可选）
     * @return 关联 Entity 数量
     */
    Long countRelatedEntities(Long entityId, String modelCode);

    /**
     * 统计关联到指定 Entity 的 Entity 数量（支持指定业务类型）
     * 
     * @param entityId 目标 Entity ID
     * @param modelCode 过滤条件：只统计指定 Model 的关联 Entity（可选）
     * @param businessTypeCode 目标 Entity 的业务类型编码（可选，用于路由到正确的存储策略）
     * @return 关联 Entity 数量
     */
    Long countRelatedEntities(Long entityId, String modelCode, String businessTypeCode);
}
