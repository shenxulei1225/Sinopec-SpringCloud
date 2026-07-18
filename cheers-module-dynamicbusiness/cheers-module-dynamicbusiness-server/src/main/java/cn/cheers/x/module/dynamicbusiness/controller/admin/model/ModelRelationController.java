package cn.cheers.x.module.dynamicbusiness.controller.admin.model;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.convert.model.ModelRelationConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - Model 关联管理 Controller
 * 
 * <p>提供 Model 之间双向关联关系的管理功能，支持建立关联、删除关联、查询关联列表。</p>
 * 
 * <h3>业务场景</h3>
 * <p>当用户需要在两个 Model 之间建立关联时（如"任务"关联"计划"），
 * 系统会自动为源 Model 和目标 Model 都创建关联字段，实现双向关联。</p>
 * 
 * <h3>双向关联字段规则</h3>
 * <p><b>源 Model 的关联字段（正向关联）</b>：</p>
 * <ul>
 *   <li>字段编码：目标 Model 编码 + "_id"（如 plan_id）</li>
 *   <li>字段名称："关联" + 目标 Model 名称（如"关联计划"）</li>
 *   <li>字段类型：ENTITY_REF_MULTI（支持多选）</li>
 *   <li>可查询：true</li>
 * </ul>
 * <p><b>目标 Model 的关联字段（反向关联）</b>：</p>
 * <ul>
 *   <li>字段编码：源 Model 编码 + "_id"（如 task_id）</li>
 *   <li>字段名称："关联" + 源 Model 名称（如"关联任务"）</li>
 *   <li>字段类型：ENTITY_REF_MULTI（支持多选）</li>
 *   <li>可查询：true</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-075: 系统必须提供 Model 关联管理 API</li>
 *   <li>FR-076: 系统必须在建立关联时自动创建关联字段</li>
 *   <li>BR-REL-001: Model 关联存储在 dynamic_model_relation 表</li>
 *   <li>BR-REL-002: 关联字段编码规则</li>
 *   <li>BR-REL-003: 关联字段自动设置为可查询</li>
 * </ul>
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - Model 关联管理", description = "提供 Model 之间关联关系的管理功能，支持建立关联、删除关联、查询关联列表")
@RestController
@RequestMapping("/dynamicbusiness/model/{modelCode}/relations")
@Validated
public class ModelRelationController {

    @Resource
    private ModelRelationService modelRelationService;

    @Resource
    private ModelMapper modelMapper;

    @PostMapping
    @Operation(
        summary = "建立 Model 关联",
        description = """
            在源 Model 和目标 Model 之间建立双向关联关系。

            **自动创建双向关联字段**：
            1. **源 Model 的关联字段**（正向关联）：
               - 字段编码：目标 Model 编码 + "_id"（如 plan_id）
               - 字段名称："关联" + 目标 Model 名称（如"关联计划"）
               - 字段类型：ENTITY_REF_MULTI（支持多选）
               - 字段自动设置为可查询（is_searchable=true）
            2. **目标 Model 的关联字段**（反向关联）：
               - 字段编码：源 Model 编码 + "_id"（如 task_id）
               - 字段名称："关联" + 源 Model 名称（如"关联任务"）
               - 字段类型：ENTITY_REF_MULTI（支持多选）
               - 字段自动设置为可查询（is_searchable=true）

            **注意事项**：
            - 如果关联已存在，返回已存在的关联 ID
            - 不支持自引用关联（源 Model 和目标 Model 相同）
            - 双向关联字段创建失败不会影响关联关系的创建，仅记录日志
            """
    )
    @Parameter(name = "modelCode", description = "源 Model 编码", required = true, example = "task")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Long> createRelation(
            @PathVariable("modelCode") String modelCode,
            @Valid @RequestBody ModelRelationCreateReqVO reqVO) {
        Long relationId = modelRelationService.createRelation(
                modelCode,
                reqVO.getTargetModelCode(),
                null,
                false, // 手动创建
                null   // 无 EntityType 关联
        );
        return success(relationId);
    }

    @DeleteMapping("/{targetModelCode}")
    @Operation(
        summary = "删除 Model 关联",
        description = "删除源 Model 和目标 Model 之间的双向关联关系。\n\n" +
            "**级联删除**：\n" +
            "- 删除关联时，会自动级联删除相关的字段分配记录（ModelFieldAssignment）\n" +
            "- 关联字段定义（Field）不会被删除，以保护已有数据\n" +
            "- 双向关联的两个方向的字段分配都会被删除\n\n" +
            "**注意事项**：\n" +
            "- 如果需要删除关联字段定义，请使用字段管理 API"
    )
    @Parameter(name = "modelCode", description = "源 Model 编码", required = true, example = "task")
    @Parameter(name = "targetModelCode", description = "目标 Model 编码", required = true, example = "plan")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<Boolean> deleteRelation(
            @PathVariable("modelCode") String modelCode,
            @PathVariable("targetModelCode") String targetModelCode) {
        modelRelationService.deleteRelation(modelCode, targetModelCode);
        return success(true);
    }

