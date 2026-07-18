package cn.cheers.x.module.dynamicbusiness.service.entity.query;

import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query.*;
import cn.cheers.x.module.dynamicbusiness.convert.entity.EntityConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.index.FieldIndexService;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.*;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.AggregateType;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.LogicType;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.vo.SearchableFieldVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants.*;

/**
 * 通用 Entity 查询服务实现类
 *
 * <p>提供元数据驱动的通用查询能力，通过 modelCode 动态指定查询的 Model，
 * 实现零代码查询。</p>
 *
 * <h3>核心职责</h3>
 * <ul>
 *   <li>请求转换：将 GenericQueryRequest 转换为 FieldQueryRequest</li>
 *   <li>Model 解析：根据 modelCode 获取 Model 信息</li>
 *   <li>查询委托：委托给 EntityFieldQueryService 执行查询</li>
 *   <li>结果转换：将 EntityDO 转换为 EntityRespVO</li>
 *   <li>可查询字段：获取 Model 的可查询字段列表并转换为 API VO</li>
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
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class GenericEntityQueryServiceImpl implements GenericEntityQueryService {

    private final EntityFieldQueryService entityFieldQueryService;
    private final FieldIndexService fieldIndexService;
    private final ModelMapper modelMapper;

    @Override
    public PageResult<EntityRespVO> query(@Valid GenericQueryRequest request) {
        // 1. 验证请求
        validateRequest(request);

        // 2. 获取 Model
        ModelDO model = getModelByCode(request.getModelCode());

        // 3. 转换为内部查询请求
        FieldQueryRequest fieldQueryRequest = convertToFieldQueryRequest(request, model.getId());

        // 4. 执行查询
        log.debug("执行通用查询: modelCode={}, modelId={}, conditions={}",
            request.getModelCode(), model.getId(),
            request.hasConditions() ? request.getConditions().size() : 0);

        PageResult<EntityDO> pageResult = entityFieldQueryService.query(fieldQueryRequest);

        // 5. 转换结果
        List<EntityRespVO> voList = EntityConvert.INSTANCE.convertList(pageResult.getList());

        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    public AggregateResultVO aggregate(@Valid GenericAggregateRequest request) {
        // 1. 验证请求
        validateRequest(request);

        // 2. 获取 Model
        ModelDO model = getModelByCode(request.getModelCode());

        // 3. 转换为内部聚合请求
        FieldAggregateRequest fieldAggregateRequest = convertToFieldAggregateRequest(request, model.getId());

        // 4. 执行聚合
        log.debug("执行通用聚合: modelCode={}, modelId={}, type={}, field={}",
            request.getModelCode(), model.getId(),
            request.getAggregateType(), request.getFieldCode());

        AggregateResult aggregateResult = entityFieldQueryService.aggregate(fieldAggregateRequest);

        // 5. 转换结果
        return convertToAggregateResultVO(aggregateResult);
    }

    @Override
    public Long count(@Valid GenericQueryRequest request) {
        // 1. 验证请求
        validateRequest(request);

        // 2. 获取 Model
        ModelDO model = getModelByCode(request.getModelCode());

        // 3. 转换为内部查询请求
        FieldQueryRequest fieldQueryRequest = convertToFieldQueryRequest(request, model.getId());

        // 4. 执行计数
        log.debug("执行通用计数: modelCode={}, modelId={}", request.getModelCode(), model.getId());

        return entityFieldQueryService.count(fieldQueryRequest);
    }

    @Override
    public List<SearchableFieldRespVO> getSearchableFields(String modelCode) {
        // 1. 获取 Model
        ModelDO model = getModelByCode(modelCode);

        // 2. 获取可查询字段列表
        List<SearchableFieldVO> serviceVOList = entityFieldQueryService.getSearchableFields(model.getId());

        // 3. 转换为 API VO
        return serviceVOList.stream()
            .map(this::convertToSearchableFieldRespVO)
            .collect(Collectors.toList());
    }

    @Override
    public void validateRequest(@Valid GenericQueryRequest request) {
        // 1. 验证 modelCode 不为空
        if (request.getModelCode() == null || request.getModelCode().isBlank()) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, "null");
        }

        // 2. 验证 Model 存在
        ModelDO model = modelMapper.selectByCode(request.getModelCode());
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, request.getModelCode());
        }

        // 3. 验证查询条件数量
        if (request.hasConditions() && request.getConditions().size() > 10) {
            throw exception(TOO_MANY_CONDITIONS, request.getConditions().size(), 10);
        }

        // 4. 验证查询字段可查询性
        if (request.hasConditions()) {
            for (QueryConditionVO condition : request.getConditions()) {
                if (!fieldIndexService.isFieldSearchable(model.getId(), condition.getFieldCode())) {
                    throw exception(FIELD_NOT_SEARCHABLE, condition.getFieldCode());
                }
            }
        }

        // 5. 验证排序字段可查询性
        if (request.hasSorts()) {
            for (QuerySortVO sort : request.getSorts()) {
                if (!fieldIndexService.isFieldSearchable(model.getId(), sort.getFieldCode())) {
                    throw exception(FIELD_NOT_SEARCHABLE, sort.getFieldCode());
                }
            }
        }
    }

    @Override
    public void validateRequest(@Valid GenericAggregateRequest request) {
        // 1. 验证 modelCode 不为空
        if (request.getModelCode() == null || request.getModelCode().isBlank()) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, "null");
        }

        // 2. 验证 Model 存在
        ModelDO model = modelMapper.selectByCode(request.getModelCode());
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, request.getModelCode());
        }

        // 3. 验证聚合类型
        AggregateType aggregateType = request.getAggregateType();
        if (aggregateType == null) {
            throw exception(UNSUPPORTED_AGGREGATE_TYPE, "null");
        }

        // 4. 验证聚合字段（SUM、AVG、MAX、MIN 必须指定字段）
        String fieldCode = request.getFieldCode();
        if (aggregateType.requiresField() && (fieldCode == null || fieldCode.isEmpty())) {
            throw exception(FIELD_NOT_EXISTS, "聚合字段");
        }

        // 5. 验证聚合字段可查询性
        if (fieldCode != null && !fieldCode.isEmpty()) {
            if (!fieldIndexService.isFieldSearchable(model.getId(), fieldCode)) {
                throw exception(FIELD_NOT_SEARCHABLE, fieldCode);
            }
        }

        // 6. 验证分组字段可查询性
        String groupByFieldCode = request.getGroupByFieldCode();
        if (groupByFieldCode != null && !groupByFieldCode.isEmpty()) {
            if (!fieldIndexService.isFieldSearchable(model.getId(), groupByFieldCode)) {
                throw exception(FIELD_NOT_SEARCHABLE, groupByFieldCode);
            }
        }

        // 7. 验证条件字段可查询性
        if (request.hasConditions()) {
            for (QueryConditionVO condition : request.getConditions()) {
                if (!fieldIndexService.isFieldSearchable(model.getId(), condition.getFieldCode())) {
                    throw exception(FIELD_NOT_SEARCHABLE, condition.getFieldCode());
                }
            }
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 根据 modelCode 获取 Model
     */
    private ModelDO getModelByCode(String modelCode) {
        ModelDO model = modelMapper.selectByCode(modelCode);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, modelCode);
        }
        return model;
    }

    /**
     * 将 GenericQueryRequest 转换为 FieldQueryRequest
     */
    private FieldQueryRequest convertToFieldQueryRequest(GenericQueryRequest request, Long modelId) {
        FieldQueryRequest.FieldQueryRequestBuilder builder = FieldQueryRequest.builder()
            .modelId(modelId)
            .logic(request.getLogic() != null ? request.getLogic() : LogicType.AND);

        // 转换查询条件
        if (request.hasConditions()) {
            List<FieldCondition> conditions = request.getConditions().stream()
                .map(this::convertToFieldCondition)
                .collect(Collectors.toList());
            builder.conditions(conditions);
        }

        // 转换排序条件
        if (request.hasSorts()) {
            List<FieldSort> sorts = request.getSorts().stream()
                .map(this::convertToFieldSort)
                .collect(Collectors.toList());
            builder.sorts(sorts);
        }

        // 设置分页参数
        PageParam pageParam = request.getPageParamOrDefault();
        builder.pageParam(pageParam);

        return builder.build();
    }

    /**
     * 将 GenericAggregateRequest 转换为 FieldAggregateRequest
     */
    private FieldAggregateRequest convertToFieldAggregateRequest(GenericAggregateRequest request, Long modelId) {
        FieldAggregateRequest.FieldAggregateRequestBuilder builder = FieldAggregateRequest.builder()
            .modelId(modelId)
            .aggregateType(request.getAggregateType())
            .fieldCode(request.getFieldCode())
            .groupByFieldCode(request.getGroupByFieldCode());

        // 转换查询条件
        if (request.hasConditions()) {
            List<FieldCondition> conditions = request.getConditions().stream()
                .map(this::convertToFieldCondition)
                .collect(Collectors.toList());
            builder.conditions(conditions);
        }

        return builder.build();
    }

    /**
     * 将 QueryConditionVO 转换为 FieldCondition
     */
    private FieldCondition convertToFieldCondition(QueryConditionVO vo) {
        return FieldCondition.builder()
            .fieldCode(vo.getFieldCode())
            .operator(vo.getOperator())
            .value(vo.getValue())
            .value2(vo.getValue2())
            .build();
    }

    /**
     * 将 QuerySortVO 转换为 FieldSort
     */
    private FieldSort convertToFieldSort(QuerySortVO vo) {
        return FieldSort.builder()
            .fieldCode(vo.getFieldCode())
            .direction(vo.getDirection())
            .build();
    }

    /**
     * 将 AggregateResult 转换为 AggregateResultVO
     */
    private AggregateResultVO convertToAggregateResultVO(AggregateResult result) {
        AggregateResultVO.AggregateResultVOBuilder builder = AggregateResultVO.builder()
            .value(result.getValue());

        // 转换分组结果
        if (result.isGrouped() && result.getGroups() != null) {
            List<AggregateResultVO.GroupResultVO> groups = result.getGroups().stream()
                .map(this::convertToGroupResultVO)
                .collect(Collectors.toList());
            builder.groups(groups);
        }

        return builder.build();
    }

    /**
     * 将 GroupResult 转换为 GroupResultVO
     */
    private AggregateResultVO.GroupResultVO convertToGroupResultVO(GroupResult result) {
        return AggregateResultVO.GroupResultVO.builder()
            .groupKey(result.getGroupKey())
            .value(result.getValue())
            .count(result.getCount())
            .build();
    }

    /**
     * 将 SearchableFieldVO（服务层）转换为 SearchableFieldRespVO（API 层）
     */
    private SearchableFieldRespVO convertToSearchableFieldRespVO(SearchableFieldVO serviceVO) {
        SearchableFieldRespVO.SearchableFieldRespVOBuilder builder = SearchableFieldRespVO.builder()
            .fieldId(serviceVO.getFieldId())
            .fieldCode(serviceVO.getFieldCode())
            .fieldName(serviceVO.getFieldName())
            .fieldType(serviceVO.getFieldType())
            .supportedOperators(serviceVO.getSupportedOperators())
            .sortable(serviceVO.getSortable())
            .description(serviceVO.getDescription())
            .unit(serviceVO.getUnit());

        // 转换选项列表（如果有）
        if (serviceVO.getOptions() != null && !serviceVO.getOptions().isEmpty()) {
            // 选项是 JSON 字符串，需要解析
            // 这里简化处理，实际可能需要 JSON 解析
            builder.options(parseFieldOptions(serviceVO.getOptions()));
        }

        return builder.build();
    }

    /**
     * 解析字段选项 JSON
     */
    private List<SearchableFieldRespVO.FieldOptionVO> parseFieldOptions(String optionsJson) {
        // 简化实现：如果需要完整解析，可以使用 Jackson
        // 这里返回空列表，实际实现时需要解析 JSON
        if (optionsJson == null || optionsJson.isBlank()) {
            return new ArrayList<>();
        }

        try {
            // 使用 Jackson 解析 JSON 数组
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.core.type.TypeReference<List<SearchableFieldRespVO.FieldOptionVO>> typeRef =
                new com.fasterxml.jackson.core.type.TypeReference<>() {};
            return objectMapper.readValue(optionsJson, typeRef);
        } catch (Exception e) {
            log.warn("解析字段选项失败: {}", optionsJson, e);
            return new ArrayList<>();
        }
    }

}
