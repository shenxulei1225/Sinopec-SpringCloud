package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分类类型 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryTypeRespVO extends CategoryTypeBaseVO {

    @Schema(description = "分类类型编号", example = "1")
    private Long id;

    @Schema(description = "顶层分类ID", example = "100")
    private Long topLevelCategoryId;

    @Schema(description = "创建者", example = "admin")
    private String creator;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
