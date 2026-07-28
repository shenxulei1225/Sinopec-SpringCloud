package cn.cheers.x.inspection.inspection_content.service.point.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点位检查项读模型。
 */
@Data
public class InspectionExecutionPointItemView {

    private Long id;
    private Long facilityId;
    private Long pointId;
    private Long itemId;
    private String paramsJson;
    private Integer sortNo;
    private Boolean enabled;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
