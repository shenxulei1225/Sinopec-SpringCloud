package cn.cheers.x.module.dynamicbusiness.controller.admin.sop;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopTemplatePageReqVO;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopTemplateQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * SOP 模板库读接口：统一库列表只走本路径，服务端固定 is_template=true。
 */
@Tag(name = "管理后台 - SOP 模板库", description = "SOP 统一库模板行分页查询；实例行不在此接口返回")
@RestController
@RequestMapping("/dynamicbusiness/sop/templates")
@Validated
public class SopTemplateController {

    @Resource
    private SopTemplateQueryService sopTemplateQueryService;

    @GetMapping
    @Operation(
            summary = "SOP 模板库分页（GET）",
            description = """
                    仅返回 is_template=true 的 SOP 行；分类筛选、关键词与 query-by-scene ENTITIES_BY_CATEGORY 一致。
                    客户端不可通过 fieldFilters 关闭模板约束。
                    """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntitySceneQueryRespVO> pageTemplates(@Valid SopTemplatePageReqVO reqVO) {
        return success(sopTemplateQueryService.pageTemplates(reqVO));
    }

    @PostMapping
    @Operation(
            summary = "SOP 模板库分页（POST JSON）",
            description = "与 GET 语义一致；categoryIds / categoryIdGroups 较多时使用 JSON body。"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<EntitySceneQueryRespVO> pageTemplatesByBody(@Valid @RequestBody SopTemplatePageReqVO reqVO) {
        return success(sopTemplateQueryService.pageTemplates(reqVO));
    }

}
