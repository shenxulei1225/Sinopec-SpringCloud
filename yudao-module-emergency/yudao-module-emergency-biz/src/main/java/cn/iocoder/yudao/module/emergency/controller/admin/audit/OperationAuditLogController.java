package cn.iocoder.yudao.module.emergency.controller.admin.audit;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogPageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.audit.vo.OperationAuditLogRespVO;
import cn.iocoder.yudao.module.emergency.service.audit.OperationAuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 用户操作审计日志
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 用户操作审计日志")
@RestController
@RequestMapping("/emergency/audit-log")
@Validated
public class OperationAuditLogController {

    @Resource
    private OperationAuditLogService auditLogService;

    @GetMapping("/get")
    @Operation(summary = "获得操作审计日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emergency:audit-log:query')")
    public CommonResult<OperationAuditLogRespVO> getOperationAuditLog(@RequestParam("id") Long id) {
        OperationAuditLogRespVO auditLog = auditLogService.getOperationAuditLog(id);
        return success(auditLog);
    }

    @GetMapping("/page")
    @Operation(summary = "获得操作审计日志分页")
    @PreAuthorize("@ss.hasPermission('emergency:audit-log:query')")
    public CommonResult<PageResult<OperationAuditLogRespVO>> getOperationAuditLogPage(@Valid OperationAuditLogPageReqVO pageReqVO) {
        PageResult<OperationAuditLogRespVO> pageResult = auditLogService.getOperationAuditLogPage(pageReqVO);
        return success(pageResult);
    }
}








