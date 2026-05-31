package cn.cheers.x.module.dynamicbusiness.controller.admin.unit;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.UnitCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.unit.UnitDO;
import cn.cheers.x.module.dynamicbusiness.service.unit.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 单位管理")
@RestController
@RequestMapping("/dynamicbusiness/unit")
@Validated
public class UnitController {

    @Resource
    private UnitService unitService;

    @GetMapping("/list")
    @Operation(summary = "获取单位列表", description = "按单位类型（可选）查询单位主数据（dynamic_unit）。")
    @Parameter(name = "unitType", description = "单位类型(可选，如 LENGTH、WEIGHT、VOLUME 等)", example = "LENGTH")
    @PreAuthorize("@ss.hasPermission('system:unit:query')")
    public CommonResult<List<UnitDO>> listUnits(@RequestParam(value = "unitType", required = false) String unitType) {
        return success(unitService.listUnits(unitType));
    }

    @PostMapping("/create")
    @Operation(summary = "创建单位", description = "创建单位主数据（dynamic_unit）。")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:unit:create')")
    public CommonResult<Long> createUnit(@Valid @RequestBody UnitCreateReqVO reqVO) {
        return success(unitService.createUnit(reqVO));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "更新单位", description = "更新单位主数据（dynamic_unit）。")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:unit:update')")
    public CommonResult<Boolean> updateUnit(@PathVariable("id") Long id, @Valid @RequestBody UnitCreateReqVO reqVO) {
        unitService.updateUnit(id, reqVO);
        return success(true);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除单位", description = "删除单位主数据（dynamic_unit）。")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:unit:delete')")
    public CommonResult<Boolean> deleteUnit(@PathVariable("id") Long id) {
        unitService.deleteUnit(id);
        return success(true);
    }
}
