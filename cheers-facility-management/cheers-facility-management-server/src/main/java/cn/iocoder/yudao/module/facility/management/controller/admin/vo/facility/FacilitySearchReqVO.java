package cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设施搜索请求 VO
 */
@Schema(description = "管理后台 - 设施搜索请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FacilitySearchReqVO extends PageParam {

    @Schema(description = "站点ID")
    private Long siteId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "设备类型")
    private String equipmentType;

    @Schema(description = "设施类型")
    private String facilitiesType;

    @Schema(description = "使用状态")
    private String usageState;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "关键字")
    private String keyword;

}
