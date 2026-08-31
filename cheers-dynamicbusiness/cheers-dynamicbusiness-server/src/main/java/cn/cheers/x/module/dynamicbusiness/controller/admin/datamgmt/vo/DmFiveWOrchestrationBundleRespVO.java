package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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

    /** 当前对象从哪来：LIST_ROW=点列表这一行；CATEGORY_NODE=点树上这个节点 */
    private String objectPickFrom;

    @Schema(description = "What 槽位")
    private WhatSlot whatSlot;

    @Schema(description = "How 槽位")
    private HowSlot howSlot;

    @Data
    public static class Semantic {
        private Boolean enabled;
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
        /**
         * How 能力码。sopHow → 前端渲染通用 SOP How 工作台；
         * 存于 how_config.capability（配方/配置写入，代码不写死业务类型码）。
         */
        private String capability;
        /**
         * capability=sopHow 时的键映射（subjectType/hostType/dimensionKey/dimensionOptions）。
         * 原样透传 how_config.sopHow，不在此校验业务类型码。
         */
        private Map<String, Object> sopHow;
        private String candidateEntityTypeCode;
        private Long panelPropsId;
    }
}
