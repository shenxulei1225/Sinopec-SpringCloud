package cn.cheers.x.module.dynamicbusiness.controller.admin.relation;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryWithStatusVO;
import cn.cheers.x.module.dynamicbusiness.service.relation.RelationFieldLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 关联字段库 Controller
 * 
 * 提供关联字段库的管理接口，支持：
 * - 关联字段的 CRUD 操作
 * - 字段状态查询（可用/待建）
 * - 可用字段列表查询
 * 
 * 需求：FR-BDA-010~016
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 关联字段库")
@RestController
@RequestMapping("/dynamicbusiness/relation-field-library")
@Validated
public class RelationFieldLibraryController {

    @Resource
    private RelationFieldLibraryService relationFieldLibraryService;

    // ========== CRUD 接口 ==========

    @PostMapping("/create")
    @Operation(summary = "创建关联字段")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:create')")
    public CommonResult<Long> createRelationField(@Valid @RequestBody RelationFieldLibraryCreateReqVO reqVO) {
        return success(relationFieldLibraryService.createRelationField(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新关联字段")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:update')")
    public CommonResult<Boolean> updateRelationField(@Valid @RequestBody RelationFieldLibraryUpdateReqVO reqVO) {
        relationFieldLibraryService.updateRelationField(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除关联字段")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:delete')")
    public CommonResult<Boolean> deleteRelationField(@RequestParam("id") Long id) {
        relationFieldLibraryService.deleteRelationField(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取关联字段详情")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<RelationFieldLibraryRespVO> getRelationField(@RequestParam("id") Long id) {
        return success(relationFieldLibraryService.getRelationField(id));
    }

    @GetMapping("/get-by-code")
    @Operation(summary = "根据字段编码获取关联字段")
    @Parameter(name = "fieldCode", description = "字段编码", required = true, example = "safety_manager")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<RelationFieldLibraryRespVO> getRelationFieldByCode(
            @RequestParam("fieldCode") String fieldCode) {
        return success(relationFieldLibraryService.getRelationFieldByCode(fieldCode));
    }

    // ========== 列表查询接口 ==========

    @GetMapping("/list")
    @Operation(summary = "获取关联字段列表（带状态）")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<List<RelationFieldLibraryWithStatusVO>> getRelationFieldList() {
        return success(relationFieldLibraryService.getRelationFieldListWithStatus());
    }

    @GetMapping("/available")
    @Operation(summary = "获取可用的关联字段列表")
    @Parameter(name = "refEntityType", description = "关联业务类型编码（可选过滤）", example = "personnel")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<List<RelationFieldLibraryRespVO>> getAvailableRelationFields(
            @RequestParam(value = "refEntityType", required = false) String refEntityType) {
        return success(relationFieldLibraryService.getAvailableRelationFields(refEntityType));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询关联字段")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<PageResult<RelationFieldLibraryWithStatusVO>> getRelationFieldPage(
            @Valid RelationFieldLibraryPageReqVO reqVO) {
        return success(relationFieldLibraryService.getRelationFieldPage(reqVO));
    }

    @GetMapping("/system")
    @Operation(summary = "获取系统预置字段列表")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<List<RelationFieldLibraryRespVO>> getSystemRelationFields() {
        return success(relationFieldLibraryService.getSystemRelationFields());
    }

    // ========== 状态检查接口 ==========

    @GetMapping("/check-target-exists")
    @Operation(summary = "检查关联目标是否存在")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true, example = "personnel")
    @Parameter(name = "modelCode", description = "Model 编码", required = true, example = "employee")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<Boolean> checkTargetExists(
            @RequestParam("entityTypeCode") String entityType,
            @RequestParam("modelCode") String modelCode) {
        return success(relationFieldLibraryService.checkTargetExists(entityType, modelCode));
    }

    @GetMapping("/target-names")
    @Operation(summary = "获取关联目标的名称信息")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true, example = "personnel")
    @Parameter(name = "modelCode", description = "Model 编码", required = true, example = "employee")
    @PreAuthorize("@ss.hasPermission('system:relation-field-library:query')")
    public CommonResult<TargetNamesVO> getTargetNames(
            @RequestParam("entityTypeCode") String entityType,
            @RequestParam("modelCode") String modelCode) {
        String[] names = relationFieldLibraryService.getTargetNames(entityType, modelCode);
        return success(new TargetNamesVO(names[0], names[1]));
    }

    /**
     * 关联目标名称信息 VO
     */
    public record TargetNamesVO(
            String entityTypeName,
            String modelName
    ) {}
}
