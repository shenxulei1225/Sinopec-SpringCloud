package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection;

import cn.iocoder.yudao.module.inspection.task.model.task.InspectionContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检对象集合 Response VO。
 */
@Schema(description = "管理后台 - 巡检对象集合 Response VO")
@Data
public class InspectionObjectCollectionRespVO {

    @Schema(description = "集合ID", example = "1")
    private Long id;

    @Schema(description = "集合编码")
    private String collectionCode;

    @Schema(description = "集合名称")
    private String collectionName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "对象数量")
    private Integer objectCount;

    @Schema(description = "巡检项数量")
    private Integer itemCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "集合内容（完整的对象和巡检项配置）")
    private InspectionContent content;
}
