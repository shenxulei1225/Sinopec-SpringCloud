package cn.cheers.x.module.dynamicbusiness.service.entity.query;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.AggregateResult;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldAggregateRequest;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldQueryRequest;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.vo.SearchableFieldVO;

import java.util.List;

/**
 * 扩展字段查询服务接口
 *
 * <p>提供统一的扩展字段查询能力，屏蔽底层查询引擎差异。
 * 业务代码通过此接口进行查询，无需关心底层使用的是 PostgreSQL、MySQL 还是 Elasticsearch。</p>
 *
 * <h3>核心功能</h3>
 * <ul>
 *   <li>条件查询：支持等值、范围、模糊、包含、空值等多种查询条件</li>
 *   <li>统计聚合：支持 count、sum、avg、max、min 等聚合操作</li>
 *   <li>分组统计：支持按字段分组进行统计</li>
 *   <li>排序分页：支持按扩展字段排序和分页</li>
 *   <li>可查询字段：获取 Model 的可查询字段列表</li>
 * </ul>
 *
 * <h3>查询条件验证</h3>
 * <p>服务会自动验证查询条件的有效性：</p>
 * <ul>
 *   <li>字段可查询性：只有 is_searchable=true 的字段才能参与查询</li>
 *   <li>条件数量限制：单次查询最多 10 个条件</li>
 *   <li>操作符有效性：验证操作符是否支持</li>
 *   <li>值类型匹配：验证查询值与字段类型是否匹配</li>
 * </ul>
 *
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-001: 系统必须提供统一的扩展字段查询服务接口，屏蔽底层查询引擎差异</li>
 *   <li>FR-003: 系统必须在 Framework 层提供查询引擎的基础抽象和通用工具</li>
 *   <li>FR-004: 系统必须在 System 模块提供查询服务的具体业务实现</li>
 *   <li>FR-022: 系统必须支持查询某个 Model 的所有可查询字段列表</li>
 *   <li>BR-QRY-001: 只有 is_searchable=true 的字段才能参与查询条件</li>
 *   <li>BR-QRY-002: 单次查询最多返回 1000 条记录</li>
 *   <li>BR-QRY-003: 查询条件最多 10 个</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
public interface EntityFieldQueryService {

    /**
     * 条件查询
     *
     * <p>根据查询条件查询 Entity 列表，支持分页。</p>
     *
     * <h4>查询条件验证</h4>
     * <ul>
     *   <li>验证 modelId 是否存在</li>
     *   <li>验证查询字段是否可查询（is_searchable=true）</li>
     *   <li>验证查询条件数量不超过 10 个</li>
     *   <li>验证操作符是否有效</li>
     * </ul>
     *
     * @param request 查询请求，包含模型ID、查询条件、排序和分页参数
     * @return 分页查询结果
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在、字段不可查询、条件数量超限等
     */
    PageResult<EntityDO> query(FieldQueryRequest request);

    /**
     * 统计聚合
     *
     * <p>对扩展字段进行统计聚合操作，支持计数、求和、平均值、最大值、最小值。
     * 支持按字段分组统计。</p>
     *
     * <h4>聚合类型</h4>
     * <ul>
     *   <li>COUNT: 计数统计，可以不指定字段（统计总数）或指定字段（统计非空值数量）</li>
     *   <li>SUM: 求和统计，必须指定数值类型字段</li>
     *   <li>AVG: 平均值统计，必须指定数值类型字段</li>
     *   <li>MAX: 最大值统计，支持数值、日期、字符串类型</li>
     *   <li>MIN: 最小值统计，支持数值、日期、字符串类型</li>
     * </ul>
     *
     * @param request 聚合请求，包含模型ID、聚合类型、聚合字段和分组字段
     * @return 聚合结果
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在、字段不可查询、聚合类型不支持等
     */
    AggregateResult aggregate(FieldAggregateRequest request);

    /**
     * 计数统计
     *
     * <p>统计满足条件的 Entity 数量。</p>
     *
     * @param request 查询请求，包含模型ID和查询条件
     * @return 满足条件的记录数
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在、字段不可查询等
     */
    Long count(FieldQueryRequest request);

    /**
     * 获取可查询字段列表
     *
     * <p>返回指定 Model 下所有标记为 is_searchable=true 的字段信息，
     * 供前端动态生成查询表单。</p>
     *
     * <h4>返回信息</h4>
     * <ul>
     *   <li>字段编码（fieldCode）</li>
     *   <li>字段名称（fieldName）</li>
     *   <li>字段类型（fieldType）</li>
     *   <li>支持的操作符列表（supportedOperators）</li>
     *   <li>是否可排序（sortable）</li>
     * </ul>
     *
     * @param modelId 模型ID
     * @return 可查询字段列表
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在
     */
    List<SearchableFieldVO> getSearchableFields(Long modelId);

    /**
     * 获取可查询字段列表（通过 Model 编码）
     *
     * @param modelCode 模型编码
     * @return 可查询字段列表
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果 Model 不存在
     */
    List<SearchableFieldVO> getSearchableFieldsByModelCode(String modelCode);

    /**
     * 验证查询请求
     *
     * <p>验证查询请求的有效性，包括：</p>
     * <ul>
     *   <li>Model 是否存在</li>
     *   <li>查询字段是否可查询</li>
     *   <li>查询条件数量是否超限</li>
     *   <li>操作符是否有效</li>
     *   <li>值类型是否匹配</li>
     * </ul>
     *
     * @param request 查询请求
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果验证失败
     */
    void validateQueryRequest(FieldQueryRequest request);

    /**
     * 验证聚合请求
     *
     * @param request 聚合请求
     * @throws cn.cheers.x.framework.common.exception.ServiceException 
     *         如果验证失败
     */
    void validateAggregateRequest(FieldAggregateRequest request);

    /**
     * 获取当前使用的查询引擎类型
     *
     * @return 引擎类型（postgresql、mysql、elasticsearch）
     */
    String getCurrentEngineType();

    /**
     * 获取所有可用的查询引擎类型
     *
     * @return 引擎类型列表
     */
    List<String> getAvailableEngineTypes();
}
