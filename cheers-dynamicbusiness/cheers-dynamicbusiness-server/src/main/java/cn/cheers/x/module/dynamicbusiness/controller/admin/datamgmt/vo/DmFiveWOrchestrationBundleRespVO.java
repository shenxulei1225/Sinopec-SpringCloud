package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "五维编排 - bundle 响应")
@Data
public class DmFiveWOrchestrationBundleRespVO {

    @Schema(description = "注册编码")
    private String registryCode;

    @Schema(description = "存储类型编码")
    private String storageEntityTypeCode;

    @Schema(description = "语义块")
    private Semantic semantic;

    @Schema(description = "Who 槽位")
    private List<WhoSlot> whoSlots;

    @Schema(description = "What 槽位")
    private WhatSlot whatSlot;

    @Schema(description = "How 槽位")
    private HowSlot howSlot;

    @Data
    public static class Semantic {
        private Boolean enabled;
        /** MODEL | ENTITY */
        private String selectionLevel;
    }

    @Data
    public static class WhoSlot {
        private Long id;
        private String slotRef;
        /** CATEGORY | MODEL | ENTITY */
        private String columnKind;
        private String perspectiveId;
        private Long propsId;
        private Boolean enabled;
        private List<String> contextOutputs;
        /** rowSelection | categoryLinkedEntity */
        private String entityIdRule;
        private Map<String, Object> categoryColumn;
    }

    @Data
    public static class WhatSlot {
        private String mode;
        private String bindLayer;
        private Long detailPropsId;
        private Long listPropsId;
        private String candidateEntityTypeCode;
        /** 候选库左侧分类种类（可与候选实体类型不同，如检查项按设备分类浏览） */
        private String candidateCategoryTypeCode;
        private Boolean detailReadonly;
        private Long panelPropsId;
        private String domain;
        private String relationKind;
    }

    @Data
    public static class HowSlot {
        private String mode;
    }
}
