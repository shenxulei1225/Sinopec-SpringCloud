package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类类型 Response VO
 */
@Schema(description = "管理后台 - 分类类型 Response VO")
@Data
public class CategoryTypeRespVO {

    @Schema(description = "分类类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "project_phase")
    private String categoryTypeCode;

    @Schema(description = "分类类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "项目阶段")
    private String name;

    @Schema(description = "分类类型描述", example = "用于管理项目执行的不同阶段")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "创建者", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String creator;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    @Schema(description = "顶层分类ID（该分类类型的根节点）", example = "100")
    private Long topLevelCategoryId;

    @Schema(
        description = "分类建立方式：SIMPLE=简单分类；ADVANCED=高级分类（节点可绑定实体）",
        example = "SIMPLE",
        allowableValues = {"SIMPLE", "ADVANCED"}
    )
    private String categoryMode;

}