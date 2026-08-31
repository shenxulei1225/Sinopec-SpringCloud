package cn.cheers.x.module.dynamicbusiness.controller.admin.sop;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopPromoteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopPromoteRespVO;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopTemplateCommandService;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * SOP 模板/实例专用接口：读 merge 生效配置、升格为新模板。
 * 不替代实体 CRUD；创建/改差量仍走实体接口。
 */
@Tag(name = "管理后台 - 现场作业标准 SOP")
@RestController
@RequestMapping("/dynamicbusiness/sop")
@Validated
public class SopController {

    @Resource
    private SopTemplateCommandService sopTemplateCommandService;

    @GetMapping("/instances/{id}/effective")
    @Operation(summary = "读 SOP 实例或模板的 merge 生效配置；缺口返回 gapCodes")
    @Parameter(name = "id", description = "SOP 实体 id（模板或实例）", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<SopMergeResult> getEffective(@PathVariable("id") Long id) {
        return success(sopTemplateCommandService.getEffective(id));
    }

    @PostMapping("/instances/{id}/promote-to-template")
    @Operation(summary = "将 SOP 实例升格为新模板；不改原实例、不改实例绑定")
    @Parameter(name = "id", description = "SOP 实例 id", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity:create')")
    public CommonResult<SopPromoteRespVO> promoteToTemplate(
            @PathVariable("id") Long id,
            @Valid @RequestBody SopPromoteReqVO reqVO) {
        long newId = sopTemplateCommandService.promoteInstanceToTemplate(
                id, reqVO.getName(), reqVO.getCategoryIds());
        SopPromoteRespVO resp = new SopPromoteRespVO();
        resp.setNewTemplateId(newId);
        return success(resp);
    }
}
