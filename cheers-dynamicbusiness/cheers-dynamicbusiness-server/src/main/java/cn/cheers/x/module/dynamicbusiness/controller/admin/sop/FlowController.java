package cn.cheers.x.module.dynamicbusiness.controller.admin.sop;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.FlowPublishReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.FlowSaveVersionReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.FlowStandardPackRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.FlowStandardPackUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.service.sop.FlowEffectiveService;
import cn.cheers.x.module.dynamicbusiness.service.sop.FlowPublishCommandService;
import cn.cheers.x.module.dynamicbusiness.service.sop.FlowStandardPackCommandService;
import cn.cheers.x.module.dynamicbusiness.service.sop.FlowStandardPackQueryService;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.FlowMergeResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * SOP 专用接口：读取生效配置与标准包读写。
 * 不替代实体 CRUD；创建/改差量仍走实体接口。
 */
@Tag(name = "管理后台 - 现场作业标准流程")
@RestController
@RequestMapping("/dynamicbusiness/sop")
@Validated
public class FlowController {

    @Resource
    private FlowEffectiveService flowEffectiveService;

    @Resource
    private FlowStandardPackQueryService flowStandardPackQueryService;

    @Resource
    private FlowStandardPackCommandService flowStandardPackCommandService;

    @Resource
    private FlowPublishCommandService flowPublishCommandService;

    @GetMapping("/instances/{id}/effective")
    @Operation(summary = "读流程 merge 生效配置；缺口返回 gapCodes")
    @Parameter(name = "id", description = "SOP 实体 id", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<FlowMergeResult> getEffective(@PathVariable("id") Long id) {
        return success(flowEffectiveService.getEffective(id));
    }

    @GetMapping("/{id}/standard-pack")
    @Operation(summary = "读取流程标准包（适用范围 + 标准检查项包）")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<FlowStandardPackRespVO> getStandardPack(
            @PathVariable("id") Long flowId) {
        return success(flowStandardPackQueryService.getStandardPack(flowId));
    }

    @PutMapping("/{id}/standard-pack")
    @Operation(summary = "覆盖保存流程标准包（适用范围 + 标准检查项包）")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> upsertStandardPack(
            @PathVariable("id") Long flowId,
            @Valid @RequestBody FlowStandardPackUpsertReqVO reqVO) {
        flowStandardPackCommandService.saveStandardPack(flowId, reqVO);
        return success(true);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "按指定版本号发布 SOP")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> publishByVersion(
            @PathVariable("id") Long flowId,
            @Valid @RequestBody FlowPublishReqVO reqVO) {
        flowPublishCommandService.publishByVersion(flowId, reqVO.getVersionNo());
        return success(true);
    }

    @PostMapping("/{id}/save-version")
    @Operation(summary = "保存到指定版本号（新建或覆盖未发布版本）")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> saveToVersion(
            @PathVariable("id") Long flowId,
            @Valid @RequestBody FlowSaveVersionReqVO reqVO) {
        flowPublishCommandService.saveToVersion(flowId, reqVO.getVersionNo());
        return success(true);
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "撤回当前 SOP 的发布状态")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> unpublish(@PathVariable("id") Long flowId) {
        flowPublishCommandService.unpublish(flowId);
        return success(true);
    }
}
