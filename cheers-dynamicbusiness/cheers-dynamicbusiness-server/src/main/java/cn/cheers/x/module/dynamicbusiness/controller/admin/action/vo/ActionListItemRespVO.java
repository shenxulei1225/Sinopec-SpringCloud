package cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "动作库列表项")
@Data
public class ActionListItemRespVO {

    @Schema(description = "动作实体 id")
    private Long id;

    @Schema(description = "动作业务编码（SOP 树节点 actionId 常用此值）", example = "act-arrive")
    private String code;

    @Schema(description = "动作显示名")
    private String name;

    @Schema(description = "参数型号 id（LIBRARY 执行参数字段权威来源）")
    private Long modelId;

    @Schema(description = "动作类型模型编码")
    private String modelCode;

    @Schema(description = "执行手段", example = "UAV")
    private String executionMeans;

    @Schema(description = "动作归属", allowableValues = {
            "DEVICE", "INTELLIGENT_ANALYSIS", "MANUAL", "PLATFORM"
    })
    private String actionDomain;

    @Schema(description = "智能分析能力 id")
    private Long analysisCapabilityId;

    @Schema(description = "参数槽编码列表（param_slots_json 全手段合并）")
    private List<String> paramSlots = new ArrayList<>();

    @Schema(description = "按执行手段拆开的参数槽（param_slots_json methods）")
    private Map<String, List<String>> paramSlotsByMeans = new LinkedHashMap<>();

    @Schema(description = "已按值来源分组的动作来源字段（全手段合并）")
    private List<ParamSourceVO> paramSources = new ArrayList<>();

    @Schema(description = "按执行手段拆开的动作来源字段")
    private Map<String, List<ParamSourceVO>> paramSourcesByMeans = new LinkedHashMap<>();

    @Schema(description = "动作组成员节点；每个成员有独立 memberKey、动作编号和参数预设")
    private Object actionTree;

    @Schema(description = "动作在协议转换中可提供的一项来源字段")
    @Data
    public static class ParamSourceVO {
        private String fieldCode;
        private String sourceKind;
        private Object defaultValue;
        private String generator;
    }
}
