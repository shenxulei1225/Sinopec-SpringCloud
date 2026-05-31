package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模型字段分组基础 VO
 */
@Data
public class ModelFieldGroupBaseVO {

    @Schema(description = "分组名称", example = "基础字段")
    private String name;

    @Schema(description = "分组颜色", example = "#409eff")
    private String color;

    @Schema(description = "排序顺序", example = "1")
    private Integer sort;
}
