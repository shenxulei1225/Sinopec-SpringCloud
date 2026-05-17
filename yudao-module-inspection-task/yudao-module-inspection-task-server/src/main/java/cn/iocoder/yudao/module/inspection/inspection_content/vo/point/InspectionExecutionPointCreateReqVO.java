package cn.iocoder.yudao.module.inspection.inspection_content.vo.point;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 执行点位创建请求 VO。
 */
@Data
public class InspectionExecutionPointCreateReqVO {

    private String pointCode;
    private String pointName;
    private Long siteId;
    private Long objectId;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal height;
    private BigDecimal yaw;
    private BigDecimal pitch;
    private BigDecimal roll;
    private String deviceType;
    private Integer weight;
    private String remark;
}
