package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分类响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = false)
public class CategoryRespVO extends CategoryBaseVO {

    @Schema(description = "分类编号", example = "1024")
    private Long id;

    @Schema(description = "树路径", example = "生产设备/加工设备")
    private String treePath;

    @Schema(description = "层级", example = "2")
    private Integer level;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

