package cn.cheers.x.module.dynamicbusiness.service.computed;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.computed.ComputedFieldConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.computed.ComputedFieldMapper;
import cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants;
import cn.cheers.x.module.dynamicbusiness.service.computed.cache.ComputedFieldCacheService;
import cn.cheers.x.module.dynamicbusiness.service.computed.executor.ComputedFieldExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 计算字段服务实现
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ComputedFieldServiceImpl implements ComputedFieldService {

    /** 缓存 Key 前缀 */
    private static final String CACHE_KEY_PREFIX = "computed_field:";
    
    /** 公式中字段引用的正则表达式：[field_code] */
    private static final Pattern FIELD_REFERENCE_PATTERN = Pattern.compile("\\[([a-z][a-z0-9_]*)\\]");
    
    /** 公式中允许的运算符 */
    private static final Set<String> ALLOWED_OPERATORS = Set.of("+", "-", "*", "/", "%", "(", ")", ".", " ");

    @Resource
    private ComputedFieldMapper computedFieldMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ComputedFieldExecutor computedFieldExecutor;

    @Resource
    private ComputedFieldCacheService cacheService;

    // ========== CRUD 操作 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComputedField(ComputedFieldCreateReqVO reqVO) {
        // 1. 验证字段编码唯一性
        validateFieldCodeUnique(reqVO.getModelId(), reqVO.getFieldCode(), null);
        
        // 2. 验证计算类型相关配置
        validateComputeTypeConfig(reqVO);
        
        // 3. 如果是公式计算，验证公式语法和循环依赖
        if (ComputedFieldDO.COMPUTE_TYPE_FORMULA.equals(reqVO.getComputeType())) {
            validateFormulaConfig(reqVO.getModelId(), reqVO.getFieldCode(), 
                    reqVO.getFormulaExpression(), reqVO.getFormulaFields());
        }
        
        // 4. 设置默认值
        setDefaultValues(reqVO);
        
        // 5. 转换并保存
        ComputedFieldDO field = ComputedFieldConvert.INSTANCE.convert(reqVO);
        computedFieldMapper.insert(field);
        
        log.info("[createComputedField][创建计算字段成功，id={}, fieldCode={}]", 
                field.getId(), field.getFieldCode());
        return field.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateComputedField(ComputedFieldUpdateReqVO reqVO) {
        // 1. 验证存在
        ComputedFieldDO existField = validateComputedFieldExists(reqVO.getId());
        
        // 2. 验证字段编码唯一性（排除自身）
        validateFieldCodeUnique(existField.getModelId(), reqVO.getFieldCode(), reqVO.getId());
        
        // 3. 如果是公式计算，验证公式语法和循环依赖
        if (ComputedFieldDO.COMPUTE_TYPE_FORMULA.equals(reqVO.getComputeType())) {
            validateFormulaConfig(existField.getModelId(), reqVO.getFieldCode(), 
                    reqVO.getFormulaExpression(), reqVO.getFormulaFields());
        }
        
        // 4. 转换并更新
        ComputedFieldDO updateField = ComputedFieldConvert.INSTANCE.convert(reqVO);
        computedFieldMapper.updateById(updateField);
        
        // 5. 清除相关缓存
        clearFieldCache(existField.getModelId(), existField.getFieldCode());
        
        log.info("[updateComputedField][更新计算字段成功，id={}]", reqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComputedField(Long id) {
        // 1. 验证存在
        ComputedFieldDO field = validateComputedFieldExists(id);
        
        // 2. 检查是否被其他公式字段引用
        validateNotReferencedByOthers(field.getModelId(), field.getFieldCode());
        
        // 3. 删除
        computedFieldMapper.deleteById(id);
        
        // 4. 清除缓存
        clearFieldCache(field.getModelId(), field.getFieldCode());
        
        log.info("[deleteComputedField][删除计算字段成功，id={}, fieldCode={}]", 
                id, field.getFieldCode());
    }

    @Override
    public ComputedFieldRespVO getComputedField(Long id) {
        ComputedFieldDO field = computedFieldMapper.selectById(id);
        if (field == null) {
            return null;
        }
        ComputedFieldRespVO respVO = ComputedFieldConvert.INSTANCE.convert(field);
        // 生成公式描述
        respVO.setFormulaDescription(generateFormulaDescription(field));
        return respVO;
    }

    @Override
    public List<ComputedFieldRespVO> getComputedFieldsByModelId(Long modelId) {
        List<ComputedFieldDO> fields = computedFieldMapper.selectByModelId(modelId);
        if (CollectionUtils.isEmpty(fields)) {
            return Collections.emptyList();
        }
        return fields.stream()
                .map(field -> {
                    ComputedFieldRespVO respVO = ComputedFieldConvert.INSTANCE.convert(field);
                    respVO.setFormulaDescription(generateFormulaDescription(field));
                    return respVO;
                })
                .collect(Collectors.toList());
    }


    // ========== 字段值计算 ==========

    @Override
    public Object computeFieldValue(Long entityId, String fieldCode) {
        // 1. 获取实体对应的 Model ID（这里需要从 Entity 服务获取）
        Long modelId = getModelIdByEntityId(entityId);
        if (modelId == null) {
            log.warn("[computeFieldValue][实体不存在，entityId={}]", entityId);
            return null;
        }
        
        // 2. 获取计算字段配置
        ComputedFieldDO field = computedFieldMapper.selectByModelIdAndFieldCode(modelId, fieldCode);
        if (field == null) {
            log.warn("[computeFieldValue][计算字段不存在，modelId={}, fieldCode={}]", modelId, fieldCode);
            return null;
        }
        
        // 3. 根据计算策略执行计算
        return executeComputation(entityId, field);
    }

    @Override
    public Map<String, Object> computeFieldValues(Long entityId, List<String> fieldCodes) {
        if (CollectionUtils.isEmpty(fieldCodes)) {
            return Collections.emptyMap();
        }
        
        // 1. 获取实体对应的 Model ID
        Long modelId = getModelIdByEntityId(entityId);
        if (modelId == null) {
            log.warn("[computeFieldValues][实体不存在，entityId={}]", entityId);
            return Collections.emptyMap();
        }
        
        // 2. 获取所有计算字段配置
        List<ComputedFieldDO> allFields = computedFieldMapper.selectByModelId(modelId);
        Map<String, ComputedFieldDO> fieldMap = allFields.stream()
                .collect(Collectors.toMap(ComputedFieldDO::getFieldCode, f -> f));
        
        // 3. 按依赖顺序排序
        List<String> orderedFieldCodes = getDependencyOrder(modelId, fieldCodes);
        
        // 4. 按顺序计算
        Map<String, Object> results = new LinkedHashMap<>();
        Map<String, Object> computedValues = new HashMap<>(); // 用于公式计算时引用
        
        for (String code : orderedFieldCodes) {
            ComputedFieldDO field = fieldMap.get(code);
            if (field == null) {
                continue;
            }
            Object value = executeComputationWithContext(entityId, field, computedValues);
            results.put(code, value);
            computedValues.put(code, value);
        }
        
        return results;
    }

    @Override
    public Map<String, Object> computeAllFieldValues(Long entityId, Long modelId) {
        List<ComputedFieldDO> fields = computedFieldMapper.selectByModelId(modelId);
        if (CollectionUtils.isEmpty(fields)) {
            return Collections.emptyMap();
        }
        
        List<String> fieldCodes = fields.stream()
                .map(ComputedFieldDO::getFieldCode)
                .collect(Collectors.toList());
        
        return computeFieldValues(entityId, fieldCodes);
    }

    // ========== 缓存管理 ==========

    @Override
    public void refreshCache(Long modelId, String fieldCode) {
        int count = cacheService.refreshFieldCache(modelId, fieldCode);
        log.info("[refreshCache][刷新缓存成功，modelId={}, fieldCode={}, 清除 {} 个缓存]", 
                modelId, fieldCode, count);
    }

    @Override
    public void refreshAllCache(Long modelId) {
        int count = cacheService.refreshModelCache(modelId);
        log.info("[refreshAllCache][刷新所有缓存成功，modelId={}, 清除 {} 个缓存]", 
                modelId, count);
    }

    @Override
    public void triggerPrecompute(Long entityId) {
        // 1. 获取实体对应的 Model ID
        Long modelId = getModelIdByEntityId(entityId);
        if (modelId == null) {
            return;
        }
        
        // 2. 获取所有预计算字段
        List<ComputedFieldDO> precomputedFields = computedFieldMapper.selectByComputeStrategy(
                ComputedFieldDO.STRATEGY_PRECOMPUTED);
        
        // 3. 过滤出当前 Model 的字段
        List<ComputedFieldDO> modelFields = precomputedFields.stream()
                .filter(f -> f.getModelId().equals(modelId))
                .collect(Collectors.toList());
        
        if (CollectionUtils.isEmpty(modelFields)) {
            return;
        }
        
        // 4. 异步执行预计算（这里简化为同步，实际应使用异步任务）
        for (ComputedFieldDO field : modelFields) {
            try {
                Object value = computedFieldExecutor.execute(entityId, field, Collections.emptyMap());
                // 存储预计算结果到缓存（长期缓存，使用较长的 TTL）
                int ttl = field.getCacheTtlMinutes() != null ? field.getCacheTtlMinutes() * 10 : 60;
                cacheService.setCachedValue(modelId, field.getFieldCode(), entityId, value, ttl);
                log.debug("[triggerPrecompute][预计算完成，entityId={}, fieldCode={}, value={}]", 
                        entityId, field.getFieldCode(), value);
            } catch (Exception e) {
                log.error("[triggerPrecompute][预计算失败，entityId={}, fieldCode={}]", 
                        entityId, field.getFieldCode(), e);
            }
        }
    }

    @Override
    public void clearEntityCache(Long entityId) {
        int count = cacheService.refreshEntityCache(entityId);
        log.info("[clearEntityCache][清除实体缓存成功，entityId={}, 清除 {} 个缓存]", 
                entityId, count);
    }

    @Override
    public int batchRefreshEntityCache(Long modelId, List<Long> entityIds) {
        return cacheService.batchRefreshEntityCache(modelId, entityIds);
    }

    @Override
    public int warmupFieldCache(Long modelId, String fieldCode, List<Long> entityIds) {
        ComputedFieldDO field = computedFieldMapper.selectByModelIdAndFieldCode(modelId, fieldCode);
        if (field == null) {
            log.warn("[warmupFieldCache][计算字段不存在，modelId={}, fieldCode={}]", modelId, fieldCode);
            return 0;
        }
        return cacheService.warmupFieldCache(field, entityIds);
    }

    @Override
    public int warmupModelCache(Long modelId, List<Long> entityIds) {
        return cacheService.warmupModelCache(modelId, entityIds);
    }

    @Override
    public CacheStatisticsVO getCacheStatistics(Long modelId) {
        ComputedFieldCacheService.CacheStatistics stats = cacheService.getCacheStatistics(modelId);
        return new CacheStatisticsVO(
                stats.totalKeys(),
                stats.hitCount(),
                stats.missCount(),
                stats.hitRate(),
                stats.memoryUsageBytes()
        );
    }

    @Override
    public boolean isCached(Long modelId, String fieldCode, Long entityId) {
        return cacheService.isCached(modelId, fieldCode, entityId);
    }

    @Override
    public long getCacheTtl(Long modelId, String fieldCode, Long entityId) {
        return cacheService.getCacheTtl(modelId, fieldCode, entityId);
    }

    @Override
    public int clearAllCache() {
        return cacheService.clearAllCache();
    }


    // ========== 验证方法 ==========

    @Override
    public String validateFormulaExpression(String formulaExpression) {
        if (!StringUtils.hasText(formulaExpression)) {
            return "公式表达式不能为空";
        }
        
        // 1. 提取所有字段引用
        List<String> fieldReferences = extractFieldReferences(formulaExpression);
        
        // 2. 移除字段引用后，检查剩余部分是否只包含数字和运算符
        String remaining = formulaExpression;
        for (String ref : fieldReferences) {
            remaining = remaining.replace("[" + ref + "]", "0");
        }
        
        // 3. 检查是否包含非法字符
        for (char c : remaining.toCharArray()) {
            if (!Character.isDigit(c) && !ALLOWED_OPERATORS.contains(String.valueOf(c))) {
                return "公式包含非法字符：" + c;
            }
        }
        
        // 4. 检查括号是否匹配
        int parenthesesCount = 0;
        for (char c : formulaExpression.toCharArray()) {
            if (c == '(') parenthesesCount++;
            if (c == ')') parenthesesCount--;
            if (parenthesesCount < 0) {
                return "括号不匹配：多余的右括号";
            }
        }
        if (parenthesesCount != 0) {
            return "括号不匹配：缺少右括号";
        }
        
        // 5. 尝试解析表达式（简单验证）
        try {
            // 将字段引用替换为数字后尝试计算
            String testExpression = remaining.replaceAll("\\s+", "");
            if (testExpression.isEmpty()) {
                return "公式表达式无效";
            }
            // 检查是否有连续的运算符
            if (testExpression.matches(".*[+\\-*/%]{2,}.*")) {
                return "公式包含连续的运算符";
            }
        } catch (Exception e) {
            return "公式语法错误：" + e.getMessage();
        }
        
        return null; // 验证通过
    }

    @Override
    public String detectCircularDependency(Long modelId, String fieldCode, List<String> formulaFields) {
        if (CollectionUtils.isEmpty(formulaFields)) {
            return null;
        }
        
        // 1. 检查是否直接引用自己
        if (formulaFields.contains(fieldCode)) {
            return fieldCode + " -> " + fieldCode;
        }
        
        // 2. 获取所有计算字段
        List<ComputedFieldDO> allFields = computedFieldMapper.selectByModelId(modelId);
        Map<String, List<String>> dependencyMap = new HashMap<>();
        
        // 构建依赖图
        for (ComputedFieldDO field : allFields) {
            if (field.isFormula() && !CollectionUtils.isEmpty(field.getFormulaFields())) {
                dependencyMap.put(field.getFieldCode(), field.getFormulaFields());
            }
        }
        
        // 添加当前字段的依赖
        dependencyMap.put(fieldCode, formulaFields);
        
        // 3. DFS 检测循环
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        List<String> path = new ArrayList<>();
        
        if (hasCycle(fieldCode, dependencyMap, visited, recursionStack, path)) {
            return String.join(" -> ", path);
        }
        
        return null;
    }

    @Override
    public List<String> getDependencyOrder(Long modelId, List<String> fieldCodes) {
        if (CollectionUtils.isEmpty(fieldCodes)) {
            return Collections.emptyList();
        }
        
        // 1. 获取所有计算字段
        List<ComputedFieldDO> allFields = computedFieldMapper.selectByModelId(modelId);
        Map<String, List<String>> dependencyMap = new HashMap<>();
        
        // 构建依赖图
        for (ComputedFieldDO field : allFields) {
            if (field.isFormula() && !CollectionUtils.isEmpty(field.getFormulaFields())) {
                dependencyMap.put(field.getFieldCode(), field.getFormulaFields());
            } else {
                dependencyMap.put(field.getFieldCode(), Collections.emptyList());
            }
        }
        
        // 2. 拓扑排序
        Set<String> targetSet = new HashSet<>(fieldCodes);
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        
        for (String code : fieldCodes) {
            topologicalSort(code, dependencyMap, visited, result, targetSet);
        }
        
        // 3. 只返回请求的字段（按依赖顺序）
        return result.stream()
                .filter(targetSet::contains)
                .collect(Collectors.toList());
    }

    // ========== 私有方法 ==========

    /**
     * 验证计算字段是否存在
     */
    private ComputedFieldDO validateComputedFieldExists(Long id) {
        ComputedFieldDO field = computedFieldMapper.selectById(id);
        if (field == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_NOT_EXISTS);
        }
        return field;
    }

    /**
     * 验证字段编码唯一性
     */
    private void validateFieldCodeUnique(Long modelId, String fieldCode, Long excludeId) {
        if (computedFieldMapper.existsByFieldCode(modelId, fieldCode, excludeId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_CODE_DUPLICATE);
        }
    }

    /**
     * 验证计算类型相关配置
     */
    private void validateComputeTypeConfig(ComputedFieldCreateReqVO reqVO) {
        if (ComputedFieldDO.COMPUTE_TYPE_AGGREGATE.equals(reqVO.getComputeType())) {
            // 聚合统计必须指定聚合函数和目标
            if (!StringUtils.hasText(reqVO.getAggregateFunction())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_FORMULA_SYNTAX_ERROR, 
                        "聚合统计必须指定聚合函数");
            }
            if (!StringUtils.hasText(reqVO.getTargetEntityType())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_FORMULA_SYNTAX_ERROR, 
                        "聚合统计必须指定目标业务类型");
            }
            // SUM/AVG/MAX/MIN 必须指定目标字段
            if (!ComputedFieldDO.AGGREGATE_COUNT.equals(reqVO.getAggregateFunction()) 
                    && !StringUtils.hasText(reqVO.getTargetFieldCode())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_FORMULA_SYNTAX_ERROR, 
                        "SUM/AVG/MAX/MIN 必须指定目标字段");
            }
        } else if (ComputedFieldDO.COMPUTE_TYPE_FORMULA.equals(reqVO.getComputeType())) {
            // 公式计算必须指定公式表达式
            if (!StringUtils.hasText(reqVO.getFormulaExpression())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_FORMULA_SYNTAX_ERROR, 
                        "公式计算必须指定公式表达式");
            }
        }
    }

    /**
     * 验证公式配置
     */
    private void validateFormulaConfig(Long modelId, String fieldCode, 
            String formulaExpression, List<String> formulaFields) {
        // 1. 验证公式语法
        String syntaxError = validateFormulaExpression(formulaExpression);
        if (syntaxError != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_FORMULA_SYNTAX_ERROR, 
                    syntaxError);
        }
        
        // 2. 验证引用的字段是否存在
        if (!CollectionUtils.isEmpty(formulaFields)) {
            List<ComputedFieldDO> existingFields = computedFieldMapper.selectByModelId(modelId);
            Set<String> existingCodes = existingFields.stream()
                    .map(ComputedFieldDO::getFieldCode)
                    .collect(Collectors.toSet());
            
            for (String refField : formulaFields) {
                if (!existingCodes.contains(refField) && !refField.equals(fieldCode)) {
                    // 引用的字段不存在（且不是自己）
                    // 注意：这里允许引用普通字段，所以只检查计算字段
                    // 实际使用时可能需要检查所有字段
                    log.warn("[validateFormulaConfig][引用的字段可能不存在：{}]", refField);
                }
            }
        }
        
        // 3. 检测循环依赖
        String cyclePath = detectCircularDependency(modelId, fieldCode, formulaFields);
        if (cyclePath != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_CIRCULAR_DEPENDENCY, 
                    cyclePath);
        }
    }

    /**
     * 验证字段未被其他公式引用
     */
    private void validateNotReferencedByOthers(Long modelId, String fieldCode) {
        List<ComputedFieldDO> allFields = computedFieldMapper.selectByModelId(modelId);
        for (ComputedFieldDO field : allFields) {
            if (field.isFormula() && !CollectionUtils.isEmpty(field.getFormulaFields())) {
                if (field.getFormulaFields().contains(fieldCode)) {
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.COMPUTED_FIELD_REFERENCE_NOT_EXISTS, 
                            "字段被 " + field.getFieldName() + " 引用，无法删除");
                }
            }
        }
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues(ComputedFieldCreateReqVO reqVO) {
        if (reqVO.getComputeStrategy() == null) {
            reqVO.setComputeStrategy(ComputedFieldDO.STRATEGY_REALTIME);
        }
        if (reqVO.getCacheTtlMinutes() == null) {
            reqVO.setCacheTtlMinutes(5);
        }
        if (reqVO.getDecimalPlaces() == null) {
            reqVO.setDecimalPlaces(0);
        }
        if (reqVO.getNullDisplay() == null) {
            reqVO.setNullDisplay("0");
        }
        if (reqVO.getSortOrder() == null) {
            reqVO.setSortOrder(0);
        }
    }


    /**
     * 执行计算（根据策略）
     */
    private Object executeComputation(Long entityId, ComputedFieldDO field) {
        return executeComputationWithContext(entityId, field, Collections.emptyMap());
    }

    /**
     * 执行计算（带上下文，用于公式计算时引用其他已计算的值）
     */
    private Object executeComputationWithContext(Long entityId, ComputedFieldDO field, 
            Map<String, Object> computedValues) {
        // 根据计算策略选择执行方式
        if (field.isCached()) {
            return executeWithCache(entityId, field, computedValues);
        } else if (field.isPrecomputed()) {
            return executePrecomputed(entityId, field);
        } else {
            // 默认实时计算
            return computedFieldExecutor.execute(entityId, field, computedValues);
        }
    }

    /**
     * 带缓存的计算
     */
    private Object executeWithCache(Long entityId, ComputedFieldDO field, 
            Map<String, Object> computedValues) {
        // 1. 尝试从缓存获取
        Object cachedValue = cacheService.getCachedValue(field.getModelId(), field.getFieldCode(), entityId);
        if (cachedValue != null) {
            log.debug("[executeWithCache][缓存命中，entityId={}, fieldCode={}]", entityId, field.getFieldCode());
            return cachedValue;
        }
        
        // 2. 执行计算
        Object value = computedFieldExecutor.execute(entityId, field, computedValues);
        
        // 3. 存入缓存
        if (value != null) {
            int ttl = field.getCacheTtlMinutes() != null ? field.getCacheTtlMinutes() : 5;
            cacheService.setCachedValue(field.getModelId(), field.getFieldCode(), entityId, value, ttl);
            log.debug("[executeWithCache][计算完成并缓存，entityId={}, fieldCode={}, ttl={}分钟]", 
                    entityId, field.getFieldCode(), ttl);
        }
        
        return value;
    }

    /**
     * 获取预计算结果
     */
    private Object executePrecomputed(Long entityId, ComputedFieldDO field) {
        // 1. 尝试从缓存获取预计算结果
        Object cachedValue = cacheService.getCachedValue(field.getModelId(), field.getFieldCode(), entityId);
        if (cachedValue != null) {
            log.debug("[executePrecomputed][预计算缓存命中，entityId={}, fieldCode={}]", 
                    entityId, field.getFieldCode());
            return cachedValue;
        }
        
        // 2. 如果没有预计算结果，触发计算并存储
        Object value = computedFieldExecutor.execute(entityId, field, Collections.emptyMap());
        if (value != null) {
            // 预计算使用较长的 TTL（默认 60 分钟）
            int ttl = field.getCacheTtlMinutes() != null ? field.getCacheTtlMinutes() * 10 : 60;
            cacheService.setCachedValue(field.getModelId(), field.getFieldCode(), entityId, value, ttl);
            log.debug("[executePrecomputed][预计算完成并缓存，entityId={}, fieldCode={}, ttl={}分钟]", 
                    entityId, field.getFieldCode(), ttl);
        }
        return value;
    }

    /**
     * 清除字段缓存（更新时使用）
     */
    private void clearFieldCache(Long modelId, String fieldCode) {
        cacheService.refreshFieldCache(modelId, fieldCode);
    }

    /**
     * 提取公式中的字段引用
     */
    private List<String> extractFieldReferences(String formulaExpression) {
        List<String> references = new ArrayList<>();
        Matcher matcher = FIELD_REFERENCE_PATTERN.matcher(formulaExpression);
        while (matcher.find()) {
            references.add(matcher.group(1));
        }
        return references;
    }

    /**
     * 检测循环依赖（DFS）
     */
    private boolean hasCycle(String node, Map<String, List<String>> graph, 
            Set<String> visited, Set<String> recursionStack, List<String> path) {
        visited.add(node);
        recursionStack.add(node);
        path.add(node);
        
        List<String> neighbors = graph.getOrDefault(node, Collections.emptyList());
        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (hasCycle(neighbor, graph, visited, recursionStack, path)) {
                    return true;
                }
            } else if (recursionStack.contains(neighbor)) {
                path.add(neighbor);
                return true;
            }
        }
        
        recursionStack.remove(node);
        path.remove(path.size() - 1);
        return false;
    }

    /**
     * 拓扑排序
     */
    private void topologicalSort(String node, Map<String, List<String>> graph, 
            Set<String> visited, List<String> result, Set<String> targetSet) {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);
        
        // 先处理依赖
        List<String> dependencies = graph.getOrDefault(node, Collections.emptyList());
        for (String dep : dependencies) {
            if (targetSet.contains(dep) || graph.containsKey(dep)) {
                topologicalSort(dep, graph, visited, result, targetSet);
            }
        }
        
        result.add(node);
    }

    /**
     * 生成公式描述
     */
    private String generateFormulaDescription(ComputedFieldDO field) {
        if (field.isAggregate()) {
            StringBuilder sb = new StringBuilder();
            sb.append(getAggregateFunctionName(field.getAggregateFunction()));
            sb.append(" ");
            sb.append(field.getTargetEntityType());
            if (StringUtils.hasText(field.getTargetModelCode())) {
                sb.append("/").append(field.getTargetModelCode());
            }
            if (StringUtils.hasText(field.getTargetFieldCode())) {
                sb.append(" 的 ").append(field.getTargetFieldCode());
            }
            if (!CollectionUtils.isEmpty(field.getFilterCondition())) {
                sb.append(" (带筛选条件)");
            }
            return sb.toString();
        } else if (field.isFormula()) {
            return "公式: " + field.getFormulaExpression();
        }
        return null;
    }

    /**
     * 获取聚合函数名称
     */
    private String getAggregateFunctionName(String function) {
        if (function == null) return "";
        return switch (function) {
            case ComputedFieldDO.AGGREGATE_COUNT -> "统计";
            case ComputedFieldDO.AGGREGATE_SUM -> "求和";
            case ComputedFieldDO.AGGREGATE_AVG -> "平均";
            case ComputedFieldDO.AGGREGATE_MAX -> "最大";
            case ComputedFieldDO.AGGREGATE_MIN -> "最小";
            default -> function;
        };
    }

    /**
     * 根据实体 ID 获取 Model ID
     * 
     * TODO: 需要从 Entity 服务获取，这里暂时返回 null
     */
    private Long getModelIdByEntityId(Long entityId) {
        // 这里需要注入 EntityService 或通过其他方式获取
        // 暂时返回 null，实际使用时需要实现
        log.warn("[getModelIdByEntityId][需要实现从 Entity 获取 Model ID 的逻辑]");
        return null;
    }
}
