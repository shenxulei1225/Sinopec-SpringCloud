package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "数据目录编排头保存：是否启用、点树还是点列表行")
@Data
public class DmCatalogOrchestrationBundleSaveReqVO {

    @Schema(description = "目录注册编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "registryCode 不能为空")
    private String registryCode;

    @Schema(description = "底座类型编码")
    private String storageEntityTypeCode;

    @NotNull(message = "semantic 不能为空")
    @Valid
    private DmCatalogOrchestrationBundleRespVO.Semantic semantic;

    /** 当前记录来源：LIST_ROW / CATEGORY_NODE */
    private String selectionSource;
}
