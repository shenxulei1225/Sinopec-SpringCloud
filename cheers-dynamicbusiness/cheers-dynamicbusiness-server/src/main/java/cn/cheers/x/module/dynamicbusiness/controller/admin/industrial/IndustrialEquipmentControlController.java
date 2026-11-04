package cn.cheers.x.module.dynamicbusiness.controller.admin.industrial;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.devicehistory.IndustrialEquipmentBoard;
import cn.cheers.x.module.dynamicbusiness.devicehistory.IndustrialEquipmentControl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 设备图层提交一条设备动作时，把对上的写点交给现场网关。
 * 分区树和设备名单走设备管理的分类查询，不在这里另开名单。
 * 可监测、可控制只标已有的工况读点和开关，不另查一套设备。
 * 成功只表示现场网关收下了这次值。不表示设备已经打开，也不消除告警。
 * 不订写点，不猜节点标识。没有远程读数时拒绝下发。
 */
@Tag(name = "管理后台 - 工业设备控制")
@RestController
@RequestMapping("/dynamicbusiness/industrial-equipment")
@Validated
public class IndustrialEquipmentControlController {

    private final IndustrialEquipmentControl control;
    private final IndustrialEquipmentBoard board;

    public IndustrialEquipmentControlController(IndustrialEquipmentControl control, IndustrialEquipmentBoard board) {
        this.control = control;
        this.board = board;
    }

    @PostMapping("/capabilities")
    @Operation(summary = "这一批设备里哪些可监测、哪些可控制")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<List<IndustrialEquipmentBoard.Capability>> capabilities(
            @RequestBody(required = false) List<Long> equipmentIds) {
        return success(board.capabilities(TenantContextHolder.getRequiredTenantId(), equipmentIds));
    }

    @GetMapping("/status")
    @Operation(summary = "这台设备的工况、远程和可点的动作")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<IndustrialEquipmentBoard.Status> status(@RequestParam("equipmentId") Long equipmentId) {
        return success(board.status(TenantContextHolder.getRequiredTenantId(), equipmentId));
    }

    @PostMapping("/submit")
    @Operation(summary = "对一台设备提交一条设备动作")
    @Parameter(name = "actionCode", description = "动作编码", required = true)
    @Parameter(name = "equipmentId", description = "设备编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity:update')")
    public CommonResult<String> submit(@RequestParam("actionCode") String actionCode,
                                       @RequestParam("equipmentId") Long equipmentId) {
        control.submit(TenantContextHolder.getRequiredTenantId(), actionCode, equipmentId);
        return success("已交给现场网关");
    }
}
