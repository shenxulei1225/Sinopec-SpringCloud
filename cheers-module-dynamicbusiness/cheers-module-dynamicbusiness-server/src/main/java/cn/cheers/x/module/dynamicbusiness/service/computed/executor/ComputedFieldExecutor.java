package cn.cheers.x.module.dynamicbusiness.service.computed.executor;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;

import java.util.Map;

/**
 * 计算字段执行器接口
 * 
 * 负责执行计算字段的实际计算逻辑，包括：
 * - 聚合统计计算（COUNT、SUM、AVG、MAX、MIN）
 * - 公式计算（表达式解析和执行）
 * 
 * @author yudao
 */
public interface ComputedFieldExecutor {

    /**
     * 执行计算
     * 
     * @param entityId 实体 ID
     * @param field 计算字段配置
     * @param computedValues 已计算的字段值（用于公式计算时引用）
     * @return 计算结果
     */
    Object execute(Long entityId, ComputedFieldDO field, Map<String, Object> computedValues);

    /**
     * 执行聚合统计计算
     * 
     * @param entityId 实体 ID
     * @param field 计算字段配置
     * @return 聚合结果
     */
    Object executeAggregate(Long entityId, ComputedFieldDO field);

    /**
     * 执行公式计算
     * 
     * @param entityId 实体 ID
     * @param field 计算字段配置
     * @param computedValues 已计算的字段值
     * @return 计算结果
     */
    Object executeFormula(Long entityId, ComputedFieldDO field, Map<String, Object> computedValues);

    /**
     * 格式化计算结果
     * 
     * @param value 原始值
     * @param resultType 结果类型
     * @param decimalPlaces 小数位数
     * @param nullDisplay 空值显示
     * @return 格式化后的值
     */
    Object formatResult(Object value, String resultType, Integer decimalPlaces, String nullDisplay);
}
