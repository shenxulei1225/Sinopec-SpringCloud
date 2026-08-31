package cn.cheers.x.module.dynamicbusiness.controller.admin.action;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListReqVO;
import cn.cheers.x.module.dynamicbusiness.service.action.ActionQueryService;
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

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 动作库读接口：按手段、按启用主体列出动作。
 *
 * <p><b>负责</b>：SOP 编模板选用（全库）与按 owner 启用集收窄。</p>
 * <p><b>不负责</b>：动作 CRUD（走通用实体接口）；启用行写入；检查业务。</p>
 * <p><b>禁止</b>：owner 已传且启用为空时返回全库。</p>
 */
@Tag(name = "管理后台 - 动作库", description = "动作列表；可选 executionMeans / ownerKind+ownerId 过滤")
@RestController
@RequestMapping("/dynamicbusiness/actions")
@Validated
public class ActionQueryController {

    @Resource
    private ActionQueryService actionQueryService;

    @GetMapping
    @Operation(
            summary = "动作库列表（GET）",
            description = """
                    未传 ownerKind+ownerId：返回全库动作（可叠 executionMeans），供 SOP 模板编排。
                    传了 ownerKind+ownerId：仅返回该主体启用的动作；启用为空则空列表（不兜底全库）。
                    """
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<ActionListItemRespVO>> listActions(@Valid ActionListReqVO reqVO) {
        return success(actionQueryService.listActions(reqVO));
    }

    @PostMapping("/list")
    @Operation(
            summary = "动作库列表（POST JSON）",
            description = "与 GET 语义一致；便于前端统一 body 传参。"
    )
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<ActionListItemRespVO>> listActionsByBody(
            @Valid @RequestBody ActionListReqVO reqVO) {
        return success(actionQueryService.listActions(reqVO));
    }
}
