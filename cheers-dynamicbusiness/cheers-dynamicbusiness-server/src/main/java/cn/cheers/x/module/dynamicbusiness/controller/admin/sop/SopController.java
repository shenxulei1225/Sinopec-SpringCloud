package cn.cheers.x.module.dynamicbusiness.controller.admin.sop;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopEffectiveService;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopStandardPackCommandService;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopStandardPackQueryService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * SOP 专用接口：读取生效配置与标准包读写。
 * 不替代实体 CRUD；创建/改差量仍走实体接口。
 */
@Tag(name = "管理后台 - 现场作业标准 SOP")
@RestController
@RequestMapping("/dynamicbusiness/sop")
@Validated
public class SopController {

    @Resource
    private SopEffectiveService sopEffectiveService;

    @Resource
    private SopStandardPackQueryService sopStandardPackQueryService;

    @Resource
    private SopStandardPackCommandService sopStandardPackCommandService;

    @GetMapping("/instances/{id}/effective")
    @Operation(summary = "读 SOP 的 merge 生效配置；缺口返回 gapCodes")
    @Parameter(name = "id", description = "SOP 实体 id", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<SopMergeResult> getEffective(@PathVariable("id") Long id) {
        return success(sopEffectiveService.getEffective(id));
    }

    @GetMapping("/{id}/standard-pack")
    @Operation(summary = "读取 SOP 标准包（适用范围 + 标准检查项包）")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<SopStandardPackRespVO> getStandardPack(
            @PathVariable("id") Long sopId) {
        return success(sopStandardPackQueryService.getStandardPack(sopId));
    }

    @PutMapping("/{id}/standard-pack")
    @Operation(summary = "覆盖保存 SOP 标准包（适用范围 + 标准检查项包）")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> upsertStandardPack(
            @PathVariable("id") Long sopId,
            @Valid @RequestBody SopStandardPackUpsertReqVO reqVO) {
        sopStandardPackCommandService.saveStandardPack(sopId, reqVO);
        return success(true);
    }
}
