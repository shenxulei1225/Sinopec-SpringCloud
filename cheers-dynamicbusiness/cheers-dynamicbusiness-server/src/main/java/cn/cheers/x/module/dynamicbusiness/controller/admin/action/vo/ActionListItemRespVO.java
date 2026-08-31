package cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    @Schema(description = "执行手段", example = "UAV")
    private String executionMeans;

    @Schema(description = "参数槽编码列表（过渡；优先用 modelId 读参数型号字段）")
    private List<String> paramSlots = new ArrayList<>();

    @Schema(description = "是否复合动作")
    private Boolean composite;

    @Schema(description = "复合子动作 code 列表")
    private List<String> childActionIds = new ArrayList<>();
}
