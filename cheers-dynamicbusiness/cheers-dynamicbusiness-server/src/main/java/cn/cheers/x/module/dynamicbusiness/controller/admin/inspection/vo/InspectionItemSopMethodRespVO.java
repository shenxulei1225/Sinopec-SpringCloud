package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "检查项方法行")
@Data
public class InspectionItemSopMethodRespVO {

    private Long id;
    private Long inspectionItemId;
    private Long sopTemplateId;
    private String executionMeans;
    private Integer sort;
    private String sopName;
    private Boolean sopIsTemplate;
}
