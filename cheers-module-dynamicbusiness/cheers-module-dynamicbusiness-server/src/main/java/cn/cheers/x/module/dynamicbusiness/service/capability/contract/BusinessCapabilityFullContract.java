package cn.cheers.x.module.dynamicbusiness.service.capability.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 能力全集契约（capability_full）。
 *
 * <p>职责边界：</p>
 * <ul>
 *   <li>描述某个 businessTypeCode 的完整能力契约结构；</li>
 *   <li>由业务能力服务层生成与解析，不属于 Controller 入参/出参 VO；</li>
 *   <li>持久化在 business_capability.capability_full JSONB 列。</li>
 * </ul>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "能力全集契约")
public class BusinessCapabilityFullContract {

    @Schema(description = "业务类型编码", example = "equipment")
    private String businessTypeCode;

    @Schema(description = "业务类型名称", example = "设备管理")
    private String businessTypeName; 

    @Schema(description = "业务类型层级", example = "USER")
    private String businessTypeLevel;

    @Schema(description = "能力来源", example = "dynamic")
    private String capabilitySource;

    @Schema(description = "能力版本", example = "1")
    private Integer capabilityVersion;

    @Schema(description = "支持的组件编码集合")
    private List<String> components;

    @Schema(description = "模型摘要")
    private ModelSummary modelSummary;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "模型摘要结构")
    public static class ModelSummary {
        @Schema(description = "模型数量", example = "43")
        private Integer modelCount;

        @Schema(description = "模型列表")
        private List<ModelSummaryItem> models;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "模型摘要项")
    public static class ModelSummaryItem {
        @Schema(description = "模型编号", example = "157")
        private Long modelId;

        @Schema(description = "模型编码", example = "MODEL-5284b71c69d946ae88c0848bfbbb0d7c")
        private String modelCode;

        @Schema(description = "模型名称", example = "测试模型")
        private String modelName;

        @Schema(description = "模型状态", example = "1")
        private Integer status;

        @Schema(description = "排序值", example = "0")
        private Integer sort;
    }
}
