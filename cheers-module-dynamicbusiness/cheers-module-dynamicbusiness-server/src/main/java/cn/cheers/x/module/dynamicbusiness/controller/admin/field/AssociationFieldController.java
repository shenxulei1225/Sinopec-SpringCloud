package cn.cheers.x.module.dynamicbusiness.controller.admin.field;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.AssociationTargetRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 业务关联字段(REF_Multi)接口
 *
 * 用途：
 * - 供“业务关联设置”界面查询系统中可被关联的业务模块
 * - 管理所有类型为 REF_Multi 的字段(新增/查询/删除/列表)
 *
 * 说明：REF_Multi 字段存储在标准字段库(dynamic_field)中；可关联目标列表来自已注册的业务类型（不依赖关联字段库）。
 */
@Tag(name = "管理后台 - 业务关联字段(REF_Multi)")
@RestController
@RequestMapping("/dynamicbusiness/association-field")
@Validated
public class AssociationFieldController {

    public static final String FIELD_TYPE_REF_MULTI = "REF_Multi";

    @Resource
    private FieldService fieldService;
    @Resource
    private EntityTypeService entityTypeService;

    @PostMapping("/create")
    @Operation(summary = "新增业务关联字段(REF_Multi)")
    @PreAuthorize("@ss.hasPermission('system:field:create')")
    public CommonResult<Long> create(@Valid @RequestBody FieldCreateReqVO reqVO) {
        // 固定为 REF_Multi
        reqVO.setType(FIELD_TYPE_REF_MULTI);
        return success(fieldService.createField(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取业务关联字段详情(REF_Multi)")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<FieldRespVO> get(@RequestParam("id") Long id) {
        return success(fieldService.getField(id));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务关联字段(REF_Multi)")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:field:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        fieldService.deleteField(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有业务关联字段列表(仅 REF_Multi)")
    @Parameter(name = "keyword", description = "关键词(可选,匹配字段名称或描述)", example = "关联设备")
    @Parameter(name = "source", description = "来源(可选,如 SYSTEM/USER)", example = "SYSTEM")
    @Parameter(name = "status", description = "状态(可选,1启用/0禁用)", example = "1")
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<List<FieldRespVO>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "status", required = false) Integer status) {
        return success(fieldService.search(keyword, FIELD_TYPE_REF_MULTI, source, status));
    }

    @GetMapping("/available-targets")
    @Operation(summary = "获取可关联的业务模块列表（按业务类型，不依赖关联字段库）")
    @Parameter(name = "excludeEntityTypeCode", description = "排除的业务模块编码(可选,通常为当前模块编码)", example = "task")
    @Parameter(name = "status", description = "保留参数，当前未使用", example = "1")
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<AssociationTargetRespVO>> getAvailableTargets(
            @RequestParam(value = "excludeEntityTypeCode", required = false) String excludeEntityTypeCode,
            @RequestParam(value = "status", required = false) Integer status) {
        List<EntityTypeRespVO> allTypes = entityTypeService.listAll();
        List<AssociationTargetRespVO> result = new ArrayList<>();
        for (EntityTypeRespVO type : allTypes) {
            if (type.getCode() == null) {
                continue;
            }
            if (excludeEntityTypeCode != null && excludeEntityTypeCode.equals(type.getCode())) {
                continue;
            }
            result.add(AssociationTargetRespVO.builder()
                    .entityTypeCode(type.getCode())
                    .entityTypeName(type.getName())
                    .fieldId(null)
                    .fieldName(null)
                    .fieldCode(null)
                    .status(1)
                    .build());
        }
        return success(result);
    }
}
