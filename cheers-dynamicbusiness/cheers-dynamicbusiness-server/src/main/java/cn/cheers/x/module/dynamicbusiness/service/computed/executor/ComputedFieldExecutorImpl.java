package cn.cheers.x.module.dynamicbusiness.service.computed.executor;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计算字段执行器实现
 * 
 * @author yudao
 */
@Component
@Slf4j
public class ComputedFieldExecutorImpl implements ComputedFieldExecutor {

    /** 公式中字段引用的正则表达式：[field_code] */
    private static final Pattern FIELD_REFERENCE_PATTERN = Pattern.compile("\\[([a-z][a-z0-9_]*)\\]");

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public Object execute(Long entityId, ComputedFieldDO field, Map<String, Object> computedValues) {
        try {
            Object result;
            if (field.isAggregate()) {
                result = executeAggregate(entityId, field);
            } else if (field.isFormula()) {
                result = executeFormula(entityId, field, computedValues);
            } else {
                log.warn("[execute][未知的计算类型：{}]", field.getComputeType());
                return null;
            }
            
            return formatResult(result, field.getResultType(), 
                    field.getDecimalPlaces(), field.getNullDisplay());
        } catch (Exception e) {
            log.error("[execute][计算失败，entityId={}, fieldCode={}]", 
                    entityId, field.getFieldCode(), e);
            return parseNullDisplay(field.getNullDisplay(), field.getResultType());
        }
    }

    @Override
    public Object executeAggregate(Long entityId, ComputedFieldDO field) {
        // 1. 构建 SQL 查询
        String sql = buildAggregateSql(entityId, field);
        if (sql == null) {
            return null;
        }
        
        log.debug("[executeAggregate][执行聚合查询，entityId={}, sql={}]", entityId, sql);
        
        // 2. 执行查询
        try {
            Object result = jdbcTemplate.queryForObject(sql, Object.class);
            return result;
        } catch (Exception e) {
            log.error("[executeAggregate][聚合查询失败，entityId={}, sql={}]", entityId, sql, e);
            return null;
        }
    }

    @Override
    public Object executeFormula(Long entityId, ComputedFieldDO field, Map<String, Object> computedValues) {
        String expression = field.getFormulaExpression();
        if (!StringUtils.hasText(expression)) {
            return null;
        }
        
        // 1. 获取实体的字段值
        Map<String, Object> fieldValues = getEntityFieldValues(entityId, field.getModelId());
        
        // 2. 合并已计算的值
        Map<String, Object> allValues = new HashMap<>(fieldValues);
        if (!CollectionUtils.isEmpty(computedValues)) {
            allValues.putAll(computedValues);
        }
        
        // 3. 替换字段引用为实际值
        String evaluableExpression = replaceFieldReferences(expression, allValues);
        
        log.debug("[executeFormula][执行公式计算，entityId={}, expression={}, evaluable={}]", 
                entityId, expression, evaluableExpression);
        
        // 4. 计算表达式
        return evaluateExpression(evaluableExpression);
    }

    @Override
    public Object formatResult(Object value, String resultType, Integer decimalPlaces, String nullDisplay) {
        if (value == null) {
            return parseNullDisplay(nullDisplay, resultType);
        }
        
        try {
            BigDecimal numValue;
            if (value instanceof BigDecimal) {
                numValue = (BigDecimal) value;
            } else if (value instanceof Number) {
                numValue = new BigDecimal(value.toString());
            } else {
                numValue = new BigDecimal(value.toString());
            }
            
            // 设置小数位数
            int scale = decimalPlaces != null ? decimalPlaces : 0;
            numValue = numValue.setScale(scale, RoundingMode.HALF_UP);
            
            // 根据结果类型返回
            if (ComputedFieldDO.RESULT_TYPE_NUMBER.equals(resultType)) {
                return numValue.longValue();
            } else if (ComputedFieldDO.RESULT_TYPE_PERCENTAGE.equals(resultType)) {
                return numValue.doubleValue();
            } else {
                return numValue.doubleValue();
            }
        } catch (Exception e) {
            log.warn("[formatResult][格式化失败，value={}, resultType={}]", value, resultType, e);
            return parseNullDisplay(nullDisplay, resultType);
        }
    }


