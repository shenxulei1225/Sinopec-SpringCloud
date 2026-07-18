package cn.cheers.x.inspection.inspection_content.vo.object;

import lombok.Data;

/**
 * 巡检对象创建请求 VO。
 */
@Data
public class InspectionObjectCreateReqVO {

    private String objectCode;
    private String objectName;
    private Long siteId;
    private String objectType;
    private Long sourceFacilityId;
    private Long ascriptionId;
    private String remark;
}
