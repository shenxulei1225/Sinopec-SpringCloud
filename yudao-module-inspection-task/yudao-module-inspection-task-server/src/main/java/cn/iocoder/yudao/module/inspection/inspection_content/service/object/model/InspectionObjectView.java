package cn.iocoder.yudao.module.inspection.inspection_content.service.object.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 巡检对象读模型。
 */
@Data
public class InspectionObjectView {

    private Long id;
    private String objectCode;
    private String objectName;
    private Long siteId;
    private String objectType;
    private Long sourceFacilityId;
    private Long ascriptionId;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
