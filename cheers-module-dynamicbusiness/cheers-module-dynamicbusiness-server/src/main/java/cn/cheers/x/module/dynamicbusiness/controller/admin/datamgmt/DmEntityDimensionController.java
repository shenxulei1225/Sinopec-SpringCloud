package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmEntityDimensionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmEntityDimensionSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datamgmt.DmEntityDimensionService;
import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据管理浏览维度")
@RestController
@RequestMapping("/dynamicbusiness/data-mgmt/entity-dimensions")
@Validated
public class DmEntityDimensionController {

    @Resource
    private DmEntityDimensionService dmEntityDimensionService;

    @GetMapping
    @Operation(summary = "按 entityTypeCode 查询数据浏览维度")
    @Parameter(name = "entityTypeCode", description = "数据类型编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<DmEntityDimensionRespVO>> list(@RequestParam("entityTypeCode") String entityTypeCode) {
        return success(dmEntityDimensionService.listByEntityTypeCode(entityTypeCode));
    }

    @PutMapping
    @Operation(summary = "批量保存数据浏览维度")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody DmEntityDimensionSaveReqVO reqVO) {
        dmEntityDimensionService.saveDimensions(reqVO);
        return success(true);
    }
}
