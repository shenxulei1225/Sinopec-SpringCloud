package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "数据管理 - 栏间关系声明保存项")
@Data
public class DmDataTabColumnRelationSaveItemVO {

    @Schema(description = "本页内稳定关系编号；缺省由服务端生成")
    private String edgeId;

    @NotBlank(message = "fromColumnIdentity 不能为空")
    private String fromColumnIdentity;

    @NotBlank(message = "toColumnIdentity 不能为空")
    private String toColumnIdentity;

    @Schema(description = "关联种类（两端端点类型组合）；可空，由两端列身份推断")
    private String relationKind;

    @NotBlank(message = "fromTypeCode 不能为空")
    private String fromTypeCode;

    @NotBlank(message = "toTypeCode 不能为空")
    private String toTypeCode;

    @Schema(description = "边动作：filter=条件筛选，write=修改关联，detail_follow=详情跟随")
    private String edgeAction;

    @Schema(description = "启用的交互方式；写边至少一项；条件筛选边可空")
    private List<String> enabledInteractions;

    @Schema(description = "可选引用字段列表；实体—实体时可空（允许仅拖挂关联）")
    private List<String> linkKeys;

    private Map<String, Object> presentation;
}
