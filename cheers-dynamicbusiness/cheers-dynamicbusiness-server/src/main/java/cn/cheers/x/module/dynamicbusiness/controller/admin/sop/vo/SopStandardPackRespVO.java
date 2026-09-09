package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "SOP 标准包详情")
@Data
public class SopStandardPackRespVO {

    @Schema(description = "SOP id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sopId;

    @Schema(description = "适用范围规则")
    private List<ScopeRule> scopeRules = new ArrayList<>();

    @Schema(description = "标准检查项包")
    private List<ItemPackRow> itemPack = new ArrayList<>();

    @Data
    @Schema(description = "SOP 适用范围规则")
    public static class ScopeRule {
        private Long id;
        private String scopeType;
        private Long targetId;
        private String targetName;
        private Integer sortNo;
        private String note;
    }

    @Data
    @Schema(description = "SOP 标准检查项包行")
    public static class ItemPackRow {
        private Long id;
        private Long inspectionItemId;
        private String inspectionItemName;
        private Boolean required;
        private Integer sortNo;
        private String note;
    }
}
