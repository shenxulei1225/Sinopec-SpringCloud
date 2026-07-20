package cn.iocoder.yudao.module.emergency.controller.admin.backup;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.backup.vo.*;
import cn.iocoder.yudao.module.emergency.convert.backup.BackupRecordConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.backup.BackupRecordDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.backup.BackupRecordMapper;
import cn.iocoder.yudao.module.emergency.service.backup.DatabaseBackupService;
import cn.iocoder.yudao.module.emergency.service.backup.DatabaseRestoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 数据备份管理 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 数据备份管理")
@RestController
@RequestMapping("/emergency/backup")
public class BackupRecordController {

    @Resource
    private DatabaseBackupService backupService;

    @Resource
    private DatabaseRestoreService restoreService;

    @Resource
    private BackupRecordMapper backupRecordMapper;

    @PostMapping("/create")
    @Operation(summary = "创建备份")
    @PreAuthorize("@ss.hasPermission('emergency:backup:create')")
    public CommonResult<Long> createBackup(@Valid @RequestBody BackupCreateReqVO createReqVO) {
        Long backupId;
        switch (createReqVO.getBackupType()) {
            case "FULL":
                backupId = backupService.createFullBackup(createReqVO.getDescription());
                break;
            case "TENANT":
                backupId = backupService.createTenantBackup(createReqVO.getTenantId(), createReqVO.getDescription());
                break;
            case "MODULE":
                backupId = backupService.createModuleBackup(createReqVO.getModuleName(), createReqVO.getDescription());
                break;
            default:
                throw new IllegalArgumentException("不支持的备份类型: " + createReqVO.getBackupType());
        }
        return success(backupId);
    }

    @GetMapping("/get")
    @Operation(summary = "获得备份记录")
    @PreAuthorize("@ss.hasPermission('emergency:backup:query')")
    public CommonResult<BackupRecordRespVO> getBackupRecord(@RequestParam("id") Long id) {
        BackupRecordDO backupRecord = backupService.getBackupRecord(id);
        return success(BackupRecordConvert.INSTANCE.convert(backupRecord));
    }

    @GetMapping("/page")
    @Operation(summary = "获得备份记录分页")
    @PreAuthorize("@ss.hasPermission('emergency:backup:query')")
    public CommonResult<PageResult<BackupRecordRespVO>> getBackupRecordPage(@Valid BackupRecordPageReqVO pageReqVO) {
        PageResult<BackupRecordDO> pageResult = backupRecordMapper.selectPage(pageReqVO,
                new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<BackupRecordDO>()
                        .eqIfPresent(BackupRecordDO::getBackupType, pageReqVO.getBackupType())
                        .eqIfPresent(BackupRecordDO::getStatus, pageReqVO.getStatus())
                        .eqIfPresent(BackupRecordDO::getTenantId, pageReqVO.getTenantId())
                        .orderByDesc(BackupRecordDO::getCreateTime));
        return success(BackupRecordConvert.INSTANCE.convertPage(pageResult));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除备份")
    @PreAuthorize("@ss.hasPermission('emergency:backup:delete')")
    public CommonResult<Boolean> deleteBackup(@RequestParam("id") Long id) {
        backupService.deleteBackup(id);
        return success(true);
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "恢复备份")
    @PreAuthorize("@ss.hasPermission('emergency:backup:restore')")
    public CommonResult<Long> restoreBackup(@PathVariable("id") Long id,
                                            @Valid @RequestBody BackupRestoreReqVO restoreReqVO) {
        Long preRestoreBackupId = restoreService.restoreBackup(id, restoreReqVO.getConfirmRestore());
        return success(preRestoreBackupId);
    }
}








