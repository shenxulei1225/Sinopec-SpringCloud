package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.*;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogEntryAccessService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import cn.cheers.x.framework.common.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.*;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 实体关联关系 Controller
 * <p>
 * 权限：关联显示在详情页的关联块里，按本页数据目录（registryCode）上的「详情」授权核对；
 * 另一端是别的目录时还要那本目录的实体查看授权；传了编号的两端记录都要属于各自的目录。
 * 按一端列出的关联列表，暂不按另一端的授权逐条过滤（数据范围随授权分配一起做）。
 * 
 * 用于管理业务实体之间的关联关系，支持：
 * - 一对一关系（ONE_TO_ONE）
 * - 一对多关系（ONE_TO_MANY）
 * - 多对多关系（MANY_TO_MANY）
 */
@Tag(name = "管理后台 - 实体关联关系管理", description = "提供实体关联关系的创建、更新、删除、查询等功能")
@RestController
@RequestMapping("/dynamicbusiness/business/entity-relations")
@Validated
public class EntityRelationController {

    @Resource
    private EntityRelationService entityRelationService;
    /** 关联两端的记录是否属于各自的数据目录。 */
    @Resource
    private CatalogEntryAccessService catalogEntryAccessService;

    @PostMapping("/create")
    @Operation(
        summary = "创建实体关联关系",
        description = """
            创建两个实体之间的关联关系。
            - 支持一对一、一对多、多对多三种关联类型
            - 不允许实体与自身建立关联关系
            - 同一对实体之间不能重复建立关联关系
            """
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@catalogAccess.allowRelation(#reqVO.registryCode, #reqVO.sourceEntityTypeCode, #reqVO.targetEntityTypeCode, 'create')")
    public CommonResult<Long> createRelation(@Valid @RequestBody EntityRelationCreateReqVO reqVO) {
        assertEnds(reqVO.getRegistryCode(), reqVO.getSourceEntityTypeCode(), reqVO.getSourceEntityId(),
                reqVO.getTargetEntityTypeCode(), reqVO.getTargetEntityId());
        return success(entityRelationService.createRelation(reqVO));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新实体关联关系",
        description = "更新关联关系的属性，如关联类型、关联名称、描述等"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@catalogAccess.allowRelation(#reqVO.registryCode, #reqVO.sourceEntityTypeCode, #reqVO.targetEntityTypeCode, 'update')")
    public CommonResult<Boolean> updateRelation(@Valid @RequestBody EntityRelationUpdateReqVO reqVO) {
        assertStoredRelationEnds(reqVO.getRegistryCode(), reqVO.getId(),
                reqVO.getSourceEntityTypeCode(), reqVO.getTargetEntityTypeCode());
        entityRelationService.updateRelation(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除实体关联关系",
        description = "删除指定的关联关系。\n" +
            "- sourceEntityTypeCode 和 targetEntityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "id", description = "关联关系ID", required = true, example = "1")
    @Parameter(name = "sourceEntityTypeCode", description = "源实体业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "targetEntityTypeCode", description = "目标实体业务类型编码（必填）", required = true, example = "task")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #sourceEntityTypeCode, #targetEntityTypeCode, 'delete')")
    public CommonResult<Boolean> deleteRelation(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("id") Long id,
            @RequestParam("sourceEntityTypeCode") String sourceEntityTypeCode,
            @RequestParam("targetEntityTypeCode") String targetEntityTypeCode) {
        assertStoredRelationEnds(registryCode, id, sourceEntityTypeCode, targetEntityTypeCode);
        entityRelationService.deleteRelation(id, sourceEntityTypeCode, targetEntityTypeCode);
        return success(true);
    }

    @GetMapping("/get-by-id")
    @Operation(
        summary = "获取关联关系详情",
        description = "根据关联关系ID获取详细信息。\n" +
            "- sourceEntityTypeCode 和 targetEntityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "id", description = "关联关系ID", required = true, example = "1")
    @Parameter(name = "sourceEntityTypeCode", description = "源实体业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "targetEntityTypeCode", description = "目标实体业务类型编码（必填）", required = true, example = "task")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #sourceEntityTypeCode, #targetEntityTypeCode, 'query')")
    public CommonResult<EntityRelationRespVO> getRelation(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("id") Long id,
            @RequestParam("sourceEntityTypeCode") String sourceEntityTypeCode,
            @RequestParam("targetEntityTypeCode") String targetEntityTypeCode) {
        assertStoredRelationEnds(registryCode, id, sourceEntityTypeCode, targetEntityTypeCode);
        return success(entityRelationService.getRelation(id, sourceEntityTypeCode, targetEntityTypeCode));
    }

    @GetMapping("/list-by-source-entity")
    @Operation(
        summary = "获取源实体的关联关系列表",
        description = "获取指定实体作为源实体的所有关联关系。\n" +
            "- entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "sourceEntityId", description = "源实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "源实体业务类型编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'query')")
    public CommonResult<List<EntityRelationRespVO>> listBySourceEntity(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("sourceEntityId") Long sourceEntityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(sourceEntityId));
        return success(entityRelationService.getRelationsBySourceEntity(sourceEntityId, entityTypeCode));
    }

    @GetMapping("/list-by-target-entity")
    @Operation(
        summary = "获取目标实体的关联关系列表",
        description = "获取指定实体作为目标实体的所有关联关系。\n" +
            "- entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "targetEntityId", description = "目标实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "目标实体业务类型编码（必填）", required = true, example = "task")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'query')")
    public CommonResult<List<EntityRelationRespVO>> listByTargetEntity(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("targetEntityId") Long targetEntityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(targetEntityId));
        return success(entityRelationService.getRelationsByTargetEntity(targetEntityId, entityTypeCode));
    }

    @GetMapping("/list-by-entity")
    @Operation(
        summary = "获取实体的所有关联关系",
        description = "获取指定实体的所有关联关系（作为源或目标）。\n" +
            "- entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "实体业务类型编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'query')")
    public CommonResult<List<EntityRelationRespVO>> listByEntity(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(entityRelationService.getAllRelationsByEntity(entityId, entityTypeCode));
    }

    @GetMapping("/list-related-by-source-or-target")
    @Operation(
        summary = "获取关联实体列表",
        description = """
            获取与指定实体关联的所有实体，可按关联类型过滤。
            支持两种查询方式：
            1. 通过源实体查询：传入 sourceEntityId 和 sourceEntityTypeCode
            2. 通过目标实体查询：传入 targetEntityId 和 targetEntityTypeCode
            """
    )
    @Parameter(name = "sourceEntityId", description = "源实体ID（与sourceEntityTypeCode一起使用）", required = false, example = "1")
    @Parameter(name = "sourceEntityTypeCode", description = "源实体业务类型编码（与sourceEntityId一起使用）", required = false, example = "equipment")
    @Parameter(name = "targetEntityId", description = "目标实体ID（与targetEntityTypeCode一起使用）", required = false, example = "2")
    @Parameter(name = "targetEntityTypeCode", description = "目标实体业务类型编码（与targetEntityId一起使用）", required = false, example = "task")
    @Parameter(name = "relationType", description = "关联类型（可选，ONE_TO_ONE/ONE_TO_MANY/MANY_TO_MANY）", example = "ONE_TO_MANY")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #sourceEntityTypeCode, #targetEntityTypeCode, 'query')")
    public CommonResult<List<EntityRelationRespVO>> getRelatedEntities(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam(value = "sourceEntityId", required = false) Long sourceEntityId,
            @RequestParam(value = "sourceEntityTypeCode", required = false) String sourceEntityTypeCode,
            @RequestParam(value = "targetEntityId", required = false) Long targetEntityId,
            @RequestParam(value = "targetEntityTypeCode", required = false) String targetEntityTypeCode,
            @RequestParam(value = "relationType", required = false) String relationType) {
        assertEnds(registryCode, sourceEntityTypeCode, sourceEntityId, targetEntityTypeCode, targetEntityId);
        return success(entityRelationService.getRelatedEntities(sourceEntityId, sourceEntityTypeCode, 
                targetEntityId, targetEntityTypeCode, relationType));
    }

    @GetMapping("/exists-relations-by-entity")
    @Operation(
        summary = "检查实体是否存在关联关系",
        description = "检查指定实体是否存在任何关联关系。\n" +
            "- entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "实体业务类型编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'query')")
    public CommonResult<Boolean> hasRelations(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(entityRelationService.hasRelations(entityId, entityTypeCode));
    }

    @GetMapping("/count-by-entity")
    @Operation(
        summary = "统计实体的关联关系数量",
        description = "统计指定实体的关联关系总数。\n" +
            "- entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "实体业务类型编码（必填）", required = true, example = "equipment")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'query')")
    public CommonResult<Long> countRelations(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(entityRelationService.countRelations(entityId, entityTypeCode));
    }

    @DeleteMapping("/delete-relations-by-entity")
    @Operation(
        summary = "批量删除实体的所有关联关系",
        description = "删除指定实体的所有关联关系（作为源或目标）。\n" +
            "- entityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "entityId", description = "实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "实体业务类型编码（必填）", required = true, example = "equipment")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'delete')")
    public CommonResult<Integer> deleteAllByEntity(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(entityId));
        return success(entityRelationService.deleteAllRelationsByEntity(entityId, entityTypeCode));
    }

    @GetMapping("/exists-by-source-and-target")
    @Operation(
        summary = "检查两个实体之间是否存在关联关系",
        description = "检查源实体和目标实体之间是否已建立关联关系。\n" +
            "- sourceEntityTypeCode 和 targetEntityTypeCode 是必填参数，用于路由到正确的存储策略"
    )
    @Parameter(name = "sourceEntityId", description = "源实体ID", required = true, example = "1")
    @Parameter(name = "sourceEntityTypeCode", description = "源实体业务类型编码（必填）", required = true, example = "equipment")
    @Parameter(name = "targetEntityId", description = "目标实体ID", required = true, example = "2")
    @Parameter(name = "targetEntityTypeCode", description = "目标实体业务类型编码（必填）", required = true, example = "task")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #sourceEntityTypeCode, #targetEntityTypeCode, 'query')")
    public CommonResult<Boolean> existsRelation(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("sourceEntityId") Long sourceEntityId,
            @RequestParam("sourceEntityTypeCode") String sourceEntityTypeCode,
            @RequestParam("targetEntityId") Long targetEntityId,
            @RequestParam("targetEntityTypeCode") String targetEntityTypeCode) {
        assertEnds(registryCode, sourceEntityTypeCode, sourceEntityId, targetEntityTypeCode, targetEntityId);
        return success(entityRelationService.existsRelation(sourceEntityId, sourceEntityTypeCode, 
                targetEntityId, targetEntityTypeCode));
    }

    @GetMapping("/list-by-target-with-source-model")
    @Operation(
        summary = "按目标实体和源模型编码查询关联关系",
        description = """
            适用场景：在目标实体详情页中，仅查看来自某一类源模型的关联关系（例如“只看来自巡检任务模型的关联”）。
            业务范围：指定业务（必须传 entityTypeCode）。
            说明：sourceModelCode 用于进一步收敛查询范围，避免同业务下多模型混查。
            """
    )
    @Parameter(name = "targetEntityId", description = "目标实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "目标实体业务类型编码（必填）", required = true, example = "task")
    @Parameter(name = "sourceModelCode", description = "源模型编码（必填）", required = true, example = "inspect_task_model")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'query')")
    public CommonResult<List<EntityRelationRespVO>> listByTargetEntityAndSourceModelCode(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("targetEntityId") Long targetEntityId,
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("sourceModelCode") String sourceModelCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(targetEntityId));
        return success(entityRelationService.getRelationsByTargetEntity(targetEntityId, entityTypeCode, sourceModelCode));
    }

    @GetMapping("/count-by-target")
    @Operation(
        summary = "统计目标实体被关联次数",
        description = """
            适用场景：在目标实体列表或详情中展示“被引用次数/被关联次数”。
            业务范围：指定业务（必须传 entityTypeCode）。
            说明：只统计目标实体维度，不区分具体源实体。
            """
    )
    @Parameter(name = "targetEntityId", description = "目标实体ID", required = true, example = "1")
    @Parameter(name = "entityTypeCode", description = "目标实体业务类型编码（必填）", required = true, example = "task")
    @PreAuthorize("@catalogAccess.allowRelation(#registryCode, #entityTypeCode, null, 'query')")
    public CommonResult<Long> countRelationsByTargetEntity(
            @RequestParam(value = "registryCode", required = false) String registryCode,
            @RequestParam("targetEntityId") Long targetEntityId,
            @RequestParam("entityTypeCode") String entityTypeCode) {
        catalogEntryAccessService.assertRecordsInTarget(registryCode, entityTypeCode, List.of(targetEntityId));
        return success(entityRelationService.countRelationsByTargetEntity(targetEntityId, entityTypeCode));
    }

    /** 关联的两端：传了编号的一端，记录必须属于它的数据目录（子数据类型看业务域，划分数据看成员）。 */
    private void assertEnds(String registryCode, String sourceTypeCode, Long sourceId,
                            String targetTypeCode, Long targetId) {
        if (sourceId != null) {
            catalogEntryAccessService.assertRecordsInTarget(registryCode, sourceTypeCode, List.of(sourceId));
        }
        if (targetId != null) {
            catalogEntryAccessService.assertRecordsInTarget(registryCode, targetTypeCode, List.of(targetId));
        }
    }

    /** 按编号改、删、看已有关联：先读出这条关联，再核对两端记录。关联不存在由服务报错。 */
    private void assertStoredRelationEnds(String registryCode, Long relationId,
                                          String sourceTypeCode, String targetTypeCode) {
        EntityRelationRespVO stored = entityRelationService.getRelation(relationId, sourceTypeCode, targetTypeCode);
        if (stored == null) {
            throw new ServiceException(404, "关联关系不存在");
        }
        assertEnds(registryCode, sourceTypeCode, stored.getSourceEntityId(), targetTypeCode, stored.getTargetEntityId());
    }
}