    // ========== 私有方法 ==========

    /**
     * 构建聚合 SQL
     */
    private String buildAggregateSql(Long entityId, ComputedFieldDO field) {
        String function = field.getAggregateFunction();
        String targetEntityType = field.getTargetEntityType();
        String targetModelCode = field.getTargetModelCode();
        String targetFieldCode = field.getTargetFieldCode();
        
        // 1. 确定目标表名
        String tableName = determineTableName(targetEntityType, targetModelCode);
        if (tableName == null) {
            log.warn("[buildAggregateSql][无法确定目标表名，targetEntityType={}, targetModelCode={}]", 
                    targetEntityType, targetModelCode);
            return null;
        }
        
        // 2. 构建聚合函数部分
        String aggregatePart;
        if (ComputedFieldDO.AGGREGATE_COUNT.equals(function)) {
            aggregatePart = "COUNT(*)";
        } else if (StringUtils.hasText(targetFieldCode)) {
            aggregatePart = function + "(" + targetFieldCode + ")";
        } else {
            log.warn("[buildAggregateSql][非 COUNT 聚合必须指定目标字段]");
            return null;
        }
        
        // 3. 构建 WHERE 条件
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(" WHERE deleted = false");
        
        // 添加关联条件
        Map<String, Object> relationCondition = field.getRelationCondition();
        if (!CollectionUtils.isEmpty(relationCondition)) {
            String relationField = (String) relationCondition.get("relation_field");
            String currentField = (String) relationCondition.get("current_field");
            if (StringUtils.hasText(relationField) && StringUtils.hasText(currentField)) {
                // 这里需要根据 currentField 获取当前实体的值
                // 简化处理：假设 currentField 是 id
                if ("id".equals(currentField)) {
                    whereClause.append(" AND ").append(relationField).append(" = ").append(entityId);
                }
            }
        }
        
        // 添加筛选条件
        List<Map<String, Object>> filterConditions = field.getFilterCondition();
        if (!CollectionUtils.isEmpty(filterConditions)) {
            for (Map<String, Object> condition : filterConditions) {
                String filterField = (String) condition.get("field");
                String operator = (String) condition.get("operator");
                Object value = condition.get("value");
                
                if (StringUtils.hasText(filterField) && StringUtils.hasText(operator) && value != null) {
                    whereClause.append(" AND ").append(filterField).append(" ").append(operator).append(" ");
                    if (value instanceof String) {
                        whereClause.append("'").append(value).append("'");
                    } else {
                        whereClause.append(value);
                    }
                }
            }
        }
        
        // 4. 组装完整 SQL
        return "SELECT " + aggregatePart + " FROM " + tableName + whereClause;
    }

    /**
     * 确定目标表名
     * 
     * TODO: 需要根据业务类型和 Model 编码查询实际表名
     */
    private String determineTableName(String entityType, String modelCode) {
        // 这里需要从 EntityTypeConfig 或 Model 配置中获取实际表名
        // 暂时使用简单的命名规则
        if (StringUtils.hasText(modelCode)) {
            return "dynamic_" + modelCode;
        } else if (StringUtils.hasText(entityType)) {
            return "dynamic_" + entityType;
        }
        return null;
    }

    /**
     * 获取实体的字段值
     * 
     * TODO: 需要从 Entity 服务获取
     */
    private Map<String, Object> getEntityFieldValues(Long entityId, Long modelId) {
        // 这里需要从 Entity 服务获取实体的所有字段值
        // 暂时返回空 Map
        log.warn("[getEntityFieldValues][需要实现从 Entity 获取字段值的逻辑]");
        return Collections.emptyMap();
    }

    /**
     * 替换公式中的字段引用为实际值
     */
    private String replaceFieldReferences(String expression, Map<String, Object> fieldValues) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = FIELD_REFERENCE_PATTERN.matcher(expression);
        
