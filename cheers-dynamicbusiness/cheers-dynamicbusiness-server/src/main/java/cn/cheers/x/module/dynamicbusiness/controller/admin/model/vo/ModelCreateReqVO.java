package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 业务模型创建请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 业务模型创建请求")
@Data
public class ModelCreateReqVO {

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "9kg 泡沫灭火器 A 型号")
    @NotBlank(message = "模型名称不能为空")
    private String name;

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "模型描述", example = "适用于消防设备的9kg泡沫灭火器A型号")
    private String description;

    @Schema(description = "模型状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型状态不能为空")
    private Integer status;

    @Schema(description = "模型在业务类型下的显示顺序（可选，不传则自动追加到末尾）", example = "1")
    private Integer sort;

    @Schema(description = "分类ID（可选，用于绑定模型到单个分类，兼容旧版）", example = "1")
    private Long categoryId;

    @Schema(description = "分类ID列表（可选，用于绑定模型到多个分类，支持多对多关系）", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @Schema(description = "业务域（Domain）；子数据类型入口下创建型号时写入", example = "巡检")
    private String domain;

    @Schema(description = "治理状态：LOCAL-站场本地型号，COMPANY-公司规格；服务端按全网治理权限定稿", example = "LOCAL")
    private String governanceStatus;

    @Schema(description = "当前有效站场 ID；创建本地型号时必填", example = "1001")
    private Long effectiveFacilityId;

}












































