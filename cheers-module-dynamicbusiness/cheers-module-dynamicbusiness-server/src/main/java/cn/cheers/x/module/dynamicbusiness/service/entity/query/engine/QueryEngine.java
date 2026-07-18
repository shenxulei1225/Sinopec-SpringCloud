package cn.cheers.x.module.dynamicbusiness.service.entity.query.engine;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.AggregateResult;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldAggregateRequest;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldQueryRequest;

import java.util.function.Consumer;

/**
 * 查询引擎接口
 * 
 * 定义查询引擎的统一能力，支持多种实现（PostgreSQL、MySQL、Elasticsearch）
 * 业务代码通过此接口进行查询，无需关心底层实现差异
 * 
 * @author 系统
 */
public interface QueryEngine {

    /**
     * 获取引擎类型
     * 
     * @return 引擎类型标识（如 "postgresql"、"mysql"、"elasticsearch"）
     */
    String getType();

    /**
     * 执行条件查询
     * 
     * 根据查询条件查询 Entity 列表，支持分页
     * 
     * @param request 查询请求，包含模型ID、查询条件、排序和分页参数
     * @return 分页查询结果
     */
    PageResult<EntityDO> query(FieldQueryRequest request);

    /**
     * 执行统计聚合
     * 
     * 对扩展字段进行统计聚合操作，支持计数、求和、平均值、最大值、最小值
     * 支持按字段分组统计
     * 
     * @param request 聚合请求，包含模型ID、聚合类型、聚合字段和分组字段
     * @return 聚合结果
     */
    AggregateResult aggregate(FieldAggregateRequest request);

    /**
     * 执行计数查询
     * 
     * 统计满足条件的 Entity 数量
     * 
     * @param request 查询请求，包含模型ID和查询条件
     * @return 满足条件的记录数
     */
    Long count(FieldQueryRequest request);

    /**
     * 同步数据到索引
     * 
     * 当 Entity 保存时，将可查询字段同步到查询索引
     * 不同引擎有不同的同步策略：
     * - PostgreSQL: 同步到 entity_field_index 表
     * - Elasticsearch: 同步到 ES 索引
     * 
     * @param entity 要同步的 Entity
     */
    void syncToIndex(EntityDO entity);

    /**
     * 从索引删除数据
     * 
     * 当 Entity 删除时，从查询索引中删除对应数据
     * 
     * @param entityId 要删除的 Entity ID
     */
    void deleteFromIndex(Long entityId);

    /**
     * 重建索引
     * 
     * 全量重建指定 Model 的查询索引
     * 用于初始化部署、字段配置变更、数据修复等场景
     * 
     * @param modelId 模型ID
     * @param progressCallback 进度回调，参数为已处理的记录数
     */
    void rebuildIndex(Long modelId, Consumer<Integer> progressCallback);

    /**
     * 检查引擎是否可用
     * 
     * @return 引擎是否可用
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * 获取引擎优先级
     * 数值越小优先级越高
     * 
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
}
