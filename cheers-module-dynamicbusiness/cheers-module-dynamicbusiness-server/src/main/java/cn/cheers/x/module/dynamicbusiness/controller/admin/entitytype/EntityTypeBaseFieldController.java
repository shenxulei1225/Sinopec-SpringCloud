package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldBatchSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypePlatformFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 业务类型固定列字段 Controller
 *
 * 职责：
 * - 在 EntityType 维度维护一组固定列字段（基于字段库定义），作为该业务类型下各模型的“基础字段候选集/基线”。
 * - 为字段-模型分配提供类型级的字段约束与推荐来源。
 *
 * 说明：
 * - 固定列字段本身不会直接决定某个 Model 的最终字段集合；
 * - 某个 Model 是否采用某个固定列字段，由“字段-模型分配”机制决定
 *   （参见《字段管理与字段库-模型分配约定（权威）》）。
 */
@Tag(name = "管理后台 - 业务类型固定列字段", description = "在业务类型维度维护固定列字段候选集，用于约定该类型下各模型的基础字段基线")
@RestController
@RequestMapping("/dynamicbusiness/entity-type-base-field")
@Validated
public class EntityTypeBaseFieldController {

    /** 支持的数据类型 */
    private static final List<String> SUPPORTED_DATA_TYPES = Arrays.asList(
            "TEXT", "NUMBER", "DATE", "DATETIME", "BOOLEAN", "ENUM", "REFERENCE"
    );

    @Resource
    private EntityTypeBaseFieldService entityTypeBaseFieldService;

