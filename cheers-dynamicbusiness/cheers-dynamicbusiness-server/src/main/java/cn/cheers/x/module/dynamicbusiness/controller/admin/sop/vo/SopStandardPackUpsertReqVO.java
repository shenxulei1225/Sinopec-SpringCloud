package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "SOP 标准包更新请求")
@Data
public class SopStandardPackUpsertReqVO {

    @Valid
    @Schema(description = "适用范围规则")
    private List<ScopeRuleUpsert> scopeRules = new ArrayList<>();

    @Valid
    @Schema(description = "标准检查项包")
    private List<ItemPackRowUpsert> itemPack = new ArrayList<>();

    @Data
    @Schema(description = "适用范围规则")
    public static class ScopeRuleUpsert {
        @NotBlank
        @Schema(description = "范围类型：CATEGORY / MODEL", requiredMode = Schema.RequiredMode.REQUIRED)
        private String scopeType;

        @NotNull
        @Min(1)
        @Schema(description = "目标 id（分类 id 或型号 id）", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long targetId;

        @Schema(description = "顺序号")
        private Integer sortNo;

        @Schema(description = "备注")
        private String note;
    }

    @Data
    @Schema(description = "标准检查项包行")
    public static class ItemPackRowUpsert {
        @NotNull
        @Min(1)
        @Schema(description = "检查项实体 id", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long inspectionItemId;

        @Schema(description = "是否必做")
        private Boolean required;

        @Schema(description = "顺序号")
        private Integer sortNo;

        @Schema(description = "备注")
        private String note;
    }
}
