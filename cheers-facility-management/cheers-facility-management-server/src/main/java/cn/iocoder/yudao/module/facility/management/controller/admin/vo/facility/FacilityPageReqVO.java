package cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 设施分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityPageReqVO extends PageParam {

    @Schema(description = "设施名称", example = "1号储罐")
    private String facilityName;

    @Schema(description = "所属区域ID", example = "1")
    private Long siteId;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "设施型号", example = "C-1000")
    private String model;

    @Schema(description = "状态：0-正常，1-停用", example = "0")
    private Integer status;

}
