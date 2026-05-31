package cn.cheers.x.module.dynamicbusiness.controller.admin.migration;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.migration.DataMigrationService;
import cn.cheers.x.module.dynamicbusiness.service.migration.vo.MigrationResultVO;
import cn.cheers.x.module.dynamicbusiness.service.migration.vo.MigrationStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 数据迁移 Controller
 * 
 * <p>提供业务动态关联功能的数据迁移 API，包括：</p>
 * <ul>
 *   <li>关联字段迁移到关联字段库</li>
 *   <li>智能默认设置应用到现有字段</li>
 *   <li>迁移状态查询</li>
 *   <li>迁移回滚</li>
 * </ul>
 * 
 * @author yudao
 * @since 2026-01-07
 */
@Tag(name = "管理后台 - 数据迁移")
@RestController
@RequestMapping("/dynamicbusiness/data-migration")
@Validated
public class DataMigrationController {

    @Resource
    private DataMigrationService dataMigrationService;


    @GetMapping("/status")
    @Operation(summary = "获取迁移状态", description = "获取当前系统的数据迁移状态")
    @PreAuthorize("@ss.hasPermission('system:data-migration:query')")
    public CommonResult<MigrationStatusVO> getMigrationStatus() {
        return success(dataMigrationService.getMigrationStatus());
    }

    @PostMapping("/relation-fields")
    @Operation(summary = "迁移关联字段", description = "将现有 ENTITY_REF 类型字段迁移到关联字段库架构")
    @Parameter(name = "dryRun", description = "是否试运行（true: 只分析不执行）", required = false)
    @PreAuthorize("@ss.hasPermission('system:data-migration:execute')")
    public CommonResult<MigrationResultVO> migrateRelationFields(
            @RequestParam(value = "dryRun", defaultValue = "true") Boolean dryRun) {
        return success(dataMigrationService.migrateRelationFieldsToLibrary(dryRun));
    }

    @PostMapping("/smart-defaults")
    @Operation(summary = "应用智能默认设置", description = "为现有字段应用智能默认的可查询和索引策略设置")
    @Parameter(name = "dryRun", description = "是否试运行", required = false)
    @Parameter(name = "overwrite", description = "是否覆盖已有设置", required = false)
    @PreAuthorize("@ss.hasPermission('system:data-migration:execute')")
    public CommonResult<MigrationResultVO> applySmartDefaults(
            @RequestParam(value = "dryRun", defaultValue = "true") Boolean dryRun,
            @RequestParam(value = "overwrite", defaultValue = "false") Boolean overwrite) {
        return success(dataMigrationService.applySmartDefaultsToExistingFields(dryRun, overwrite));
    }

    @PostMapping("/full")
    @Operation(summary = "执行完整迁移", description = "按顺序执行所有迁移任务")
    @Parameter(name = "dryRun", description = "是否试运行", required = false)
    @PreAuthorize("@ss.hasPermission('system:data-migration:execute')")
    public CommonResult<MigrationResultVO> executeFullMigration(
            @RequestParam(value = "dryRun", defaultValue = "true") Boolean dryRun) {
        return success(dataMigrationService.executeFullMigration(dryRun));
    }

    @PostMapping("/rollback/relation-fields")
    @Operation(summary = "回滚关联字段迁移", description = "将已迁移的关联字段回滚到迁移前状态")
    @PreAuthorize("@ss.hasPermission('system:data-migration:rollback')")
    public CommonResult<MigrationResultVO> rollbackRelationFieldMigration() {
        return success(dataMigrationService.rollbackRelationFieldMigration());
    }

    @GetMapping("/validate")
    @Operation(summary = "验证迁移数据完整性", description = "检查迁移后的数据是否完整和一致")
    @PreAuthorize("@ss.hasPermission('system:data-migration:query')")
    public CommonResult<MigrationResultVO> validateMigrationIntegrity() {
        return success(dataMigrationService.validateMigrationIntegrity());
    }
}
