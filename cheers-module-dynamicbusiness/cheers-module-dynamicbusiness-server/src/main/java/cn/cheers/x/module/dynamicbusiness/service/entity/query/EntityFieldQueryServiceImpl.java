package cn.cheers.x.module.dynamicbusiness.service.entity.query;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.index.FieldIndexService;
import cn.cheers.x.module.dynamicbusiness.service.field.SmartSearchableService;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.AggregateResult;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldAggregateRequest;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldCondition;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldQueryRequest;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.engine.QueryEngine;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.AggregateType;
import cn.cheers.x.module.dynamicbusiness.service.entity.query.vo.SearchableFieldVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ExtendFieldQueryErrorCodeConstants.*;

/**
 * 扩展字段查询服务实现类
 *
 * <p>提供统一的扩展字段查询能力，通过 QueryEngineRouter 路由到具体的查询引擎。</p>
 *
 * <h3>核心职责</h3>
 * <ul>
 *   <li>查询请求验证：验证字段可查询性、条件数量限制等</li>
 *   <li>查询引擎路由：通过 QueryEngineRouter 获取当前引擎</li>
 *   <li>查询执行：委托给具体的查询引擎执行</li>
 *   <li>可查询字段管理：获取 Model 的可查询字段列表</li>
 * </ul>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>BR-QRY-001: 只有 is_searchable=true 的字段才能参与查询条件</li>
 *   <li>BR-QRY-002: 单次查询最多返回 1000 条记录</li>
 *   <li>BR-QRY-003: 查询条件最多 10 个</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class EntityFieldQueryServiceImpl implements EntityFieldQueryService {

    /**
     * 查询条件最大数量
     */
    private static final int MAX_CONDITIONS = 10;

    /**
     * 单次查询最大返回记录数
     */
    private static final int MAX_PAGE_SIZE = 1000;

    private final QueryEngineRouter queryEngineRouter;
    private final FieldIndexService fieldIndexService;
    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    private final SmartSearchableService smartSearchableService;

    @Override
    public PageResult<EntityDO> query(@Valid FieldQueryRequest request) {
        // 1. 验证查询请求
        validateQueryRequest(request);

        // 2. 限制分页大小
        limitPageSize(request);

        // 3. 获取查询引擎并执行查询
        QueryEngine engine = queryEngineRouter.getEngine();
        log.debug("执行查询: modelId={}, engine={}, conditions={}",
            request.getModelId(), engine.getType(),
            request.hasConditions() ? request.getConditions().size() : 0);

        return engine.query(request);
    }

    @Override
    public AggregateResult aggregate(@Valid FieldAggregateRequest request) {
        // 1. 验证聚合请求
        validateAggregateRequest(request);

        // 2. 获取查询引擎并执行聚合
        QueryEngine engine = queryEngineRouter.getEngine();
        log.debug("执行聚合: modelId={}, engine={}, type={}, field={}",
            request.getModelId(), engine.getType(),
            request.getAggregateType(), request.getFieldCode());

        return engine.aggregate(request);
    }

    @Override
    public Long count(@Valid FieldQueryRequest request) {
        // 1. 验证查询请求
        validateQueryRequest(request);

        // 2. 获取查询引擎并执行计数
        QueryEngine engine = queryEngineRouter.getEngine();
        log.debug("执行计数: modelId={}, engine={}", request.getModelId(), engine.getType());

        return engine.count(request);
    }

    @Override
    public List<SearchableFieldVO> getSearchableFields(Long modelId) {
        // 1. 验证 Model 存在
        validateModelExists(modelId);

        // 2. 获取可查询字段列表
        List<FieldDO> fields = fieldIndexService.getSearchableFields(modelId);

        // 3. 转换为 VO（传递 modelId 以便从模型字段分配中读取 isSortable）
        return fields.stream()
            .map(field -> convertToSearchableFieldVO(field, modelId))
            .collect(Collectors.toList());
    }

    @Override
    public List<SearchableFieldVO> getSearchableFieldsByModelCode(String modelCode) {
        // 1. 获取 Model
        ModelDO model = modelMapper.selectByCode(modelCode);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, modelCode);
        }

        // 2. 获取可查询字段列表
        return getSearchableFields(model.getId());
    }

    @Override
    public void validateQueryRequest(FieldQueryRequest request) {
        // 1. 验证 Model 存在
        validateModelExists(request.getModelId());

        // 2. 验证查询条件数量
        if (request.hasConditions() && request.getConditions().size() > MAX_CONDITIONS) {
            throw exception(TOO_MANY_CONDITIONS, request.getConditions().size(), MAX_CONDITIONS);
        }

        // 3. 验证查询字段可查询性
        if (request.hasConditions()) {
            validateFieldsSearchable(request.getModelId(), request.getConditions());
        }

        // 4. 验证排序字段可排序性
        if (request.hasSorts()) {
            validateFieldsSortable(request.getModelId(), request.getSorts());
        }
    }

    @Override
    public void validateAggregateRequest(FieldAggregateRequest request) {
        // 1. 验证 Model 存在
        validateModelExists(request.getModelId());

        // 2. 验证聚合类型
        AggregateType aggregateType = request.getAggregateType();
        if (aggregateType == null) {
            throw exception(UNSUPPORTED_AGGREGATE_TYPE, "null");
        }

        // 3. 验证聚合字段（SUM、AVG 必须指定字段）
        String fieldCode = request.getFieldCode();
        if (aggregateType.requiresField() && (fieldCode == null || fieldCode.isEmpty())) {
            throw exception(FIELD_NOT_EXISTS, "聚合字段");
        }

        // 4. 验证聚合字段可查询性
        if (fieldCode != null && !fieldCode.isEmpty()) {
            if (!fieldIndexService.isFieldSearchable(request.getModelId(), fieldCode)) {
                throw exception(FIELD_NOT_SEARCHABLE, fieldCode);
            }
        }

        // 5. 验证分组字段可查询性
        String groupByFieldCode = request.getGroupByFieldCode();
        if (groupByFieldCode != null && !groupByFieldCode.isEmpty()) {
            if (!fieldIndexService.isFieldSearchable(request.getModelId(), groupByFieldCode)) {
                throw exception(FIELD_NOT_SEARCHABLE, groupByFieldCode);
            }
        }

        // 6. 验证条件字段可查询性
        if (request.hasConditions()) {
            validateFieldsSearchable(request.getModelId(), request.getConditions());
        }
    }

    @Override
    public String getCurrentEngineType() {
        return queryEngineRouter.getCurrentEngineType();
    }

    @Override
    public List<String> getAvailableEngineTypes() {
        return queryEngineRouter.getAvailableEngineTypes();
    }

    // ==================== 私有方法 ====================

    /**
     * 验证 Model 存在
     */
    private void validateModelExists(Long modelId) {
        if (modelId == null) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, "null");
        }

        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw exception(MODEL_NOT_EXISTS_FOR_QUERY, modelId);
        }
    }

    /**
     * 验证查询字段可查询性
     */
    private void validateFieldsSearchable(Long modelId, List<FieldCondition> conditions) {
        Set<String> checkedFields = new HashSet<>();

        for (FieldCondition condition : conditions) {
            String fieldCode = condition.getFieldCode();

            // 避免重复检查
            if (checkedFields.contains(fieldCode)) {
                continue;
            }
            checkedFields.add(fieldCode);

            // 检查字段是否可查询
            if (!fieldIndexService.isFieldSearchable(modelId, fieldCode)) {
                throw exception(FIELD_NOT_SEARCHABLE, fieldCode);
            }
        }
    }

    /**
     * 验证排序字段可排序性
     */
    private void validateFieldsSortable(Long modelId, List<cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldSort> sorts) {
        Set<String> checkedFields = new HashSet<>();

        for (cn.cheers.x.module.dynamicbusiness.service.entity.query.dto.FieldSort sort : sorts) {
            String fieldCode = sort.getFieldCode();

            // 避免重复检查
            if (checkedFields.contains(fieldCode)) {
                continue;
            }
            checkedFields.add(fieldCode);

            // 检查字段是否可排序（可排序的字段必须先是可查询的）
            if (!fieldIndexService.isFieldSearchable(modelId, fieldCode)) {
                throw exception(FIELD_NOT_SEARCHABLE, fieldCode);
            }
        }
    }

    /**
     * 限制分页大小
     */
    private void limitPageSize(FieldQueryRequest request) {
        if (request.getPageParam() != null) {
            int pageSize = request.getPageParam().getPageSize();
            if (pageSize > MAX_PAGE_SIZE) {
                request.getPageParam().setPageSize(MAX_PAGE_SIZE);
                log.warn("分页大小超限，已限制为 {}", MAX_PAGE_SIZE);
            }
        }
    }

    /**
     * 转换为 SearchableFieldVO
     *
     * 注意：isSortable 优先从模型字段分配中读取，如果为 null 则使用字段定义中的默认值
     */
    private SearchableFieldVO convertToSearchableFieldVO(FieldDO field, Long modelId) {
        // 获取模型字段分配信息（优先使用模型字段分配中的配置）
        Boolean isSortable;
        if (modelId != null) {
            ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, field.getId());
            if (assignment != null && assignment.getIsSortable() != null) {
                isSortable = assignment.getIsSortable();
            } else {
                // 使用智能默认值服务获取字段类型的默认可排序属性
                isSortable = smartSearchableService.getDefaultSortable(field.getType());
            }
        } else {
            // 没有 modelId 时使用智能默认值
            isSortable = smartSearchableService.getDefaultSortable(field.getType());
        }

        return SearchableFieldVO.builder()
            .fieldId(field.getId())
            .fieldCode(field.getCode())
            .fieldName(field.getName())
            .fieldType(field.getType())
            .sortable(isSortable)
            .description(field.getDescription())
            .unit(field.getUnit())
            .supportedOperators(SearchableFieldVO.getSupportedOperatorsForType(field.getType()))
            .build();
    }

}