        while (matcher.find()) {
            String fieldCode = matcher.group(1);
            Object value = fieldValues.get(fieldCode);
            String replacement;
            
            if (value == null) {
                replacement = "0"; // 空值默认为 0
            } else if (value instanceof Number) {
                replacement = value.toString();
            } else {
                try {
                    replacement = new BigDecimal(value.toString()).toString();
                } catch (NumberFormatException e) {
                    replacement = "0";
                }
            }
            
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    /**
     * 计算数学表达式
     * 
     * 支持的运算符：+、-、*、/、%、()
     */
    private Object evaluateExpression(String expression) {
        try {
            // 移除空格
            expression = expression.replaceAll("\\s+", "");
            
            if (expression.isEmpty()) {
                return 0;
            }
            
            // 使用简单的表达式解析器
            return evaluateSimpleExpression(expression);
        } catch (Exception e) {
            log.error("[evaluateExpression][表达式计算失败：{}]", expression, e);
            return null;
        }
    }

    /**
     * 简单表达式计算（支持 +、-、*、/、%、括号）
     */
    private BigDecimal evaluateSimpleExpression(String expression) {
        // 处理括号
        while (expression.contains("(")) {
            int start = expression.lastIndexOf("(");
            int end = expression.indexOf(")", start);
            if (end == -1) {
                throw new IllegalArgumentException("括号不匹配");
            }
            String inner = expression.substring(start + 1, end);
            BigDecimal innerResult = evaluateSimpleExpression(inner);
            expression = expression.substring(0, start) + innerResult.toString() + expression.substring(end + 1);
        }
        
        // 处理加减法（优先级最低）
        int addIndex = findOperatorIndex(expression, '+', '-');
        if (addIndex > 0) {
            String left = expression.substring(0, addIndex);
            String right = expression.substring(addIndex + 1);
            char operator = expression.charAt(addIndex);
            
            BigDecimal leftValue = evaluateSimpleExpression(left);
            BigDecimal rightValue = evaluateSimpleExpression(right);
            
            if (operator == '+') {
                return leftValue.add(rightValue);
            } else {
                return leftValue.subtract(rightValue);
            }
        }
        
        // 处理乘除法和取模
        int mulIndex = findOperatorIndex(expression, '*', '/', '%');
        if (mulIndex > 0) {
            String left = expression.substring(0, mulIndex);
            String right = expression.substring(mulIndex + 1);
            char operator = expression.charAt(mulIndex);
            
            BigDecimal leftValue = evaluateSimpleExpression(left);
            BigDecimal rightValue = evaluateSimpleExpression(right);
            
            if (operator == '*') {
                return leftValue.multiply(rightValue);
            } else if (operator == '/') {
                if (rightValue.compareTo(BigDecimal.ZERO) == 0) {
                    log.warn("[evaluateSimpleExpression][除数为零，返回 0]");
                    return BigDecimal.ZERO;
                }
                return leftValue.divide(rightValue, 10, RoundingMode.HALF_UP);
            } else {
                return leftValue.remainder(rightValue);
            }
        }
        
        // 解析数字
        return new BigDecimal(expression);
    }

    /**
     * 查找运算符位置（从右向左，跳过括号内的运算符）
     */
    private int findOperatorIndex(String expression, char... operators) {
        int parenthesesCount = 0;
        Set<Character> operatorSet = new HashSet<>();
        for (char op : operators) {
            operatorSet.add(op);
        }
        
        // 从右向左查找，确保正确的运算顺序
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == ')') parenthesesCount++;
            if (c == '(') parenthesesCount--;
            
            if (parenthesesCount == 0 && operatorSet.contains(c)) {
                // 跳过负号（如果是表达式开头或前一个字符是运算符）
                if (c == '-' && (i == 0 || isOperator(expression.charAt(i - 1)))) {
                    continue;
                }
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断是否是运算符
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '(';
    }

    /**
     * 解析空值显示
     */
    private Object parseNullDisplay(String nullDisplay, String resultType) {
        if (!StringUtils.hasText(nullDisplay)) {
            return 0;
        }
        try {
            if (ComputedFieldDO.RESULT_TYPE_NUMBER.equals(resultType)) {
                return Long.parseLong(nullDisplay);
            } else {
                return Double.parseDouble(nullDisplay);
            }
        } catch (NumberFormatException e) {
            return nullDisplay;
        }
    }
}
