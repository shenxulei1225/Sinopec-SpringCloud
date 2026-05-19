package cn.iocoder.yudao.module.inspection.task.service.schedule;

import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementCreateReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementRespVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementUpdateReqVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO.ScheduleTemplateConfig;

import java.util.List;

/**
 * 巡检任务排期需求 Service 接口
 *
 * <p>排期需求是一个配置容器，包含多个模板组合。</p>
 */
public interface InspectionTaskScheduleRequirementService {

    // ==================== 基础操作 ====================

    /**
     * 创建排期需求
     */
    Long createScheduleRequirement(InspectionTaskScheduleRequirementCreateReqVO createReqVO);

    /**
     * 更新排期需求
     */
    void updateScheduleRequirement(InspectionTaskScheduleRequirementUpdateReqVO updateReqVO);

    /**
     * 删除排期需求
     */
    void deleteScheduleRequirement(Long id);

    /**
     * 获取排期需求详情
     */
    InspectionTaskScheduleRequirementRespVO getScheduleRequirement(Long id);

    /**
     * 根据任务ID获取排期需求
     */
    InspectionTaskScheduleRequirementRespVO getScheduleRequirementByTaskId(Long taskId);

    // ==================== 模板组合操作 ====================

    /**
     * 添加模板组合到排期需求
     *
     * @param requirementId 排期需求ID
     * @param templateConfig 模板组合配置
     */
    void addTemplateConfig(Long requirementId, ScheduleTemplateConfig templateConfig);

    /**
     * 更新模板组合配置
     *
     * @param requirementId 排期需求ID
     * @param templateId 模板ID
     * @param templateConfig 新的模板组合配置
     */
    void updateTemplateConfig(Long requirementId, Long templateId, ScheduleTemplateConfig templateConfig);

    /**
     * 删除模板组合
     *
     * @param requirementId 排期需求ID
     * @param templateId 模板ID
     */
    void removeTemplateConfig(Long requirementId, Long templateId);

    /**
     * 恢复模板配置（移除 changedFields，使用 originalConfig）
     *
     * @param requirementId 排期需求ID
     * @param templateId 模板ID
     */
    void restoreTemplateConfig(Long requirementId, Long templateId);

    // ==================== 查询操作 ====================

    /**
     * 获取任务关联的所有排期需求
     */
    List<InspectionTaskScheduleRequirementRespVO> getScheduleRequirementsByTaskId(Long taskId);

    /**
     * 获取所有启用的模板组合（用于编排引擎）
     */
    List<ScheduleTemplateConfig> getEnabledTemplateConfigs();
}
