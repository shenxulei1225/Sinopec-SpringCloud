package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务能力列表项响应。
 *
 * <p>用于前端业务数据来源下拉，不返回大体积契约正文。</p>
 */
@Schema(description = "管理后台 - 业务能力列表项响应")
@Data
public class BusinessCapabilitySummaryRespVO {

    @Schema(description = "业务分类：dynamic / system", requiredMode = Schema.RequiredMode.REQUIRED, example = "dynamic")
    private String businessCategory;

    @Schema(description = "业务类型编码（能力主索引）", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String entityTypeCode;

    @Schema(description = "业务类型名称（展示名）", example = "设备管理")
    private String entityTypeName;

    @Schema(description = "支持的数据种类：dynamic 为 model/entity；system 固定 entity", example = "[\"model\",\"entity\"]")
    private java.util.List<String> supportedDataKinds;

    @Schema(description = "能力版本号", example = "8")
    private Long version;
}
