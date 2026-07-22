package cn.iocoder.yudao.module.emergency.controller.admin.plan;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCopyReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.service.plan.PlanStepService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 预案步骤 Controller
 * <p>
 * 提供预案步骤的完整生命周期管理功能，包括：
 * - 步骤的创建、查询、更新、删除
 * - 步骤树形结构的展示和管理
 * - 步骤的复制和移动操作
 * - 步骤的层级关系维护
 * <p>
 * 前端界面预期展示：
 * ┌─ 应急预案步骤管理 ──────────────────────────┐
 * │ 预案名称：XX应急预案                          │
 * │                                              │
 * │ ┌─ 预警阶段 ──────────────────────────┐     │
 * │ │ ⚠️ 1. 监测预警信号                    │     │
 * │ │ ⚠️ 2. 启动预警机制                    │     │
 * │ │ ⚠️ 3. 通知相关人员                    │     │
 * │ └─────────────────────────────────────┘     │
 * │                                              │
 * │ ┌─ 响应阶段 ──────────────────────────┐     │
 * │ │ ┌─ I级响应 ──────┬─ II级响应 ──────┬─ III级响应 ──────┐ │
 * │ │ │ 🚨 1. 启动应急 │ 🚨 1. 组织指挥 │ 🚨 1. 启动总响应 │ │
 * │ │ │ 🚨 2. 通知领导 │ 🚨 2. 调集资源 │ 🚨 2. 协调各部门 │ │
 * │ │ │ 🚨 3. 激活预案 │ 🚨 3. 执行救援 │ 🚨 3. 管控现场   │ │
 * │ │ └───────────────┴───────────────┴───────────────┘ │
 * │ └─────────────────────────────────────┘     │
 * │                                              │
 * │ ┌─ 恢复阶段 ──────────────────────────┐     │
 * │ │ 🔄 1. 评估损失情况                    │     │
 * │ │ 🔄 2. 制定恢复计划                    │     │
 * │ │ 🔄 3. 执行恢复工作                    │     │
 * │ └─────────────────────────────────────┘     │
 * └──────────────────────────────────────────────┘
 * <p>
 * - 步骤详情弹窗：完整的步骤信息展示和编辑表单
 * - 步骤创建向导：分步骤引导用户创建完整的应急处置流程
 * - 拖拽排序：支持步骤的拖拽重新排序
 * - 阶段标识：不同处置阶段使用不同颜色和图标标识
 *
 * @author 芋道源码
 */
@ApiSupport(order = 10, author = "芋道源码")
@Tag(name = "管理后台 - 预案步骤", description = "预案步骤管理 - 提供应急预案步骤的完整CRUD操作，支持树形结构展示和管理。界面采用分阶段分组展示，包含预警、响应、恢复三个阶段的可视化管理。")
@RestController
@RequestMapping("/emergency/plan-steps")
@Validated
@Slf4j
public class PlanStepController {

    @Resource
    private PlanStepService planStepService;

