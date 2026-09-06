package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据目录编排头响应：是否启用、点树还是点列表行")
@Data
public class DmCatalogOrchestrationBundleRespVO {

    @Schema(description = "目录注册编码")
    private String registryCode;

    @Schema(description = "底座类型编码")
    private String storageEntityTypeCode;

    @Schema(description = "是否启用")
    private Semantic semantic;

    /** 当前记录来源：LIST_ROW=点列表这一行；CATEGORY_NODE=点树上这个节点 */
    private String selectionSource;

    @Data
    public static class Semantic {
        private Boolean enabled;
    }
}
