package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务类型固定列字段 响应 VO
 * 
 * <h3>用途</h3>
 * <p>返回业务类型固定列字段的详细信息,包含字段元数据和权限标识。</p>
 * 
 * <h3>使用场景</h3>
 * <ul>
 *   <li>管理后台 - 业务类型配置页面,展示固定列字段列表</li>
 *   <li>模型字段管理页面,区分固定列字段(不可编辑/删除)和自定义字段</li>
 *   <li>实体表单渲染时,根据 fieldSource 判断字段来源</li>
 * </ul>
 * 
 * <h3>关键字段</h3>
 * <ul>
 *   <li>fieldSource - 字段来源:BASE=固定列,CUSTOM=自定义</li>
 *   <li>editable/deletable - 固定列字段通常为 false,前端据此禁用编辑/删除按钮</li>
 *   <li>typeConfig - JSON 格式类型配置,如数字精度、枚举选项等</li>
 * </ul>
 * 
 * <h3>关联 VO</h3>
 * <ul>
 *   <li>查询请求 → {@link EntityTypeBaseFieldPageReqVO}</li>
 *   <li>保存请求 → {@link EntityTypeBaseFieldSaveReqVO}</li>
 * </ul>
 */
@Schema(description = "管理后台 - 业务类型固定列字段 Response VO")
@Data
public class EntityTypeBaseFieldRespVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String entityTypeCode;

    @Schema(description = "字段编码(对应数据库列名)", requiredMode = Schema.RequiredMode.REQUIRED, example = "code")
    private String fieldCode;

    @Schema(description = "字段显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备编码")
    private String fieldName;

    @Schema(description = "数据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEXT")
    private String dataType;

    @Schema(description = "默认值", example = "NORMAL")
    private String defaultValue;

    @Schema(description = "字段描述", example = "设备唯一编码")
    private String description;

    @Schema(description = "类型配置(JSON格式)", example = "{\"precision\": 10, \"scale\": 2}")
    private String typeConfig;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态(1=启用,0=禁用)", example = "1")
    private Integer status;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

    @Schema(description = "是否可搜索", example = "true")
    private Boolean isSearchable;

    @Schema(description = "是否可筛选", example = "true")
    private Boolean isFilterable;

    @Schema(description = "是否可排序", example = "true")
    private Boolean isSortable;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // ========== 扩展字段 ==========

    @Schema(description = "字段来源标识", example = "BASE")
    private String fieldSource;

    @Schema(description = "是否可编辑(固定列字段在模型字段管理页面不可编辑,但可以在“业务类型固定列字段”页面编辑字段定义)", example = "false")
    private Boolean editable;

    @Schema(description = "是否可删除(固定列字段不可删除)", example = "false")
    private Boolean deletable;

    @Schema(description = "字段库字段 ID", example = "3681")
    private Long libraryFieldId;

    @Schema(description = "字段库 code", example = "FLD-BASE-facility-address")
    private String libraryFieldCode;

    @Schema(description = "字段库默认名称", example = "联系电话")
    private String libraryFieldName;
}
