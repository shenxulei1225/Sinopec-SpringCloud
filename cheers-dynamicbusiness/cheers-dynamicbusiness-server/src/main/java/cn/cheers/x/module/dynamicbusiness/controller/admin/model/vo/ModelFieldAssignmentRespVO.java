package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型字段分配响应 VO
 * 
 * 支持三种字段来源：
 * - BASE：固定列字段，来自业务类型配置，自动继承到该业务类型下的所有 Model
 * - CUSTOM：扩展字段，用户通过 ModelFieldAssignment 添加
 * - RELATION：关联字段，引用其他业务实体
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 模型字段分配响应")
@Data
public class ModelFieldAssignmentRespVO {

    @Schema(description = "字段信息")
    private FieldRespVO field;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

    @Schema(description = "是否可查询", example = "true")
    private Boolean isSearchable;

    @Schema(description = "是否可筛选", example = "true")
    private Boolean isFilterable;

    @Schema(description = "是否可排序", example = "true")
    private Boolean isSortable;

    @Schema(description = "默认值", example = "默认值")
    private String defaultValue;

    @Schema(description = "业务规则（JSON格式）", example = "{\"min\": 0, \"max\": 100}")
    private String validationRules;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "字段分组 ID", example = "1")
    private Long fieldGroupId;

    // ========== 字段来源标识 ==========

    @Schema(description = "字段来源标识：BASE=固定列字段，CUSTOM=扩展字段，RELATION=关联字段", example = "CUSTOM")
    private String fieldSource;

    @Schema(description = "是否可编辑", example = "true")
    private Boolean editable;

    @Schema(description = "是否可删除", example = "true")
    private Boolean deletable;

    // ========== 固定列字段专用属性 ==========

    @Schema(description = "字段编码", example = "code")
    private String fieldCode;

    @Schema(description = "数据类型", example = "TEXT")
    private String dataType;

    @Schema(description = "类型配置（JSON格式）", example = "{\"precision\": 10, \"scale\": 2}")
    private String typeConfig;

    // ========== 关联字段专用属性 ==========

    @Schema(description = "关联字段库ID", example = "1")
    private Long refLibraryId;

    @Schema(description = "关联目标业务类型编码", example = "personnel")
    private String targetEntityType;

    @Schema(description = "关联目标业务类型名称", example = "人员管理")
    private String targetEntityTypeName;

    @Schema(description = "关联目标 Model 编码", example = "employee")
    private String targetModelCode;

    @Schema(description = "关联目标 Model 名称", example = "员工")
    private String targetModelName;

    @Schema(description = "展示字段编码", example = "name")
    private String displayFieldCode;

    /**
     * 字段来源常量
     */
    public static final String FIELD_SOURCE_BASE = "BASE";
    public static final String FIELD_SOURCE_CUSTOM = "CUSTOM";
    public static final String FIELD_SOURCE_RELATION = "RELATION";
}