    /**
     * 创建预案步骤（兼容简化接口）
     *
     * - POST /admin-api/emergency/plan-steps           （集成测试使用）
     * - POST /admin-api/emergency/plan-steps/create   （标准接口）
     *
     * 前端界面预期展示：
     * - 步骤创建弹窗：包含所有必要字段的表单
     * - 智能序号自动计算：根据父步骤自动计算步骤序号
     * - 实时验证：步骤标题必填、预案ID验证等
     * - 成功后刷新树形列表，自动展开到新创建的步骤
     */
    @PostMapping({"", "/create"})
    @Operation(
        summary = "创建预案步骤",
        description = "创建新的预案步骤，支持创建根步骤或子步骤。系统会自动计算步骤序号，验证预案和父步骤的存在性。\n\n" +
            "前端界面展示效果：\n" +
            "┌─ 创建步骤弹窗 ──────────────────────────┐\n" +
            "│ 步骤标题： [________________________]     │\n" +
            "│ 处置阶段： [⚠️ 预警] [🚨 响应] [🔄 恢复]     │\n" +
            "│ 响应级别： [I级] [II级] [III级] (响应阶段时显示) │\n" +
            "│            每个级别下可创建多个步骤              │\n" +
            "│ 父步骤：   [选择下拉框...]                 │\n" +
            "│ 步骤序号： [自动计算：5]                  │\n" +
            "│ 负责人：   [岗位][部门][用户]             │\n" +
            "│ 计划时间： [__分钟后启动]                 │\n" +
            "│ 步骤描述： [多行文本框]                   │\n" +
            "│                                          │\n" +
            "│         [创建] [取消]                     │\n" +
            "└──────────────────────────────────────────┘\n\n" +
            "表单特性：\n" +
            "- 实时验证：步骤标题必填，预案ID自动验证\n" +
            "- 智能计算：序号自动计算，父步骤联动更新\n" +
            "- 阶段标识：不同阶段显示不同图标和颜色\n" +
            "- 级别联动：选择响应阶段时显示I/II/III级别选择，每个级别可创建多个步骤\n" +
            "- 步骤组织：同级别步骤按序号排序显示，支持无限层级嵌套\n" +
            "- 成功反馈：创建成功后自动刷新步骤树并展开到新步骤"
    )
    @PreAuthorize("@ss.hasPermission('emergency:plan:create')")
    public CommonResult<Long> createStep(@Valid @RequestBody PlanStepCreateReqVO createReqVO) {
        // 调试：记录接收到的完整请求
        log.info("[createStep][Controller接收] planId={}, parentId={}, stepTitle={}, 完整对象={}",
            createReqVO.getPlanId(), createReqVO.getParentId(), createReqVO.getStepTitle(), createReqVO);
        return success(planStepService.createStep(createReqVO));
    }

    @GetMapping("/tree")
    @Operation(
        summary = "查询预案步骤树",
        description = "根据预案ID和预案级别查询步骤树形结构。支持按级别筛选步骤，返回完整的树形数据结构。\n\n" +
            "前端界面展示效果：\n" +
            "┌─ 应急预案步骤管理 ──────────────────────────┐\n" +
            "│ 预案名称：XX应急预案                          │\n" +
            "│                                              │\n" +
            "│ ┌─ 预警阶段 ──────────────────────────┐     │\n" +
            "│ │ ⚠️ 1. 监测预警信号                    │     │\n" +
            "│ │ ⚠️ 2. 启动预警机制                    │     │\n" +
            "│ │ ⚠️ 3. 通知相关人员                    │     │\n" +
            "│ └─────────────────────────────────────┘     │\n" +
            "│                                              │\n" +
            "│ ┌─ 响应阶段 ──────────────────────────┐     │\n" +
            "│ │ ┌─ I级响应 ──────┬─ II级响应 ──────┬─ III级响应 ──────┐ │\n" +
            "│ │ │ 🚨 1. 启动应急 │ 🚨 1. 组织指挥 │ 🚨 1. 启动总响应 │ │\n" +
            "│ │ │ 🚨 2. 通知领导 │ 🚨 2. 调集资源 │ 🚨 2. 协调各部门 │ │\n" +
            "│ │ │ 🚨 3. 激活预案 │ 🚨 3. 执行救援 │ 🚨 3. 管控现场   │ │\n" +
            "│ │ └───────────────┴───────────────┴───────────────┘ │\n" +
            "│ └─────────────────────────────────────┘     │\n" +
            "│                                              │\n" +
            "│ ┌─ 恢复阶段 ──────────────────────────┐     │\n" +
            "│ │ 🔄 1. 评估损失情况                    │     │\n" +
            "│ │ 🔄 2. 制定恢复计划                    │     │\n" +
            "│ │ 🔄 3. 执行恢复工作                    │     │\n" +
            "│ └─────────────────────────────────────┘     │\n" +
            "└──────────────────────────────────────────────┘\n\n" +
            "功能特性：\n" +
            "- 树形结构：支持无限层级展开/折叠\n" +
            "- 阶段分组：按预警、响应、恢复三个阶段自动分组\n" +
            "- 阶段编号：每个阶段和级别内部的步骤编号都从1开始（如预警1,2,3；响应I级1,2,3）\n" +
            "- 响应分级：响应阶段按I/II/III级别并排展示，每个级别下可创建多个步骤\n" +
            "- 级别并列：I级、II级、III级响应步骤同时显示，方便对比不同级别的处置流程\n" +
            "- 灵活扩展：每个级别和阶段下都可以无限添加步骤，支持复杂应急场景\n" +
            "- 图标标识：不同阶段和级别使用不同颜色和图标进行视觉区分\n" +
            "- 拖拽排序：支持步骤的拖拽重新排序，自动重新计算序号\n" +
            "- 搜索过滤：支持按步骤名称、阶段、级别等条件进行多维度筛选\n" +
            "- 快捷操作：右键菜单提供编辑、复制、删除等常用操作\n" +
            "- 批量操作：支持选中多个步骤进行批量编辑或删除"
    )
    @Parameter(name = "planId", description = "预案ID", required = true, example = "1024")
    @Parameter(name = "planLevel", description = "预案级别（可选）", example = "I")
    @PreAuthorize("@ss.hasPermission('emergency:plan:query')")
    public CommonResult<List<PlanStepRespVO>> getStepTree(
            @RequestParam("planId") Long planId,
            @RequestParam(value = "planLevel", required = false) String planLevel) {
        return success(planStepService.getStepTree(planId, planLevel));
    }

