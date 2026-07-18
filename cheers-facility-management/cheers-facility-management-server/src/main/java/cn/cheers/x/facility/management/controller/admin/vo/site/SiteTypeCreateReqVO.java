package cn.cheers.x.facility.management.controller.admin.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 站场类型创建请求 VO
 */
@Schema(description = "管理后台 - 站场类型创建请求 VO")
@Data
public class SiteTypeCreateReqVO {

    @Schema(description = "类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "GAS_STATION")
    @NotBlank(message = "类型编码不能为空")
    private String typeCode;

    @Schema(description = "类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "加油站")
    @NotBlank(message = "类型名称不能为空")
    private String typeName;

    @Schema(description = "类型描述", example = "用于加油服务的站场")
    private String description;

    @Schema(description = "排序号", example = "1")
    private Integer sortNo;

    @Schema(description = "备注", example = "这是加油站的描述")
    private String remark;

}
