package cn.cheers.x.workorder.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardCreateReqVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardPageReqVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardRespVO;
import cn.cheers.x.workorder.controller.admin.vo.standard.FieldWorkStandardUpdateReqVO;
import cn.cheers.x.workorder.service.standard.FieldWorkStandardService;
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
 * 管理后台 - 现场作业标准（波次 2：作者面迁至 /maintenance/standards，本控制器保留兼容）
 *
 * @deprecated 新写入请使用 maintenance-server
 */
@Deprecated
@Tag(name = "管理后台 - 现场作业标准（兼容）")
@RestController
@RequestMapping("/work-order/standards")
@Validated
public class FieldWorkStandardController {

    @Resource
    private FieldWorkStandardService fieldWorkStandardService;

    @PostMapping
    @Operation(summary = "创建现场作业标准（草稿）")
    @PreAuthorize("@ss.hasPermission('work-order:standard:create')")
    public CommonResult<Long> createStandard(@Valid @RequestBody FieldWorkStandardCreateReqVO createReqVO) {
        return success(fieldWorkStandardService.createStandard(createReqVO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新现场作业标准（草稿）")
    @Parameter(name = "id", description = "标准编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('work-order:standard:update')")
    public CommonResult<Boolean> updateStandard(@PathVariable("id") Long id,
                                                @Valid @RequestBody FieldWorkStandardUpdateReqVO updateReqVO) {
        fieldWorkStandardService.updateStandard(id, updateReqVO);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "现场作业标准分页")
    @PreAuthorize("@ss.hasPermission('work-order:standard:query')")
    public CommonResult<PageResult<FieldWorkStandardRespVO>> getStandardPage(
            @Valid FieldWorkStandardPageReqVO pageReqVO) {
        return success(fieldWorkStandardService.getStandardPage(pageReqVO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取现场作业标准详情")
    @Parameter(name = "id", description = "标准编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('work-order:standard:query')")
    public CommonResult<FieldWorkStandardRespVO> getStandard(@PathVariable("id") Long id) {
        return success(fieldWorkStandardService.getStandard(id));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布现场作业标准新版本",
            description = "以当前 code 最大 version_no+1 插入已发布行；steps 取自源记录。约定：首条草稿 v1，首次 publish 为 v2。")
    @Parameter(name = "id", description = "源标准编号（通常为草稿）", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('work-order:standard:publish')")
    public CommonResult<Long> publishStandard(@PathVariable("id") Long id) {
        return success(fieldWorkStandardService.publishStandard(id));
    }

}
