package cn.cheers.x.facility.management.api.dto;

import cn.cheers.x.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设施分页请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityPageReqDTO extends PageParam {

    /**
     * 设施名称（模糊查询）
     */
    private String facilityName;

    /**
     * 设施类型编码
     */
    private String facilityTypeCode;

    /**
     * 所属区域ID
     */
    private Long siteId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 设施型号
     */
    private String model;

}