    @GetMapping
    @Operation(
        summary = "获取 Model 关联列表",
        description = "获取指定 Model 的所有关联列表（包括正向关联和反向关联）。\n\n" +
            "**正向关联**：从当前 Model 出发的关联（当前 Model 是源）\n" +
            "**反向关联**：指向当前 Model 的关联（当前 Model 是目标）"
    )
    @Parameter(name = "modelCode", description = "Model 编码", required = true, example = "task")
    @Parameter(name = "direction", description = "关联方向：source-正向关联，target-反向关联，all-全部（默认）", example = "all")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<List<ModelRelationRespVO>> getRelations(
            @PathVariable("modelCode") String modelCode,
            @RequestParam(value = "direction", defaultValue = "all") String direction) {
        
        List<ModelRelationDO> relations;
        switch (direction.toLowerCase()) {
            case "source":
                relations = modelRelationService.getRelationsBySourceModelCode(modelCode);
                break;
            case "target":
                relations = modelRelationService.getRelationsByTargetModelCode(modelCode);
                break;
            default:
                relations = modelRelationService.getAllRelationsByModelCode(modelCode);
        }

        // 转换为 VO 并填充 Model 名称
        List<ModelRelationRespVO> respList = ModelRelationConvert.INSTANCE.convertList(relations);
        fillModelNames(respList);
        
        return success(respList);
    }

    @GetMapping("/{targetModelCode}")
    @Operation(
        summary = "获取指定的 Model 关联",
        description = "获取源 Model 和目标 Model 之间的关联详情。"
    )
    @Parameter(name = "modelCode", description = "源 Model 编码", required = true, example = "task")
    @Parameter(name = "targetModelCode", description = "目标 Model 编码", required = true, example = "plan")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<ModelRelationRespVO> getRelation(
            @PathVariable("modelCode") String modelCode,
            @PathVariable("targetModelCode") String targetModelCode) {
        ModelRelationDO relation = modelRelationService.getRelation(modelCode, targetModelCode);
        if (relation == null) {
            return success(null);
        }
        
        ModelRelationRespVO respVO = ModelRelationConvert.INSTANCE.convert(relation);
        fillModelName(respVO);
        
        return success(respVO);
    }

    @GetMapping("/exists/{targetModelCode}")
    @Operation(
        summary = "检查 Model 关联是否存在",
        description = "检查源 Model 和目标 Model 之间是否存在关联关系。"
    )
    @Parameter(name = "modelCode", description = "源 Model 编码", required = true, example = "task")
    @Parameter(name = "targetModelCode", description = "目标 Model 编码", required = true, example = "plan")
    @PreAuthorize("@ss.hasPermission('system:model:query')")
    public CommonResult<Boolean> existsRelation(
            @PathVariable("modelCode") String modelCode,
            @PathVariable("targetModelCode") String targetModelCode) {
        return success(modelRelationService.existsRelation(modelCode, targetModelCode));
    }

    // ========== 私有方法 ==========

    /**
     * 填充 Model 名称
     */
    private void fillModelNames(List<ModelRelationRespVO> respList) {
        if (respList == null || respList.isEmpty()) {
            return;
        }

        // 收集所有 Model 编码
        List<String> modelCodes = respList.stream()
                .flatMap(r -> List.of(r.getSourceModelCode(), r.getTargetModelCode()).stream())
                .distinct()
                .collect(Collectors.toList());

        // 批量查询 Model
        Map<String, String> modelNameMap = modelCodes.stream()
                .map(modelMapper::selectByCode)
                .filter(m -> m != null)
                .collect(Collectors.toMap(ModelDO::getCode, ModelDO::getName));

        // 填充名称
        for (ModelRelationRespVO respVO : respList) {
            respVO.setSourceModelName(modelNameMap.get(respVO.getSourceModelCode()));
            respVO.setTargetModelName(modelNameMap.get(respVO.getTargetModelCode()));
        }
    }

    /**
     * 填充单个 Model 名称
     */
    private void fillModelName(ModelRelationRespVO respVO) {
        if (respVO == null) {
            return;
        }

        ModelDO sourceModel = modelMapper.selectByCode(respVO.getSourceModelCode());
        if (sourceModel != null) {
            respVO.setSourceModelName(sourceModel.getName());
        }

        ModelDO targetModel = modelMapper.selectByCode(respVO.getTargetModelCode());
        if (targetModel != null) {
            respVO.setTargetModelName(targetModel.getName());
        }
    }
}
