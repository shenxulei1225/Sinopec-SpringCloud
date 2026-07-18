package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理后台 - 模型筛选字段元信息")
public class ModelFilterFieldMetaRespVO {

    @Schema(description = "字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "字段编码", example = "F-abc123")
    private String fieldCode;

    @Schema(description = "字段名称", example = "生产日期")
    private String fieldName;

    @Schema(description = "字段类型", example = "DATE")
    private String fieldType;

    @Schema(description = "是否可搜索", example = "true")
    private Boolean searchable;

    @Schema(description = "是否可筛选", example = "true")
    private Boolean filterable;

    @Schema(description = "是否可排序", example = "true")
    private Boolean sortable;

    @Schema(description = "支持的筛选操作符")
    private List<String> operators;

    @Schema(description = "枚举选项（ENUM/SELECT/MULTI_SELECT）")
    private String options;
}