    @GetMapping("/get")
    @Operation(
        summary = "获得预案步骤",
        description = "根据步骤ID获取单个步骤的详细信息。返回完整的步骤数据，包括所有关联信息。前端通常在编辑或查看详情时调用。"
    )
    @Parameter(name = "id", description = "步骤ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emergency:plan:query')")
    public CommonResult<EmergencyPlanStepDO> getStep(@RequestParam("id") Long id) {
        return success(planStepService.getStep(id));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新预案步骤",
        description = "更新已存在的预案步骤信息。支持更新步骤属性、调整执行顺序、移动位置等操作。更新成功后会重新计算受影响步骤的序号。\n\n" +
            "前端界面展示效果：\n" +
            "┌─ 编辑步骤弹窗 ──────────────────────────┐\n" +
            "│ 步骤ID：   1001                         │\n" +
            "│ 步骤标题： [________________________]     │\n" +
            "│ 处置阶段： [⚠️ 预警] [🚨 响应] [🔄 恢复]     │\n" +
            "│ 父步骤：   [选择下拉框...]                 │\n" +
            "│ 步骤序号： [5]                          │\n" +
            "│ 负责人：   [岗位][部门][用户]             │\n" +
            "│ 计划时间： [30分钟后启动]                 │\n" +
            "│ 步骤描述： [多行文本框]                   │\n" +
            "│                                          │\n" +
            "│         [保存] [取消] [删除]              │\n" +
            "└──────────────────────────────────────────┘\n\n" +
            "更新特性：\n" +
            "- 数据回填：自动填充当前步骤的所有信息\n" +
            "- 变更检测：高亮显示修改过的字段\n" +
            "- 序号重算：移动步骤时自动重新计算序号\n" +
            "- 级联更新：更新父步骤时同步更新子步骤时间"
    )
    @PreAuthorize("@ss.hasPermission('emergency:plan:update')")
    public CommonResult<Boolean> updateStep(@Valid @RequestBody PlanStepUpdateReqVO updateReqVO) {
        planStepService.updateStep(updateReqVO);
        return success(true);
    }

