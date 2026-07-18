package cn.cheers.x.inspection.task.dal.mysql.schedule;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementPageReqVO;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 排期需求 Mapper
 *
 * <p>排期需求是一个配置容器，包含多个模板组合。</p>
 *
 * <h3>编排引擎常用查询场景</h3>
 * <ul>
 *   <li>根据任务ID查询排期需求</li>
 *   <li>根据策略ID查询排期需求</li>
 *   <li>获取启用的模板组合</li>
 * </ul>
 */
@Mapper
public interface InspectionTaskScheduleRequirementMapper extends BaseMapperX<InspectionTaskScheduleRequirementDO> {

    // ==================== 管理后台 - 条件查询 ====================

    /**
     * 分页查询
     */
    default PageResult<InspectionTaskScheduleRequirementDO> selectPage(InspectionTaskScheduleRequirementPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .likeIfPresent(InspectionTaskScheduleRequirementDO::getRequirementName, reqVO.getRequirementName())
                .eqIfPresent(InspectionTaskScheduleRequirementDO::getSchedulePolicyId, reqVO.getSchedulePolicyId())
                .likeIfPresent(InspectionTaskScheduleRequirementDO::getDescription, reqVO.getDescription())
                .orderByDesc(InspectionTaskScheduleRequirementDO::getCreateTime));
    }

    // ==================== 任务维度查询 ====================

    /**
     * 根据任务ID查询排期需求
     */
    default InspectionTaskScheduleRequirementDO selectByTaskId(Long taskId) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getTaskId, taskId));
    }

    /**
     * 根据任务ID查询所有排期需求
     */
    default List<InspectionTaskScheduleRequirementDO> selectByTaskIdList(List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .in(InspectionTaskScheduleRequirementDO::getTaskId, taskIds));
    }

    // ==================== 策略维度查询 ====================

    /**
     * 根据策略 ID 查询所有关联的需求
     */
    default List<InspectionTaskScheduleRequirementDO> selectByPolicyId(Long policyId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getSchedulePolicyId, policyId)
                .orderByAsc(InspectionTaskScheduleRequirementDO::getId));
    }

    // ==================== 批量操作 ====================

    /**
     * 批量根据 ID 查询
     */
    default List<InspectionTaskScheduleRequirementDO> selectByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .in(InspectionTaskScheduleRequirementDO::getId, ids));
    }

    // ==================== 编码查询 ====================

    /**
     * 根据需求编码查询
     */
    default InspectionTaskScheduleRequirementDO selectByRequirementCode(String requirementCode) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getRequirementCode, requirementCode));
    }
}
