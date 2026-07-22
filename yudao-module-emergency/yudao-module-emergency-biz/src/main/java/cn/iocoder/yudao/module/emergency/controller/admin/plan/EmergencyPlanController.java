package cn.iocoder.yudao.module.emergency.controller.admin.plan;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanUpdateReqVO;
import cn.iocoder.yudao.module.emergency.service.plan.EmergencyPlanService;
import cn.iocoder.yudao.module.emergency.service.plan.PlanStatusService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@ApiSupport(order = 9, author = "芋道源码")
@Tag(name = "管理后台 - 应急预案", description = "应急预案管理 - 提供应急预案的完整生命周期管理，包括预案的创建、编辑、发布、执行等功能")
@RestController
@RequestMapping("/emergency/plan")
@Validated
@Slf4j
public class EmergencyPlanController {

    @Resource
    private EmergencyPlanService emergencyPlanService;

    @Resource
    private PlanStatusService planStatusService;

    @PostMapping("/create")
    @Operation(
        summary = "创建应急预案",
        description = "创建包含步骤和附件的完整应急预案。系统会验证预案的完整性和一致性。前端展示为多步骤创建向导，包括基本信息填写、步骤配置、附件上传等步骤。"
    )
    @PreAuthorize("@ss.hasPermission('emergency:plan:create')")
    public CommonResult<Long> createEmergencyPlan(@Valid @RequestBody EmergencyPlanCreateReqVO createReqVO) {
        return success(emergencyPlanService.createEmergencyPlan(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应急预案", description = "全量更新包含步骤和附件的完整预案")
    @PreAuthorize("@ss.hasPermission('emergency:plan:update')")
    public CommonResult<Boolean> updateEmergencyPlan(@Valid @RequestBody EmergencyPlanUpdateReqVO updateReqVO) {
        emergencyPlanService.updateEmergencyPlan(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应急预案")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emergency:plan:delete')")
    public CommonResult<Boolean> deleteEmergencyPlan(@RequestParam("id") Long id) {
        emergencyPlanService.deleteEmergencyPlan(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应急预案", description = "获得包含步骤树和附件的完整预案详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emergency:plan:query')")
    public CommonResult<EmergencyPlanRespVO> getEmergencyPlan(@RequestParam("id") Long id) {
        return success(emergencyPlanService.getEmergencyPlan(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应急预案分页", description = "支持按分组筛选，planGroupId=null表示查询未分组预案")
    @PreAuthorize("@ss.hasPermission('emergency:plan:query')")
    public CommonResult<PageResult<EmergencyPlanRespVO>> getEmergencyPlanPage(@Valid cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanPageReqVO pageReqVO) {
        try {
            log.info("[getEmergencyPlanPage][请求参数] pageNo={}, pageSize={}, planGroupId={}", 
                    pageReqVO.getPageNo(), pageReqVO.getPageSize(), pageReqVO.getPlanGroupId());
            PageResult<EmergencyPlanRespVO> result = emergencyPlanService.getEmergencyPlanPage(pageReqVO);
            log.info("[getEmergencyPlanPage][查询成功] 总数={}, 当前页数据量={}", 
                    result.getTotal(), result.getList() != null ? result.getList().size() : 0);
            return success(result);
        } catch (Exception e) {
            log.error("[getEmergencyPlanPage][查询失败] 请求参数: pageNo={}, pageSize={}, planGroupId={}, 错误信息: {}", 
                    pageReqVO.getPageNo(), pageReqVO.getPageSize(), pageReqVO.getPlanGroupId(), e.getMessage(), e);
            throw e; // 重新抛出异常，让全局异常处理器处理
        }
    }

    @GetMapping("/recommend")
    @Operation(
        summary = "根据响应级别推荐应急预案",
        description = "根据事件响应级别智能推荐适用的应急预案。从预案的plan_levels数组中查找包含该响应级别的预案。支持自动推荐（排除未配置级别的预案）和手动选择模式。前端展示为预案选择列表，支持按预案类型、适用级别等维度筛选和排序。"
    )
    @Parameter(name = "responseLevel", description = "响应级别（I/II/III/IV/V）", required = true, example = "II")
    @Parameter(name = "planType", description = "预案类型（可选，用于进一步筛选）", example = "1")
    @PreAuthorize("@ss.hasPermission('emergency:plan:query')")
    public CommonResult<java.util.List<EmergencyPlanRespVO>> recommendPlans(
            @RequestParam("responseLevel") String responseLevel,
            @RequestParam(value = "planType", required = false) Integer planType) {
        return success(emergencyPlanService.recommendPlans(responseLevel, planType));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布预案", description = "将草稿状态的预案发布为已发布状态，只有已发布的预案才能被用于创建应急响应")
    @Parameter(name = "id", description = "预案ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emergency:plan:update')")
    public CommonResult<Boolean> publishPlan(@PathVariable("id") Long id) {
        planStatusService.publishPlan(id);
        return success(true);
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "停用预案", description = "将已发布状态的预案停用，停用后的预案不能用于创建新的响应，但已创建的响应可以继续使用")
    @Parameter(name = "id", description = "预案ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emergency:plan:update')")
    public CommonResult<Boolean> disablePlan(@PathVariable("id") Long id) {
        planStatusService.disablePlan(id);
        return success(true);
    }

}

