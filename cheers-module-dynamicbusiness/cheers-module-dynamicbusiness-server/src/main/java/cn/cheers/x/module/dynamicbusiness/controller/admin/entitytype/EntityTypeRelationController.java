package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.RelatableEntityTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * EntityType 关联 Controller
 *
 * 职责：
 * - 在业务类型（EntityType）维度管理“业务之间允许建立引用/关联”的门禁关系。
 * - 为字段-模型分配（尤其是 REFERENCE 字段）、跨业务查询与组合提供类型级关联规则依据。
 *
 * 说明：
 * - 这里只管理 EntityType 级别的“可以/不可以关联”规则；
 * - 实体和模型层面是否真的建立引用字段，由字段管理与模型配置决定
 *   （参见《字段管理与字段库-模型分配约定（权威）》）。
 */
@Tag(name = "管理后台 - EntityType 关联", description = "管理业务类型之间的关联规则（类型级门禁），用于约束和发现跨业务引用能力")
@RestController
@RequestMapping("/dynamicbusiness/entity-type-relation")
@Validated
public class EntityTypeRelationController {

    @Resource
    private EntityTypeRelationService entityTypeRelationService;

    @PostMapping("/create")
    @Operation(summary = "创建 EntityType 关联规则", description = "为源业务类型与目标业务类型之间建立允许关联的门禁规则，用于后续字段/模型配置中的引用约束")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Long> createRelation(@Valid @RequestBody EntityTypeRelationCreateReqVO reqVO) {
        Long id = entityTypeRelationService.createRelation(reqVO);
        return success(id);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 EntityType 关联规则", description = "删除一条类型级关联规则，不再允许该对业务类型新建新的引用关系")
    @Parameter(name = "id", description = "关联 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> deleteRelation(@RequestParam("id") Long id) {
        entityTypeRelationService.deleteRelation(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取 EntityType 关联详情")
    @Parameter(name = "id", description = "关联 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<EntityTypeRelationRespVO> getRelation(@RequestParam("id") Long id) {
        return success(entityTypeRelationService.getRelation(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有 EntityType 关联规则列表", description = "查看系统中已配置的所有业务类型级关联规则，用于全局分析业务关系")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<EntityTypeRelationRespVO>> getAllRelations() {
        return success(entityTypeRelationService.getAllRelations());
    }

    @GetMapping("/list-by-source")
    @Operation(summary = "获取源 EntityType 的关联规则列表", description = "查看某个源业务类型当前已允许关联的目标业务类型集合，用于设计其引用字段和业务组合")
    @Parameter(name = "sourceEntityTypeCode", description = "源业务类型编码", required = true, example = "task_management")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<EntityTypeRelationRespVO>> getRelationsBySourceCode(
            @RequestParam("sourceEntityTypeCode") String sourceEntityTypeCode) {
        return success(entityTypeRelationService.getRelationsBySourceCode(sourceEntityTypeCode));
    }

    @GetMapping("/list-by-target")
    @Operation(summary = "获取目标 EntityType 的关联规则列表", description = "查看某个目标业务类型被哪些源业务类型允许引用，用于反向分析依赖关系")
    @Parameter(name = "targetEntityTypeCode", description = "目标业务类型编码", required = true, example = "production_plan")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<EntityTypeRelationRespVO>> getRelationsByTargetCode(
            @RequestParam("targetEntityTypeCode") String targetEntityTypeCode) {
        return success(entityTypeRelationService.getRelationsByTargetCode(targetEntityTypeCode));
    }

    @GetMapping("/available-targets")
    @Operation(
            summary = "获取当前业务类型可新建关联的目标业务类型列表",
            description = "用于大模型/前端在配置引用字段或业务组合时，获取尚未建立关联的候选目标列表。必须提供 currentEntityTypeCode 或 currentEntityTypeName 其中一个。")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<RelatableEntityTypeRespVO>> getAvailableTargets(
            @RequestParam(value = "currentEntityTypeCode", required = false) String currentEntityTypeCode,
            @RequestParam(value = "currentEntityTypeName", required = false) String currentEntityTypeName) {
        return success(entityTypeRelationService.getAvailableTargets(currentEntityTypeCode, currentEntityTypeName));
    }

    @GetMapping("/exists")
    @Operation(summary = "检查 EntityType 关联规则是否存在", description = "用于在创建引用字段或业务组合前，校验两种业务类型之间是否已存在允许关联的门禁规则")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<Boolean> existsRelation(
            @RequestParam("sourceEntityTypeCode") String sourceEntityTypeCode,
            @RequestParam("targetEntityTypeCode") String targetEntityTypeCode) {
        return success(entityTypeRelationService.existsRelation(sourceEntityTypeCode, targetEntityTypeCode));
    }

    // 已删除：syncRelationsToModels 接口 - 不再在 Model 层自动展开关联
}
