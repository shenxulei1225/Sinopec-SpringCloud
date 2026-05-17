package cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementPageReqVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 排期需求 Mapper
 *
 * <h3>编排引擎常用查询场景</h3>
 * <ul>
 *   <li>生成候选时间点：根据排期模式/重复模式找到对应需求</li>
 *   <li>判断某日是否执行：检查 weekDays/monthDays 是否包含该日期</li>
 *   <li>过滤有效期：startDate &lt;= 目标日期 &lt;= endDate</li>
 * </ul>
 */
@Mapper
public interface InspectionTaskScheduleRequirementMapper extends BaseMapperX<InspectionTaskScheduleRequirementDO> {

    // ==================== 管理后台 - 条件查询 ====================

    /**
     * 分页查询（支持模板管理）
     */
    default PageResult<InspectionTaskScheduleRequirementDO> selectPage(InspectionTaskScheduleRequirementPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .likeIfPresent(InspectionTaskScheduleRequirementDO::getTemplateName, reqVO.getTemplateName())
                .eqIfPresent(InspectionTaskScheduleRequirementDO::getPolicyId, reqVO.getPolicyId())
                .eqIfPresent(InspectionTaskScheduleRequirementDO::getIsTemplate, reqVO.getIsTemplate())
                .eqIfPresent(InspectionTaskScheduleRequirementDO::getScheduleMode, reqVO.getScheduleMode())
                .eqIfPresent(InspectionTaskScheduleRequirementDO::getRepeatMode, reqVO.getRepeatMode())
                .geIfPresent(InspectionTaskScheduleRequirementDO::getStartDate, reqVO.getStartDate())
                .leIfPresent(InspectionTaskScheduleRequirementDO::getEndDate, reqVO.getEndDate())
                .likeIfPresent(InspectionTaskScheduleRequirementDO::getDescription, reqVO.getDescription())
                .orderByDesc(InspectionTaskScheduleRequirementDO::getCreateTime));
    }

    // ==================== 编排引擎 - 核心查询 ====================

    /**
     * 查询所有启用的需求（非模板）
     * 用于编排引擎加载配置
     */
    default List<InspectionTaskScheduleRequirementDO> selectActiveRequirements() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE)
                .orderByAsc(InspectionTaskScheduleRequirementDO::getId));
    }

    /**
     * 根据策略 ID 查询所有关联的需求
     * 用于按策略分组编排
     */
    default List<InspectionTaskScheduleRequirementDO> selectByPolicyId(Long policyId) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getPolicyId, policyId)
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE)
                .orderByAsc(InspectionTaskScheduleRequirementDO::getId));
    }

    /**
     * 查询指定排期模式的所有需求
     *
     * @param scheduleMode 1-固定时间点 2-间隔执行 3-自动编排
     */
    default List<InspectionTaskScheduleRequirementDO> selectByScheduleMode(Integer scheduleMode) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getScheduleMode, scheduleMode)
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE));
    }

    /**
     * 查询指定重复模式的所有需求
     *
     * @param repeatMode 1-一次性 2-每日 3-每周 4-每月
     */
    default List<InspectionTaskScheduleRequirementDO> selectByRepeatMode(Integer repeatMode) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getRepeatMode, repeatMode)
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE));
    }

    // ==================== 编排引擎 - 日期范围查询 ====================

    /**
     * 查询在某日期有效的需求
     * 条件：startDate <= 目标日期 AND (endDate IS NULL OR endDate >= 目标日期)
     *
     * @param targetDate 目标日期
     */
    default List<InspectionTaskScheduleRequirementDO> selectEffectiveOnDate(LocalDate targetDate) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .le(InspectionTaskScheduleRequirementDO::getStartDate, targetDate)
                .and(w -> w.isNull(InspectionTaskScheduleRequirementDO::getEndDate)
                        .or()
                        .ge(InspectionTaskScheduleRequirementDO::getEndDate, targetDate))
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE));
    }

    /**
     * 查询指定日期范围内有效的需求
     * 用于预生成一段时间的排期计划
     *
     * @param startDate 开始日期（包含）
     * @param endDate   结束日期（包含）
     */
    default List<InspectionTaskScheduleRequirementDO> selectEffectiveBetween(LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .le(InspectionTaskScheduleRequirementDO::getStartDate, endDate)
                .and(w -> w.isNull(InspectionTaskScheduleRequirementDO::getEndDate)
                        .or()
                        .ge(InspectionTaskScheduleRequirementDO::getEndDate, startDate))
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE)
                .orderByAsc(InspectionTaskScheduleRequirementDO::getId));
    }

    // ==================== 编排引擎 - 组合条件查询 ====================

    /**
     * 查询指定排期模式 + 日期范围内有效的需求
     * 用于某模式的预编排
     */
    default List<InspectionTaskScheduleRequirementDO> selectByScheduleModeAndEffectiveBetween(
            Integer scheduleMode, LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getScheduleMode, scheduleMode)
                .le(InspectionTaskScheduleRequirementDO::getStartDate, endDate)
                .and(w -> w.isNull(InspectionTaskScheduleRequirementDO::getEndDate)
                        .or()
                        .ge(InspectionTaskScheduleRequirementDO::getEndDate, startDate))
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE)
                .orderByAsc(InspectionTaskScheduleRequirementDO::getId));
    }

    /**
     * 查询指定重复模式 + 日期范围内有效的需求
     * 用于每周/月模式的展开
     */
    default List<InspectionTaskScheduleRequirementDO> selectByRepeatModeAndEffectiveBetween(
            Integer repeatMode, LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getRepeatMode, repeatMode)
                .le(InspectionTaskScheduleRequirementDO::getStartDate, endDate)
                .and(w -> w.isNull(InspectionTaskScheduleRequirementDO::getEndDate)
                        .or()
                        .ge(InspectionTaskScheduleRequirementDO::getEndDate, startDate))
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.FALSE)
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

    // ==================== 模板管理 ====================

    /**
     * 查询所有模板
     */
    default List<InspectionTaskScheduleRequirementDO> selectTemplates() {
        return selectList(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getIsTemplate, Boolean.TRUE)
                .orderByDesc(InspectionTaskScheduleRequirementDO::getCreateTime));
    }

    /**
     * 根据模板名称精确查询
     */
    default InspectionTaskScheduleRequirementDO selectByTemplateName(String templateName) {
        return selectOne(new LambdaQueryWrapperX<InspectionTaskScheduleRequirementDO>()
                .eq(InspectionTaskScheduleRequirementDO::getTemplateName, templateName));
    }
}
