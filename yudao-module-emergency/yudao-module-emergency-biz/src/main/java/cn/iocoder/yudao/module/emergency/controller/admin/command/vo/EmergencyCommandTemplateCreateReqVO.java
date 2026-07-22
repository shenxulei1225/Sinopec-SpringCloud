package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 应急指令模板创建 Request VO")
@Data
public class EmergencyCommandTemplateCreateReqVO {

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "消防处置指令")
    @NotEmpty(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "disposal")
    @NotEmpty(message = "模板分类不能为空")
    private String category;

    @Schema(description = "标题模板", example = "关于{事件地点}的处置指令")
    private String titleTemplate;

    @Schema(description = "内容模板", requiredMode = Schema.RequiredMode.REQUIRED, example = "请立即组织人员进行现场处置...")
    @NotEmpty(message = "内容模板不能为空")
    private String contentTemplate;

    @Schema(description = "适用场景ID列表", example = "[1, 2]")
    private List<Integer> applicableScenarios;

    @Schema(description = "适用阶段", requiredMode = Schema.RequiredMode.REQUIRED, example = "disposal")
    @NotEmpty(message = "适用阶段不能为空")
    private String stage;

    @Schema(description = "默认优先级", example = "HIGH")
    private String priority;

    @Schema(description = "关联自定义配置表单ID（可选）- 用于从模板创建指令时自动继承表单配置。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long formId;
}
