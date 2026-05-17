package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分类响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryRespVO extends CategoryBaseVO {

    @Schema(description = "分类编号", example = "1")
    private Long id;

    @Schema(description = "树路径", example = "/1/2/")
    private String treePath;

    @Schema(description = "层级", example = "2")
    private Integer level;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
