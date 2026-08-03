package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "五维编排 - bundle 保存")
@Data
public class DmFiveWOrchestrationBundleSaveReqVO {

    @Schema(description = "注册编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "registryCode 不能为空")
    private String registryCode;

    @Schema(description = "存储类型编码")
    private String storageEntityTypeCode;

    @NotNull(message = "semantic 不能为空")
    @Valid
    private DmFiveWOrchestrationBundleRespVO.Semantic semantic;

    @NotNull(message = "whoSlots 不能为空")
    private List<DmFiveWOrchestrationBundleRespVO.WhoSlot> whoSlots;

    @NotNull(message = "whatSlot 不能为空")
    private DmFiveWOrchestrationBundleRespVO.WhatSlot whatSlot;

    @NotNull(message = "howSlot 不能为空")
    private DmFiveWOrchestrationBundleRespVO.HowSlot howSlot;
}
