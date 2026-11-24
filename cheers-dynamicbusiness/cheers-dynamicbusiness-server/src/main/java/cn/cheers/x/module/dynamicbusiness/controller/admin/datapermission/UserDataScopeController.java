package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.UserDataScopeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo.UserDataScopeSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.UserDataScopeQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.UserDataScopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 数据资源管理里给一个人保存数据范围。
 * 范围可以多选组织、场站、管线。查看和维护仍由角色决定。
 * 不负责：给这个人加职务。
 */
@Tag(name = "管理后台 - 人员数据范围")
@RestController
@RequestMapping("/dynamicbusiness/user-data-scope")
@Validated
public class UserDataScopeController {

    @Resource
    private UserDataScopeQueryService userDataScopeQueryService;
    @Resource
    private UserDataScopeService userDataScopeService;

    @GetMapping("/list")
    @Operation(summary = "列出一个人可以访问的管理单元")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<List<UserDataScopeRespVO>> list(@RequestParam("userId") Long userId) {
        return success(userDataScopeQueryService.listViewsByUser(userId).stream().map(row -> {
            UserDataScopeRespVO vo = new UserDataScopeRespVO();
            vo.setId(row.id());
            vo.setUserId(row.userId());
            vo.setRegistryCode(row.registryCode());
            vo.setUnitId(row.unitId());
            vo.setUnitName(row.unitName());
            return vo;
        }).toList());
    }

    @PutMapping("/save")
    @Operation(summary = "整份保存一个人的数据范围")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-user-role')")
    public CommonResult<Boolean> save(@Valid @RequestBody UserDataScopeSaveReqVO reqVO) {
        List<UserDataScopeService.UnitRef> units = reqVO.getUnits() == null ? List.of()
                : reqVO.getUnits().stream()
                .map(unit -> new UserDataScopeService.UnitRef(unit.getRegistryCode(), unit.getUnitId()))
                .toList();
        userDataScopeService.replace(reqVO.getUserId(), units);
        return success(true);
    }
}
