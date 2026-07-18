package cn.cheers.x.module.dynamicbusiness.controller.admin.relation;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 统一关联来源", description = "统一来源候选查询")
@RestController
@RequestMapping("/dynamicbusiness/relation")
@Validated
public class RelationController {

    @GetMapping("/candidates")
    @Operation(summary = "统一来源候选查询")
    @Parameter(name = "sourceBizCode", description = "源业务编码", required = true, example = "task")
    @Parameter(name = "targetKind", description = "来源类型(DYNAMIC_BIZ/PROVIDER)", required = true, example = "PROVIDER")
    @Parameter(name = "targetCode", description = "目标编码（业务编码或 providerCode）", required = true, example = "dynamic_USER")
    @PreAuthorize("@ss.hasPermission('system:reference-provider:query')")
    public CommonResult<List<ReferenceCandidateRespVO>> candidates(
            @RequestParam("sourceBizCode") @NotBlank String sourceBizCode,
            @RequestParam("targetKind") @NotBlank String targetKind,
            @RequestParam("targetCode") @NotBlank String targetCode,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        // reference 模块已废弃，统一关联候选查询将迁移到实体/关联服务
        return success(Collections.emptyList());
    }
}