    @PostMapping("/create")
    @Operation(summary = "创建业务类型固定列字段", description = "须从字段库选择 libraryFieldId；编码/名称/类型以字段库为准，不可手填。创建后自动关联该业务类型下全部模型。")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:create')")
    public CommonResult<Long> createBaseField(@Valid @RequestBody EntityTypeBaseFieldSaveReqVO reqVO) {
        return success(entityTypeBaseFieldService.createBaseField(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新业务类型固定列字段", description = "可调整显示别名、必填、默认值、排序、状态等；编码/类型以字段库为准。")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:update')")
    public CommonResult<Boolean> updateBaseField(@Valid @RequestBody EntityTypeBaseFieldSaveReqVO reqVO) {
        entityTypeBaseFieldService.updateBaseField(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除业务类型固定列字段", description = "从 EntityType 的固定列候选集中移除某个字段，不会直接删除已有模型中的字段，仅影响后续分配")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:delete')")
    public CommonResult<Boolean> deleteBaseField(@RequestParam("id") Long id) {
        entityTypeBaseFieldService.deleteBaseField(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取业务类型固定列字段详情")
    @Parameter(name = "id", description = "字段ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:query')")
    public CommonResult<EntityTypeBaseFieldRespVO> getBaseField(@RequestParam("id") Long id) {
        return success(entityTypeBaseFieldService.getBaseFieldRespVO(id));
    }

    @GetMapping("/list")
    @Operation(summary = "根据业务类型编码获取固定列字段列表", description = "查看某个 EntityType 当前配置的固定列字段候选集，用于了解该类型下模型的基础字段基线")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:query')")
    public CommonResult<List<EntityTypeBaseFieldRespVO>> listByEntityTypeCode(
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityTypeBaseFieldService.listByEntityTypeCode(entityTypeCode));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新固定列字段状态", description = "启用/停用某个 EntityType 级固定列字段，影响其在后续模型分配中的可用性")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:update')")
    public CommonResult<Boolean> updateBaseFieldStatus(@RequestParam("id") Long id,
                                                        @RequestParam("status") Integer status) {
        entityTypeBaseFieldService.updateBaseFieldStatus(id, status);
        return success(true);
    }

    @GetMapping("/exists")
    @Operation(summary = "检查字段编码在该业务类型固定列中是否已存在")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:query')")
    public CommonResult<Boolean> existsFieldCode(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("fieldCode") String fieldCode) {
        return success(entityTypeBaseFieldService.existsFieldCode(entityTypeCode, fieldCode));
    }

    @GetMapping("/count")
    @Operation(summary = "统计业务类型下固定列字段数量")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:query')")
    public CommonResult<Long> countByEntityTypeCode(
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityTypeBaseFieldService.countByEntityTypeCode(entityTypeCode));
    }

    @GetMapping("/field-codes")
    @Operation(summary = "获取业务类型固定列字段编码列表")
    @Parameter(name = "entityTypeCode", description = "业务类型编码", required = true, example = "equipment")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:query')")
    public CommonResult<List<String>> getFieldCodes(
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityTypeBaseFieldService.getFieldCodes(entityTypeCode));
    }

    @GetMapping("/data-types")
    @Operation(summary = "获取支持的数据类型列表", description = "为字段库与固定列字段配置提供可选数据类型说明")
    public CommonResult<List<DataTypeOption>> getDataTypes() {
        List<DataTypeOption> options = Arrays.asList(
                new DataTypeOption("TEXT", "文本", "字符串类型，支持任意文本"),
                new DataTypeOption("NUMBER", "数字", "数值类型，支持整数和小数"),
                new DataTypeOption("DATE", "日期", "日期类型，格式：yyyy-MM-dd"),
                new DataTypeOption("DATETIME", "日期时间", "日期时间类型，格式：yyyy-MM-ddTHH:mm:ss"),
                new DataTypeOption("BOOLEAN", "布尔", "布尔类型，true/false"),
                new DataTypeOption("ENUM", "枚举", "枚举类型，需要配置选项列表"),
                new DataTypeOption("REFERENCE", "引用", "引用类型，关联其他模型")
        );
        return success(options);
    }

    @PostMapping("/validate")
    @Operation(summary = "验证字段值", description = "在为某个业务类型配置或生成数据前，校验给定字段值是否符合固定列字段的定义与约束")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:query')")
    public CommonResult<String> validateFieldValue(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("fieldCode") String fieldCode,
            @RequestParam(value = "value", required = false) String value) {
        String error = entityTypeBaseFieldService.validateFieldValue(entityTypeCode, fieldCode, value);
        return success(error);
    }

    @GetMapping("/platform-fields")
    @Operation(summary = "获取实体通用列（系统字段）", description = "返回名称、状态等平台自带字段，不可删除，可设置业务别名")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:query')")
    public CommonResult<List<EntityTypePlatformFieldRespVO>> listPlatformFields(
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityTypeBaseFieldService.listPlatformFields(entityTypeCode));
    }

    @PutMapping("/platform-field-label")
    @Operation(summary = "更新系统字段显示别名", description = "按业务类型设置名称、状态等实体通用列的界面文案")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:update')")
    public CommonResult<Boolean> updatePlatformFieldLabel(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam("fieldCode") String fieldCode,
            @RequestParam("label") String label) {
        entityTypeBaseFieldService.updatePlatformFieldLabel(entityTypeCode, fieldCode, label);
        return success(true);
    }

    @PutMapping("/save-batch")
    @Operation(summary = "批量保存业务类型基础字段", description = "一次提交新增/更新/删除与系统字段别名；结束时统一刷新能力投影")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:update')")
    public CommonResult<Boolean> saveBaseFieldBatch(@Valid @RequestBody EntityTypeBaseFieldBatchSaveReqVO reqVO) {
        entityTypeBaseFieldService.saveBaseFieldBatch(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete-by-assignment")
    @Operation(summary = "按模型分配删除基础字段", description = "优先删除注册记录；无注册记录时仍从该类型下全部模型移除")
    @PreAuthorize("@ss.hasPermission('system:entity-type-base-field:delete')")
    public CommonResult<Boolean> deleteByAssignment(
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam(value = "libraryFieldId", required = false) Long libraryFieldId,
            @RequestParam(value = "fieldCode", required = false) String fieldCode) {
        entityTypeBaseFieldService.deleteBaseFieldByAssignment(entityTypeCode, libraryFieldId, fieldCode);
        return success(true);
    }

    /**
     * 数据类型选项
     */
    public record DataTypeOption(
            String code,
            String name,
            String description
    ) {}
}
