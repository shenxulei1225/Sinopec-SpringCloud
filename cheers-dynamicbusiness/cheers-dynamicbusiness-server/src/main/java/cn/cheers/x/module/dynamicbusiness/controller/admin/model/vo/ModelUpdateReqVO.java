package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 业务模型更新请求 VO
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 业务模型更新请求")
@Data
public class ModelUpdateReqVO {

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long id;

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

    @Schema(description = "模型在业务类型下的显示顺序（可选）", example = "1")
    private Integer sort;

    @Schema(description = "是否启用实体层级（实体树）", example = "false")
    private Boolean isTreeEntity;

    @Schema(description = "分类ID列表（可选，用于更新模型与分类的绑定关系）", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @Schema(description = "是否完全替换分类关联（默认false为增量添加模式，true为完全替换模式）。当为false时，只添加新分类，不删除现有分类；当为true时，使用差集更新（删除不在新列表中的，添加不在现有列表中的）", example = "false")
    private Boolean replaceCategories;

    @Schema(description = "业务域（Domain）；须为该存储类型下已建 DOMAIN 子数据类型的域标识", example = "巡检")
    private String domain;

    @Schema(description = "治理状态：LOCAL-站场本地型号，COMPANY-公司规格；服务端按全网治理权限校验", example = "LOCAL")
    private String governanceStatus;

    @Schema(description = "当前有效站场 ID；用于校验本地型号可见性", example = "1001")
    private Long effectiveFacilityId;

}

