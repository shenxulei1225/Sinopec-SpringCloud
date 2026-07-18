package cn.cheers.x.module.dynamicbusiness.controller.admin.field;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldStatusUpdateReqVO;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 字段管理 Controller
 * 
 * Field(字段)是系统中可复用的字段定义，包含字段基础信息(名称、类型、单位编码等)。
 * 字段中的单位仅引用单位库（dynamic_unit）中的单位编码，不在字段模块内维护单位主数据。
 * 字段定义只包含基础规则(数据类型、格式)，业务规则在字段分配到 Model 时配置。
 * 字段可以被多个 Model 复用，实现字段定义的统一管理。
 * 
 * 
 * @author yudao
 */
@Tag(name = "管理后台 - 字段管理", description = "提供字段的创建、更新、删除、查询等功能。字段是系统中可复用的字段定义，可以被多个模型使用")
@RestController
@RequestMapping("/dynamicbusiness/field")
@Validated
public class FieldController {

    @Resource
    private FieldService fieldService;

    @PostMapping("/create")
    @Operation(
        summary = "创建字段",
        description = """
            创建一个新的字段定义。
            - 字段定义包含基础信息(名称、类型、单位编码等)
            - 单位编码来源于单位库（dynamic_unit）
            - 字段可以被多个 Model 复用
            - 字段创建后，需要通过 ModelFieldAssignmentController 分配给 Model 才能使用
            - 智能默认可查询：根据字段类型自动设置 isSearchable 和 isSortable
              - 常用类型(TEXT、NUMBER、INTEGER、DATE、DATETIME、BOOLEAN、ENUM、ENTITY_REF)默认可查询
              - 大文本类型(LONG_TEXT)默认不可查询，用户可手动开启
              - 用户可显式传入 isSearchable/isSortable 覆盖默认值
            """
    )
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:field:create')")
    public CommonResult<Long> createField(@Valid @RequestBody FieldCreateReqVO reqVO) {
        return success(fieldService.createField(reqVO));
    }

    @PutMapping("/update")
    @Operation(
        summary = "更新字段",
        description = "更新字段定义的基本信息。\n" +
            "- 更新会影响所有使用该字段的 Model\n" +
            "- 更新不会影响已创建的实体数据"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:field:update')")
    public CommonResult<Boolean> updateField(@Valid @RequestBody FieldUpdateReqVO reqVO) {
        fieldService.updateField(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "删除字段",
        description = "删除字段定义。\n" +
            "- 删除前会检查是否有 Model 使用该字段，如果存在则禁止删除\n" +
            "- 需要先解除所有 Model 与该字段的关联关系才能删除"
    )
    @Parameter(name = "id", description = "字段编号", required = true, example = "1")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:field:delete')")
    public CommonResult<Boolean> deleteField(@RequestParam("id") Long id) {
        fieldService.deleteField(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(
        summary = "获取字段详情",
        description = "根据字段ID获取详细信息，包含：\n" +
            "- 基本信息(名称、类型、单位编码、描述等)\n" +
            "- 字段的基础规则(数据类型、格式等)\n" +
            "- 如需单位详情，请通过单位管理接口(system/unit)查询"
    )
    @Parameter(name = "id", description = "字段编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<FieldRespVO> getField(@RequestParam("id") Long id) {
        return success(fieldService.getField(id));
    }

    @GetMapping("/search")
    @Operation(
        summary = "搜索字段(不分页)",
        description = "根据关键词搜索字段。\n" +
            "- 关键词会匹配字段名称(模糊搜索)\n" +
            "- 返回所有匹配的字段，不进行分页\n" +
            "- 适用于字段选择器等场景"
    )
    @Parameter(name = "keyword", description = "关键词(必填，模糊匹配字段名称)", required = true, example = "设备名称")
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<List<FieldRespVO>> searchFields(@RequestParam("keyword") String keyword) {
        return success(fieldService.search(keyword, null, null, null));
    }

    @GetMapping("/list")
    @Operation(
        summary = "字段全量列表(不分页)",
        description = "返回字段库全量列表，不分页。\n" +
            "- 适用于字段库、字段池等需要一次加载全部字段的场景\n" +
            "- 支持按类型、来源、状态筛选"
    )
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<List<FieldRespVO>> listFields(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "status", required = false) Integer status) {
        return success(fieldService.listAll(type, source, status));
    }

    @GetMapping("/page")
    @Operation(
        summary = "分页查询字段列表",
        description = "支持按业务类型编码、关键词、状态进行分页查询。\n" +
            "- 关键词会匹配字段名称和描述(模糊查询)\n" +
            "- 返回结果包含字段的基本信息和统计信息"
    )
    @PreAuthorize("@ss.hasPermission('system:field:query')")
    public CommonResult<PageResult<FieldRespVO>> pageField(@Valid FieldPageReqVO reqVO) {
        return success(fieldService.page(reqVO));
    }

    @PutMapping("/update-status")
    @Operation(
        summary = "更新字段状态",
        description = "启用或禁用字段。\n" +
            "- 禁用的字段在分配时会被过滤\n" +
            "- 已分配的字段状态更新不会影响已创建的实体"
    )
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:field:update')")
    public CommonResult<Boolean> updateFieldStatus(@Valid @RequestBody FieldStatusUpdateReqVO reqVO) {
        if (reqVO.getStatus() == 1) {
            fieldService.enable(reqVO.getId());
        } else {
            fieldService.disable(reqVO.getId());
        }
        return success(true);
    }

}
