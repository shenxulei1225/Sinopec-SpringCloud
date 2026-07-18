package cn.cheers.x.inspection.inspection_content.controller.admin.vo.collection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 巡检对象集合分页查询 Request VO。
 */
@Schema(description = "管理后台 - 巡检对象集合分页查询 Request VO")
@Data
public class InspectionObjectCollectionPageReqVO {

    @Schema(description = "集合编码")
    private String collectionCode;

    @Schema(description = "集合名称")
    private String collectionName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNo = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;
}
