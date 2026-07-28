package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategorySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmModelTabCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 模型管理左侧分类栏")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/model-tab-category")
@Validated
public class DmModelTabCategoryController {

    @Resource
    private DmModelTabCategoryService dmModelTabCategoryService;

    @GetMapping
    @Operation(summary = "按 entityTypeCode 查询模型管理左侧分类栏")
    @Parameter(name = "entityTypeCode", description = "数据类型编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<DmModelTabCategoryRespVO> get(@RequestParam("entityTypeCode") String entityTypeCode) {
        return success(dmModelTabCategoryService.getByEntityTypeCode(entityTypeCode));
    }

    @PutMapping
    @Operation(summary = "保存模型管理左侧分类栏")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<DmModelTabCategoryRespVO> save(@Valid @RequestBody DmModelTabCategorySaveReqVO reqVO) {
        return success(dmModelTabCategoryService.save(reqVO));
    }

    @DeleteMapping
    @Operation(summary = "清除模型管理左侧分类栏")
    @ApiAccessLog(operateType = DELETE)
    @Parameter(name = "entityTypeCode", description = "数据类型编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> clear(@RequestParam("entityTypeCode") String entityTypeCode) {
        dmModelTabCategoryService.clear(entityTypeCode);
        return success(true);
    }
}
