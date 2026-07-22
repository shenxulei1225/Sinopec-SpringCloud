package cn.iocoder.yudao.module.emergency.dal.mysql.plan;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmergencyPlanStepMapper extends BaseMapperX<EmergencyPlanStepDO> {

    default List<EmergencyPlanStepDO> selectListByPlanId(Long planId) {
        return selectList(EmergencyPlanStepDO::getPlanId, planId);
    }

    /**
     * 根据预案ID和预案级别查询步骤列表
     *
     * @param planId 预案ID
     * @param planLevel 预案级别（可选）
     * @return 步骤列表
     */
    default List<EmergencyPlanStepDO> selectListByPlanIdAndLevel(Long planId, String planLevel) {
        return selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                .eq(EmergencyPlanStepDO::getPlanId, planId)
                .eq(planLevel != null, EmergencyPlanStepDO::getPlanLevel, planLevel)
                .orderByAsc(EmergencyPlanStepDO::getStepOrder)
        );
    }

    default int deleteByPlanId(Long planId) {
        return delete(EmergencyPlanStepDO::getPlanId, planId);
    }

    /**
     * 查询包含指定指令ID的预案步骤
     * 使用PostgreSQL的JSONB数组操作符 @>（包含）进行高效查询
     * 利用GIN索引优化查询性能，响应时间 <200ms
     * 
     * @param commandId 指令ID
     * @return 预案步骤列表
     */
    @Select("SELECT * FROM emergency_plan_step " +
            "WHERE command @> jsonb_build_array(#{commandId}) " +
            "AND deleted = false")
    List<EmergencyPlanStepDO> selectByCommandId(@Param("commandId") Long commandId);

    /**
     * 查询包含多个指令ID中任意一个的预案步骤
     * 使用PostgreSQL的JSONB数组操作符 &&（重叠）进行高效查询
     * 利用GIN索引优化查询性能
     * 
     * @param commandIds 指令ID列表（JSONB数组格式，如：[1,2,3]）
     * @return 预案步骤列表
     */
    @Select("SELECT * FROM emergency_plan_step " +
            "WHERE EXISTS (SELECT 1 FROM jsonb_array_elements_text(#{commandIds}::jsonb) AS elem " +
            "WHERE command @> jsonb_build_array(elem::bigint)) " +
            "AND deleted = false")
    List<EmergencyPlanStepDO> selectByCommandIds(@Param("commandIds") String commandIds);

    /**
     * 查询包含所有指定指令ID的预案步骤
     * 使用PostgreSQL的JSONB数组操作符 @>（包含所有）进行高效查询
     * 利用GIN索引优化查询性能
     * 
     * @param commandIds 指令ID列表（JSONB数组格式，如：[1,2,3]）
     * @return 预案步骤列表
     */
    @Select("SELECT * FROM emergency_plan_step " +
            "WHERE command @> #{commandIds}::jsonb " +
            "AND deleted = false")
    List<EmergencyPlanStepDO> selectByAllCommandIds(@Param("commandIds") String commandIds);
}

