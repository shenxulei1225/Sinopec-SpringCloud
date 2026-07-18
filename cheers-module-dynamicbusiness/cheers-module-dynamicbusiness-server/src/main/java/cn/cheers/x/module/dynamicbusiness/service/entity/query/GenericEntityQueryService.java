package cn.cheers.x.module.dynamicbusiness.service.entity.query;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query.AggregateResultVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query.GenericAggregateRequest;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query.GenericQueryRequest;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query.SearchableFieldRespVO;

import java.util.List;

/**
 * 通用 Entity 查询服务接口
 *
 * <p>提供元数据驱动的通用查询能力，支持通过 modelCode 动态指定查询的 Model，
 * 实现零代码查询。</p>
 *
 * <h3>核心功能</h3>
 * <ul>
 *   <li>通用查询：通过 modelCode 动态查询任意 Model 的 Entity</li>
 *   <li>通用聚合：通过 modelCode 动态聚合任意 Model 的 Entity</li>
 *   <li>可查询字段：获取 Model 的可查询字段列表，供前端动态生成查询表单</li>
 *   <li>查询验证：验证查询条件的有效性（字段存在、可查询、类型匹配）</li>
 *   <li>结果转换：根据 Model 字段定义动态转换查询结果</li>
 * </ul>
 *
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-051: 系统必须提供元数据驱动的通用查询服务，无需为每个 Model 编写代码</li>
 *   <li>FR-052: 系统必须支持通过 modelCode 参数动态指定查询的 Model</li>
 *   <li>FR-053: 系统必须提供获取 Model 可查询字段列表的 API，供前端动态生成查询表单</li>
 *   <li>FR-054: 系统必须支持运行时验证查询字段的有效性</li>
 *   <li>FR-055: 系统必须支持动态结果转换，根据 Model 字段定义转换查询结果</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
public interface GenericEntityQueryService {

    /**
     * 通用条件查询
     *
     * <p>根据 modelCode 和查询条件查询 Entity 列表，支持分页。
     * 查询结果会根据 Model 字段定义进行动态转换。</p>
     *
     * <h4>查询流程</h4>
     * <ol>
     *   <li>根据 modelCode 获取 Model 信息</li>
     *   <li>验证查询条件的有效性</li>
     *   <li>转换为内部查询请求</li>
     *   <li>执行查询</li>
     *   <li>转换查询结果为 VO</li>
     * </ol>
     *
     * @param request 通用查询请求
     * @return 分页查询结果
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在、字段不可查询、条件数量超限等
     */
    PageResult<EntityRespVO> query(GenericQueryRequest request);

    /**
     * 通用聚合查询
     *
     * <p>根据 modelCode 和聚合参数进行统计聚合。
     * 支持计数、求和、平均值、最大值、最小值，以及分组统计。</p>
     *
     * @param request 通用聚合请求
     * @return 聚合结果
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在、字段不可查询、聚合类型不支持等
     */
    AggregateResultVO aggregate(GenericAggregateRequest request);

    /**
     * 通用计数查询
     *
     * <p>统计满足条件的 Entity 数量。</p>
     *
     * @param request 通用查询请求
     * @return 满足条件的记录数
     */
    Long count(GenericQueryRequest request);

    /**
     * 获取可查询字段列表
     *
     * <p>返回指定 Model 下所有标记为 is_searchable=true 的字段信息，
     * 供前端动态生成查询表单。</p>
     *
     * <h4>返回信息</h4>
     * <ul>
     *   <li>字段编码、名称、类型</li>
     *   <li>支持的操作符列表</li>
     *   <li>是否可排序</li>
     *   <li>选项列表（SELECT 类型）</li>
     *   <li>引用 Model 列表（ENTITY_REF 类型）</li>
     * </ul>
     *
     * @param modelCode Model 编码
     * @return 可查询字段列表
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在
     */
    List<SearchableFieldRespVO> getSearchableFields(String modelCode);

    /**
     * 验证通用查询请求
     *
     * <p>验证查询请求的有效性，包括：</p>
     * <ul>
     *   <li>Model 是否存在</li>
     *   <li>查询字段是否存在且可查询</li>
     *   <li>查询条件数量是否超限</li>
     *   <li>操作符是否有效</li>
     *   <li>值类型是否匹配</li>
     * </ul>
     *
     * @param request 通用查询请求
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果验证失败
     */
    void validateRequest(GenericQueryRequest request);

    /**
     * 验证通用聚合请求
     *
     * @param request 通用聚合请求
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果验证失败
     */
    void validateRequest(GenericAggregateRequest request);
}
