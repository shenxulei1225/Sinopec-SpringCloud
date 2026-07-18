package cn.cheers.x.module.dynamicbusiness.convert.computed;

import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 计算字段 Convert
 * 
 * @author yudao
 */
@Mapper
public interface ComputedFieldConvert {

    ComputedFieldConvert INSTANCE = Mappers.getMapper(ComputedFieldConvert.class);

    /**
     * 创建请求 VO 转 DO
     */
    default ComputedFieldDO convert(ComputedFieldCreateReqVO reqVO) {
        if (reqVO == null) {
            return null;
        }
        ComputedFieldDO field = new ComputedFieldDO();
        field.setModelId(reqVO.getModelId());
        field.setFieldName(reqVO.getFieldName());
        field.setFieldCode(reqVO.getFieldCode());
        field.setComputeType(reqVO.getComputeType());
        field.setAggregateFunction(reqVO.getAggregateFunction());
        field.setTargetEntityType(reqVO.getTargetEntityType());
        field.setTargetModelCode(reqVO.getTargetModelCode());
        field.setTargetFieldCode(reqVO.getTargetFieldCode());
        field.setRelationCondition(reqVO.getRelationCondition());
        field.setFilterCondition(reqVO.getFilterCondition());
        field.setFormulaExpression(reqVO.getFormulaExpression());
        field.setFormulaFields(reqVO.getFormulaFields());
        field.setResultType(reqVO.getResultType());
        field.setDecimalPlaces(reqVO.getDecimalPlaces());
        field.setNullDisplay(reqVO.getNullDisplay());
        field.setComputeStrategy(reqVO.getComputeStrategy());
        field.setCacheTtlMinutes(reqVO.getCacheTtlMinutes());
        field.setDescription(reqVO.getDescription());
        return field;
    }

    /**
     * 更新请求 VO 转 DO
     */
    default ComputedFieldDO convert(ComputedFieldUpdateReqVO reqVO) {
        if (reqVO == null) {
            return null;
        }
        ComputedFieldDO field = new ComputedFieldDO();
        field.setId(reqVO.getId());
        field.setFieldName(reqVO.getFieldName());
        field.setFieldCode(reqVO.getFieldCode());
        field.setComputeType(reqVO.getComputeType());
        field.setAggregateFunction(reqVO.getAggregateFunction());
        field.setTargetEntityType(reqVO.getTargetEntityType());
        field.setTargetModelCode(reqVO.getTargetModelCode());
        field.setTargetFieldCode(reqVO.getTargetFieldCode());
        field.setRelationCondition(reqVO.getRelationCondition());
        field.setFilterCondition(reqVO.getFilterCondition());
        field.setFormulaExpression(reqVO.getFormulaExpression());
        field.setFormulaFields(reqVO.getFormulaFields());
        field.setResultType(reqVO.getResultType());
        field.setDecimalPlaces(reqVO.getDecimalPlaces());
        field.setNullDisplay(reqVO.getNullDisplay());
        field.setComputeStrategy(reqVO.getComputeStrategy());
        field.setCacheTtlMinutes(reqVO.getCacheTtlMinutes());
        field.setDescription(reqVO.getDescription());
        field.setSortOrder(reqVO.getSortOrder());
        return field;
    }

    /**
     * DO 转响应 VO
     * 
     * 注意：名称相关字段（computeTypeName, aggregateFunctionName 等）需要在 Service 层设置
     */
    default ComputedFieldRespVO convert(ComputedFieldDO field) {
        if (field == null) {
            return null;
        }
        ComputedFieldRespVO vo = new ComputedFieldRespVO();
        vo.setId(field.getId());
        vo.setModelId(field.getModelId());
        vo.setFieldName(field.getFieldName());
        vo.setFieldCode(field.getFieldCode());
        vo.setComputeType(field.getComputeType());
        
        // 聚合统计配置
        vo.setAggregateFunction(field.getAggregateFunction());
        vo.setTargetEntityType(field.getTargetEntityType());
        vo.setTargetModelCode(field.getTargetModelCode());
        vo.setTargetFieldCode(field.getTargetFieldCode());
        vo.setRelationCondition(field.getRelationCondition());
        vo.setFilterCondition(field.getFilterCondition());
        
        // 公式计算配置
        vo.setFormulaExpression(field.getFormulaExpression());
        vo.setFormulaFields(field.getFormulaFields());
        
        // 结果配置
        vo.setResultType(field.getResultType());
        vo.setDecimalPlaces(field.getDecimalPlaces());
        vo.setNullDisplay(field.getNullDisplay());
        
        // 性能配置
        vo.setComputeStrategy(field.getComputeStrategy());
        vo.setCacheTtlMinutes(field.getCacheTtlMinutes());
        
        // 其他
        vo.setDescription(field.getDescription());
        vo.setSortOrder(field.getSortOrder());
        vo.setCreateTime(field.getCreateTime());
        vo.setUpdateTime(field.getUpdateTime());
        
        // 设置名称（需要在 Service 层补充）
        vo.setComputeTypeName(getComputeTypeName(field.getComputeType()));
        vo.setAggregateFunctionName(getAggregateFunctionName(field.getAggregateFunction()));
        vo.setResultTypeName(getResultTypeName(field.getResultType()));
        vo.setComputeStrategyName(getComputeStrategyName(field.getComputeStrategy()));
        
        return vo;
    }

    /**
     * DO 列表转响应 VO 列表
     */
    default List<ComputedFieldRespVO> convertList(List<ComputedFieldDO> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(this::convert).toList();
    }

    /**
     * 获取计算类型名称
     */
    default String getComputeTypeName(String computeType) {
        if (computeType == null) {
            return null;
        }
        return switch (computeType) {
            case ComputedFieldDO.COMPUTE_TYPE_AGGREGATE -> "聚合统计";
            case ComputedFieldDO.COMPUTE_TYPE_FORMULA -> "公式计算";
            default -> computeType;
        };
    }

    /**
     * 获取聚合函数名称
     */
    default String getAggregateFunctionName(String aggregateFunction) {
        if (aggregateFunction == null) {
            return null;
        }
        return switch (aggregateFunction) {
            case ComputedFieldDO.AGGREGATE_COUNT -> "计数";
            case ComputedFieldDO.AGGREGATE_SUM -> "求和";
            case ComputedFieldDO.AGGREGATE_AVG -> "平均值";
            case ComputedFieldDO.AGGREGATE_MAX -> "最大值";
            case ComputedFieldDO.AGGREGATE_MIN -> "最小值";
            default -> aggregateFunction;
        };
    }

    /**
     * 获取结果类型名称
     */
    default String getResultTypeName(String resultType) {
        if (resultType == null) {
            return null;
        }
        return switch (resultType) {
            case ComputedFieldDO.RESULT_TYPE_NUMBER -> "整数";
            case ComputedFieldDO.RESULT_TYPE_DECIMAL -> "小数";
            case ComputedFieldDO.RESULT_TYPE_PERCENTAGE -> "百分比";
            default -> resultType;
        };
    }

    /**
     * 获取计算策略名称
     */
    default String getComputeStrategyName(String computeStrategy) {
        if (computeStrategy == null) {
            return null;
        }
        return switch (computeStrategy) {
            case ComputedFieldDO.STRATEGY_REALTIME -> "实时计算";
            case ComputedFieldDO.STRATEGY_CACHED -> "缓存计算";
            case ComputedFieldDO.STRATEGY_PRECOMPUTED -> "预计算存储";
            default -> computeStrategy;
        };
    }
}
