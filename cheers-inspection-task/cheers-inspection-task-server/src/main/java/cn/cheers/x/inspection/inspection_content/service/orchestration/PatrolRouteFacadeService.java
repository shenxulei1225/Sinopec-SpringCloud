package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolInspectionConfigReadinessRespVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolOrchestrationRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleConflictReportDTO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;

/**
 * 巡检门面：路径规划与智能编排（orchestration）分轨调用中台引擎。
 * <p>不负责：排期模板 CRUD、任务 CRUD、直连 scheduling/runtime 内部实现。
 */
public interface PatrolRouteFacadeService {

    /**
     * 路径规划步·预览路线：展开作业项 + 算路，dryRun；不写 plannedRoute，不占设备时间轴。
     */
    ScheduleRunResponse previewRoute(PatrolRouteRunReqVO reqVO);

    /**
     * 冲突检测：按这一页的排期模板与指定设备对照已有占窗。
     * 没有重叠时带上计划时间点。有重叠时不带计划点。不写快照、不生成步骤图、不生成指令。
     */
    ScheduleConflictReportDTO detectScheduleConflicts(PatrolOrchestrationRunReqVO reqVO);

    /**
     * 第 3 步·只查配置：执行设备、协议、路线端点、检查项方法是否齐全；不生成步骤图、不试译。
     */
    PatrolInspectionConfigReadinessRespVO validateInspectionConfig(PatrolOrchestrationRunReqVO reqVO);

    /**
     * 第 3 步·只生成步骤图：不写试排、不试译、不写任务。须先 {@link #validateInspectionConfig} 无缺口。
     */
    ScheduleRunResponse composeExecutionStepTree(PatrolOrchestrationRunReqVO reqVO);

    /**
     * 第 3 步·只生成指令：用页面上已经生成的步骤图试译一次。
     * 各计划点用同一份指令。时间重叠不参与这一步。
     * 不重新编步骤图，不写出计划点，不写草稿，不替换已发布步骤图。
     */
    ScheduleRunResponse previewPageInstructions(PatrolOrchestrationRunReqVO reqVO);

    /**
     * 智能编排·试排：冲突解决 + 调用步骤图生成，结果写草稿 orchestrationPreview*；不写 runtimeJobId、不物化待执行账。
     * <p>同一任务再次试排时，若存在存量 runtime 试排占窗则先释放再重算。
     */
    ScheduleRunResponse previewOrchestration(PatrolOrchestrationRunReqVO reqVO);

    /**
     * 生成任务（commitOrchestration）：占窗定稿 → 写待执行 → 标记已生成并排期 → 登记到点；与向导底栏、列表启用第二步同序。
     */
    ScheduleRunResponse commitOrchestration(PatrolOrchestrationRunReqVO reqVO);

    /**
     * 任务列表·停用：关到点自动开跑，释放未执行占窗并清除试排草稿；与建任务向导无关。
     */
    void abortOrchestration(PatrolTaskPauseReqVO reqVO);

    void writebackSlot(PatrolSlotWritebackReqVO reqVO);
}
