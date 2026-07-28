package cn.cheers.x.inspection.inspection_content.service.point.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 执行点位读模型。
 */
@Data
public class InspectionExecutionPointView {

    private Long id;
    private String pointCode;
    private String pointName;
    private Long facilityId;
    private Long objectId;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal height;
    private BigDecimal yaw;
    private BigDecimal pitch;
    private BigDecimal roll;
    private String deviceType;
    private Integer weight;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
