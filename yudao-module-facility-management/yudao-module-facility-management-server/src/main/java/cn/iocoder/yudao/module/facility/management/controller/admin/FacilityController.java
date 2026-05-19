package cn.iocoder.yudao.module.facility.management.controller.admin;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.*;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.FacilityDO;
import cn.iocoder.yudao.module.facility.management.service.facility.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 设施管理接口
 * <p>
 * 提供设施的 CRUD 操作接口
 */
@Tag(name = "管理后台 - 设施管理")
@RestController
@RequestMapping("/facility")
public class FacilityController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private FacilityService facilityService;

    // ==================== 设施 CRUD ====================

    @PostMapping("/create")
    @Operation(summary = "创建设施")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('facility:create')")
    public CommonResult<Long> createFacility(@Valid @RequestBody FacilityCreateReqVO reqVO) {
        Long id = facilityService.createFacility(reqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新设施")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('facility:update')")
    public CommonResult<Boolean> updateFacility(@Valid @RequestBody FacilityUpdateReqVO reqVO) {
        facilityService.updateFacility(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设施")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('facility:delete')")
    public CommonResult<Boolean> deleteFacility(@RequestParam("id") Long id) {
        facilityService.deleteFacility(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取设施详情")
    @PreAuthorize("@ss.hasPermission('facility:query')")
    public CommonResult<FacilityRespVO> getFacility(@RequestParam("id") Long id) {
        FacilityDO facility = facilityService.getFacility(id);
        if (facility == null) {
            return success(null);
        }
        return success(toFacilityRespVO(facility));
    }

    @GetMapping("/check-code")
    @Operation(summary = "检查设施编码唯一性")
    @PreAuthorize("@ss.hasPermission('facility:query')")
    public CommonResult<Boolean> checkFacilityCode(@RequestParam("code") String code, @RequestParam(value = "excludeId", required = false) Long excludeId) {
        FacilityDO existing = facilityService.getFacilityByCode(code);
        if (existing == null) {
            return success(true); // 不存在，可以使用
        }
        if (excludeId != null && existing.getId().equals(excludeId)) {
            return success(true); // 排除自身，可以用
        }
        return success(false); // 已存在，不可使用
    }

    // ==================== 转换方法 ====================

    private FacilityRespVO toFacilityRespVO(FacilityDO facility) {
        if (facility == null) {
            return null;
        }
        FacilityRespVO vo = BeanUtils.toBean(facility, FacilityRespVO.class);
        // 设置状态描述
        vo.setStatusDesc(facility.getStatus() != null && facility.getStatus() == 0 ? "正常" : "停用");
        // 格式化安装日期
        if (facility.getInstallDate() != null) {
            vo.setInstallDate(facility.getInstallDate().toString());
        }
        // 格式化时间
        if (facility.getCreateTime() != null) {
            vo.setCreateTime(facility.getCreateTime().format(DATE_FORMATTER));
        }
        if (facility.getUpdateTime() != null) {
            vo.setUpdateTime(facility.getUpdateTime().format(DATE_FORMATTER));
        }
        return vo;
    }

}
