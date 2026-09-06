package cn.cheers.x.inspection.task.dal.mysql.task;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskPageReqVO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务 Mapper。
 */
@Mapper
public interface InspectionTaskMapper extends BaseMapperX<InspectionTaskDO> {

    // ==================== 基础查询 ====================

    default InspectionTaskDO selectByTaskCode(@Param("taskCode") String taskCode) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getTaskCode, taskCode));
    }

    default List<InspectionTaskDO> selectByCategoryId(@Param("categoryId") Long categoryId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getCategoryId, categoryId)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    default List<InspectionTaskDO> selectByEnabled(Boolean enabled) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getEnabled, enabled)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    // ==================== 编排相关查询 ====================

    /**
     * 根据排期策略ID查询任务列表。
     */
    default List<InspectionTaskDO> selectBySchedulePolicyId(@Param("schedulePolicyId") Long schedulePolicyId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getSchedulePolicyId, schedulePolicyId)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    /**
     * 获取所有启用的任务。
     */
    default List<InspectionTaskDO> selectActiveTasks() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getEnabled, Boolean.TRUE)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    /**
     * 根据激活的编排批次ID查询任务列表。
     */
    default List<InspectionTaskDO> selectByActivePlanId(@Param("activePlanId") Long activePlanId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getActivePlanId, activePlanId)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    /**
     * 获取未设置排期策略的任务。
     */
    default List<InspectionTaskDO> selectWithoutSchedulePolicy() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .isNull(InspectionTaskDO::getSchedulePolicyId)
                .eq(InspectionTaskDO::getEnabled, Boolean.TRUE)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    // ==================== 树形结构查询 ====================

    /**
     * 根据父任务ID查询子任务列表。
     */
    default List<InspectionTaskDO> selectByParentId(@Param("parentId") Long parentId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getParentId, parentId)
                .orderByAsc(InspectionTaskDO::getCreateTime));
    }

    /**
     * 获取根任务列表（父任务ID为空的顶级任务）。
     */
    default List<InspectionTaskDO> selectRootTasks() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .isNull(InspectionTaskDO::getParentId)
                .orderByAsc(InspectionTaskDO::getCreateTime));
    }

    // ==================== 继承配置查询 ====================

    /**
     * 获取需要继承排期的任务。
     * <p>条件：inheritParentSchedule = true 且 scheduleRequirementId/schedulePolicyId 为空</p>
     */
    default List<InspectionTaskDO> selectNeedInheritSchedule() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getInheritParentSchedule, Boolean.TRUE)
                .and(w -> w
                        .isNull(InspectionTaskDO::getScheduleRequirementId)
                        .or()
                        .isNull(InspectionTaskDO::getSchedulePolicyId))
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    /**
     * 获取需要继承资源策略的任务。
     * <p>条件：inheritParentResourcePolicy = true 且 resourcePolicy 为空</p>
     */
    default List<InspectionTaskDO> selectNeedInheritResourcePolicy() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getInheritParentResourcePolicy, Boolean.TRUE)
                .isNull(InspectionTaskDO::getResourcePolicy)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    // ==================== 冲突检测 ====================

    /**
     * 获取潜在冲突的任务。
     * <p>同一分类下使用了相同排期策略的任务</p>
     */
    default List<InspectionTaskDO> selectPotentialConflict(@Param("schedulePolicyId") Long schedulePolicyId,
                                                          @Param("categoryId") Long categoryId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getSchedulePolicyId, schedulePolicyId)
                .eq(InspectionTaskDO::getCategoryId, categoryId)
                .eq(InspectionTaskDO::getEnabled, Boolean.TRUE)
                .orderByDesc(InspectionTaskDO::getCreateTime));
    }

    // ==================== 统计 ====================

    /**
     * 按状态统计任务数量。
     */
    default long countByStatus(@Param("status") Integer status) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getStatus, status));
    }

    /**
     * 按排期策略ID统计任务数量。
     */
    default long countBySchedulePolicyId(@Param("schedulePolicyId") Long schedulePolicyId) {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getSchedulePolicyId, schedulePolicyId));
    }

    /**
     * 统计启用的任务数量。
     */
    default long countEnabled() {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskDO>()
                .eq(InspectionTaskDO::getEnabled, Boolean.TRUE));
    }

    // ==================== 分页查询 ====================

    /**
     * 分页查询任务列表。
     *
     * <p>status 筛选按「派生展示状态」翻译成 enabled + runtime_job_id 条件
     * （库里 status 列只在创建时写 0，已废弃不查）。派生规则见
     * {@code InspectionTaskQueryServiceImpl#deriveDisplayStatus}，两处必须一起改。</p>
     */
    default PageResult<InspectionTaskDO> selectPage(InspectionTaskPageReqVO pageReqVO) {
        LambdaQueryWrapperX<InspectionTaskDO> wrapper = new LambdaQueryWrapperX<InspectionTaskDO>()
                .likeIfPresent(InspectionTaskDO::getTaskName, pageReqVO.getTaskName())
                .likeIfPresent(InspectionTaskDO::getTaskCode, pageReqVO.getTaskCode())
                .eqIfPresent(InspectionTaskDO::getCategoryId, pageReqVO.getCategoryId())
                .eqIfPresent(InspectionTaskDO::getEnabled, pageReqVO.getEnabled())
                .orderByDesc(InspectionTaskDO::getCreateTime);
        Integer status = pageReqVO.getStatus();
        if (status != null) {
            if (status == 1) {
                // 已启用
                wrapper.eq(InspectionTaskDO::getEnabled, Boolean.TRUE);
            } else if (status == 2) {
                // 已停用：排过期（有作业 ID）但当前未启用
                wrapper.isNotNull(InspectionTaskDO::getRuntimeJobId)
                        .and(w -> w.isNull(InspectionTaskDO::getEnabled)
                                .or().eq(InspectionTaskDO::getEnabled, Boolean.FALSE));
            } else {
                // 草稿：从未进入排程
                wrapper.isNull(InspectionTaskDO::getRuntimeJobId)
                        .and(w -> w.isNull(InspectionTaskDO::getEnabled)
                                .or().eq(InspectionTaskDO::getEnabled, Boolean.FALSE));
            }
        }
        return selectPage(pageReqVO, wrapper);
    }

    /**
     * 统计「已停用」任务数：排过期（有作业 ID）但当前未启用。口径同 selectPage 的 status=2。
     */
    default long countDisabledScheduled() {
        return selectCount(new LambdaQueryWrapperX<InspectionTaskDO>()
                .isNotNull(InspectionTaskDO::getRuntimeJobId)
                .and(w -> w.isNull(InspectionTaskDO::getEnabled)
                        .or().eq(InspectionTaskDO::getEnabled, Boolean.FALSE)));
    }

    // ==================== 批量统计 ====================

    /**
     * 批量统计子任务数量。
     * <p>根据父任务ID列表，批量查询每个父任务的直接子任务数量。</p>
     *
     * @param parentIds 父任务ID列表
     * @return Map，key为父任务ID，value为子任务数量
     */
    default Map<Long, Long> countSubTasksByParentIds(List<Long> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Long> countMap = new HashMap<>();
        for (Long parentId : parentIds) {
            countMap.put(parentId, 0L);
        }
        for (Long parentId : parentIds) {
            long count = selectCount(new LambdaQueryWrapperX<InspectionTaskDO>()
                    .eq(InspectionTaskDO::getParentId, parentId));
            countMap.put(parentId, count);
        }
        return countMap;
    }
}
