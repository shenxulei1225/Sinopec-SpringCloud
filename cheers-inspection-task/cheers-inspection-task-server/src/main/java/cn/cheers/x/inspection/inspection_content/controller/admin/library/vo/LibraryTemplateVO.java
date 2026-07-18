package cn.cheers.x.inspection.inspection_content.controller.admin.library.vo;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 巡检配置模板 VO。
 *
 * <p>用于前端展示模板列表和详情。</p>
 */
@Schema(description = "管理后台 - 巡检配置模板 VO")
@Data
public class LibraryTemplateVO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板编码")
    private String collectionCode;

    @Schema(description = "模板名称")
    private String collectionName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "对象数量")
    private Integer objectCount;

    @Schema(description = "检查项数量")
    private Integer itemCount;

    @Schema(description = "模板内容（完整配置）", hidden = true)
    private InspectionContent content;
}
