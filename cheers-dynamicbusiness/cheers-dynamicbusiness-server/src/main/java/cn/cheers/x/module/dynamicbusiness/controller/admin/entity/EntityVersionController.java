package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityVersionOptionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityVersionPublishReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityVersionSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityVersionUnpublishReqVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.version.EntityVersionCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

import java.util.List;

/**
 * 管理后台 - 通用版本管理命令。
 *
 * <p>面向所有启用 version-management 的数据类型；SOP 可继续兼容旧接口。</p>
 */
@Tag(name = "管理后台 - 通用实体版本管理")
@RestController
@RequestMapping("/dynamicbusiness/business/entities/version")
@Validated
public class EntityVersionController {

    @Resource
    private EntityVersionCommandService entityVersionCommandService;

    @PostMapping("/save")
    @Operation(summary = "保存到指定版本号（新建或覆盖未发布版本）")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> saveToVersion(@Valid @RequestBody EntityVersionSaveReqVO reqVO) {
        entityVersionCommandService.saveToVersion(
                reqVO.getEntityTypeCode(),
                reqVO.getEntityId(),
                reqVO.getVersionNo()
        );
        return success(true);
    }

    @PostMapping("/publish")
    @Operation(summary = "按指定版本号发布")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> publishByVersion(@Valid @RequestBody EntityVersionPublishReqVO reqVO) {
        entityVersionCommandService.publishByVersion(
                reqVO.getEntityTypeCode(),
                reqVO.getEntityId(),
                reqVO.getVersionNo()
        );
        return success(true);
    }

    @PostMapping("/unpublish")
    @Operation(summary = "撤回发布（同 code 全部置未发布）")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<Boolean> unpublish(@Valid @RequestBody EntityVersionUnpublishReqVO reqVO) {
        entityVersionCommandService.unpublish(
                reqVO.getEntityTypeCode(),
                reqVO.getEntityId()
        );
        return success(true);
    }

    @GetMapping("/options")
    @Operation(summary = "查询同 code 版本下拉列表")
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<List<EntityVersionOptionRespVO>> listVersionOptions(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("entityId") Long entityId
    ) {
        return success(entityVersionCommandService.listVersionOptions(entityTypeCode, entityId));
    }
}

