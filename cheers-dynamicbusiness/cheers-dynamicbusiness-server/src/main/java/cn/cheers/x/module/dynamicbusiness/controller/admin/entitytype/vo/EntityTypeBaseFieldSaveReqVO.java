package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 业务类型固定列字段 保存请求 VO
 * 
 * <h3>用途说明</h3>
 * <p>用于创建或更新业务类型的固定列字段定义。固定列字段是预定义在专用表中的物理列。</p>
 * 
 * <h3>应用场景</h3>
 * <ul>
 *   <li><b>接口</b>：POST /system/entity-type/base-field/create、PUT /system/entity-type/base-field/update</li>
 *   <li><b>场景1</b>：系统初始化时，为 equipment 业务类型定义 code、name、status 等固定列</li>
 * </ul>
 * 
 * <h3>使用注意</h3>
 * <ul>
 *   <li><b>id 为空</b>：创建新字段</li>
 *   <li><b>id 不为空</b>：更新已有字段</li>
 *   <li><b>fieldCode</b>：必须与数据库物理列名一致</li>
 *   <li><b>dataType</b>：支持 TEXT、NUMBER、DATE、DATETIME、BOOLEAN、ENUM、REFERENCE、REF_Multi</li>
 * </ul>
 * 
 * <h3>与其他 VO 的关系</h3>
 * <ul>
 *   <li>响应结果 → {@link EntityTypeBaseFieldRespVO}</li>
 * </ul>
 */
@Schema(description = "管理后台 - 业务类型固定列字段保存 Request VO")
@Data
public class EntityTypeBaseFieldSaveReqVO {

    @Schema(description = "字段ID（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "字段库字段 ID（新增时必填，须从字段库选择，不可手填编码/名称/类型）", example = "3681")
    private Long libraryFieldId;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    @Size(max = 50, message = "业务类型编码长度不能超过50个字符")
    private String entityTypeCode;

    @Schema(description = "字段编码（对应数据库列名，新增时由字段库条目推导，勿手填）", example = "code")
    @Size(max = 100, message = "字段编码长度不能超过100个字符")
    private String fieldCode;

    @Schema(description = "字段显示名称（新增时由字段库条目推导，勿手填）", example = "设备编码")
    @Size(max = 200, message = "字段显示名称长度不能超过200个字符")
    private String fieldName;

    @Schema(description = "数据类型（新增时由字段库条目推导，勿手填）", example = "TEXT")
    @Size(max = 50, message = "数据类型长度不能超过50个字符")
    private String dataType;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

    @Schema(description = "是否可搜索", example = "true")
    private Boolean isSearchable;

    @Schema(description = "是否可筛选", example = "true")
    private Boolean isFilterable;

    @Schema(description = "是否可排序", example = "true")
    private Boolean isSortable;

    @Schema(description = "默认值", example = "NORMAL")
    @Size(max = 500, message = "默认值长度不能超过500个字符")
    private String defaultValue;

    @Schema(description = "字段描述", example = "设备唯一编码")
    @Size(max = 500, message = "字段描述长度不能超过500个字符")
    private String description;

    @Schema(description = "类型配置（JSON格式）", example = "{\"precision\": 10, \"scale\": 2}")
    @Size(max = 2000, message = "类型配置长度不能超过2000个字符")
    private String typeConfig;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态（1=启用，0=禁用）", example = "1")
    private Integer status;
}
