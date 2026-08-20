package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmDataTabColumnRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据 Tab 栏间关系声明")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/data-tab-column-relation")
@Validated
public class DmDataTabColumnRelationController {

    @Resource
    private DmDataTabColumnRelationService dmDataTabColumnRelationService;

    @GetMapping
    @Operation(summary = "按布局 id 或目录编码查询栏间关系声明")
    @Parameter(name = "layoutId", description = "工作台布局实例 id（优先）")
    @Parameter(name = "entityTypeCode", description = "目录编码；无 layoutId 时解析其 dataLayoutId")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<DmDataTabColumnRelationRespVO>> list(
            @RequestParam(value = "layoutId", required = false) Long layoutId,
            @RequestParam(value = "entityTypeCode", required = false) String entityTypeCode) {
        if (layoutId != null) {
            return success(dmDataTabColumnRelationService.listByLayoutId(layoutId));
        }
        if (StringUtils.hasText(entityTypeCode)) {
            return success(dmDataTabColumnRelationService.listByEntityTypeCode(entityTypeCode));
        }
        throw new ServiceException(400, "layoutId 与 entityTypeCode 不能同时为空");
    }

    @PutMapping
    @Operation(summary = "批量保存栏间关系声明（整页替换）")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody DmDataTabColumnRelationSaveReqVO reqVO) {
        dmDataTabColumnRelationService.saveRelations(reqVO);
        return success(true);
    }
}