    @PutMapping("/move")
    @Operation(
        summary = "移动预案步骤",
        description = "将步骤及其子步骤移动到新的父节点下。系统会自动重新计算受影响步骤的序号，维护树形结构的完整性。前端支持拖拽操作，可视化展示移动过程。"
    )
    @Parameter(name = "id", description = "步骤ID", required = true, example = "1024")
    @Parameter(name = "targetParentId", description = "目标父节点ID（null表示移动到根节点）", example = "2048")
    @PreAuthorize("@ss.hasPermission('emergency:plan:update')")
    public CommonResult<Boolean> moveStep(
            @RequestParam("id") Long id,
            @RequestParam(value = "targetParentId", required = false) Long targetParentId) {
        planStepService.moveStep(id, targetParentId);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除预案步骤",
        description = "删除指定的预案步骤。支持两种删除模式：级联删除（同时删除所有子步骤）或提升子步骤（将子步骤提升到父级）。删除后会重新计算剩余步骤的序号。\n\n" +
            "前端界面展示效果：\n" +
            "┌─ 删除确认对话框 ──────────────────────┐\n" +
            "│ ⚠️ 确定要删除步骤吗？                    │\n" +
            "│                                        │\n" +
            "│ 步骤：启动应急响应                      │\n" +
            "│ 阶段：🚨 响应阶段                      │\n" +
            "│                                        │\n" +
            "│ 删除模式：                              │\n" +
            "│ ○ 级联删除（同时删除3个子步骤）        │\n" +
            "│ ● 提升子步骤（子步骤提升到父级）        │\n" +
            "│                                        │\n" +
            "│ 💡 注意：删除后序号将自动重新计算      │\n" +
            "│                                        │\n" +
            "│         [确定删除] [取消]              │\n" +
            "└────────────────────────────────────────┘\n\n" +
            "删除特性：\n" +
            "- 风险提示：显示将要删除的步骤数量和影响\n" +
            "- 模式选择：让用户选择删除策略\n" +
            "- 不可恢复：重要操作需要二次确认\n" +
            "- 序号重算：删除后自动重新计算所有步骤序号"
    )
    @Parameter(name = "id", description = "步骤ID", required = true, example = "1024")
    @Parameter(name = "cascade", description = "是否级联删除子步骤。true=级联删除，false=提升子步骤到父级", example = "true")
    @PreAuthorize("@ss.hasPermission('emergency:plan:delete')")
    public CommonResult<Boolean> deleteStep(
            @RequestParam("id") Long id,
            @RequestParam(value = "cascade", defaultValue = "true") boolean cascade) {
        planStepService.deleteStep(id, cascade);
        return success(true);
    }

    @PostMapping("/copy")
    @Operation(
        summary = "复制预案步骤",
        description = "复制指定的步骤及其整个子树结构。支持复制到同一预案的其他位置或不同预案。可调整时间偏移量来适应新的执行环境。\n\n" +
            "前端界面展示效果：\n" +
            "┌─ 复制步骤配置 ────────────────────────┐\n" +
            "│ 源步骤：启动应急响应                     │\n" +
            "│ 包含子步骤：3个                          │\n" +
            "│                                        │\n" +
            "│ 目标预案： [选择下拉框...]               │\n" +
            "│ 目标级别： [I] [II] [III] [IV] [V]      │\n" +
            "│ 父步骤：   [选择下拉框...]               │\n" +
            "│                                        │\n" +
            "│ 时间偏移： [+30分钟]                    │\n" +
            "│ 💡 正数=延后，负数=提前                 │\n" +
            "│                                        │\n" +
            "│         [开始复制] [取消]               │\n" +
            "└────────────────────────────────────────┘\n\n" +
            "复制特性：\n" +
            "- 树结构保持：完整复制步骤及其所有子步骤\n" +
            "- 时间调整：支持设置时间偏移适应新环境\n" +
            "- 目标选择：可选择不同预案或同一预案的位置\n" +
            "- 进度反馈：显示复制进度和结果统计"
    )
    @PreAuthorize("@ss.hasPermission('emergency:plan:create')")
    public CommonResult<Long> copyStep(@Valid @RequestBody PlanStepCopyReqVO copyReqVO) {
        return success(planStepService.copyStep(copyReqVO));
    }

    @PostMapping("/fix-orphaned-steps")
    @Operation(
        summary = "修复孤立步骤",
        description = "检测并修复数据完整性问题，将引用已删除父步骤的步骤的parent_id设为null"
    )
    @PreAuthorize("@ss.hasPermission('emergency:plan:update')")
    public CommonResult<Integer> fixOrphanedSteps() {
        return success(planStepService.fixOrphanedSteps());
    }

    @GetMapping("/active-step-ids")
    @Operation(
        summary = "获取活跃步骤ID列表",
        description = "获取指定预案的所有活跃（未删除）步骤ID列表，用于前端刷新步骤选择器"
    )
    @Parameter(name = "planId", description = "预案ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emergency:plan:query')")
    public CommonResult<List<Long>> getActiveStepIds(@RequestParam("planId") Long planId) {
        return success(planStepService.getActiveStepIds(planId));
    }

    @PostMapping("/cleanup-deleted-steps")
    @Operation(
        summary = "清理已删除的步骤记录",
        description = "物理删除所有已软删除的步骤记录，用于清理测试数据。生产环境请谨慎使用！"
    )
    @PreAuthorize("@ss.hasPermission('emergency:plan:delete')")
    public CommonResult<Integer> cleanupDeletedSteps() {
        return success(planStepService.cleanupDeletedSteps());
    }
}
