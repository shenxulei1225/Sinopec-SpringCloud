package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 字段分页查询请求")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FieldPageReqVO extends PageParam {

    @Schema(description = "字段类型", example = "TEXT")
    private String type;

    @Schema(description = "关键词（字段名称、描述）", example = "设备")
    private String keyword;

    @Schema(description = "来源（SYSTEM/CUSTOM）", example = "SYSTEM")
    private String source;

    @Schema(description = "状态（0-禁用，1-启用）", example = "1")
    private Integer status;

    @Schema(description = "当前有效站场编号；用于本地字段可见性判断", example = "12")
    private Long effectiveFacilityId;
}












































