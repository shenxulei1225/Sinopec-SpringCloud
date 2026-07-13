package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "实体变更模型 - 字段迁移项")
@Data
public class EntityChangeModelFieldItemVO {

    @Schema(description = "字段编码 fieldCode")
    private String fieldCode;

    @Schema(description = "字段显示名")
    private String label;

    @Schema(description = "原值（预览用）")
    private Object value;

    @Schema(description = "分桶：base / custom")
    private String bucket;

    @Schema(description = "目标模型是否必填")
    private Boolean required;

    @Schema(description = "数据类型（补填控件参考）")
    private String dataType;
}
