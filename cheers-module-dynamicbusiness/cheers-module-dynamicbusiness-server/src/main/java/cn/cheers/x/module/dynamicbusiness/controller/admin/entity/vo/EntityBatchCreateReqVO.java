package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量创建实体请求 VO（模板 + 明细）。
 *
 * <p>说明：当前版本先支持“公共模板 + 明细覆盖”创建。
 * 后续还需完善：
 * 1) 基于 categoryId 的自动关系挂接与事务一致性；
 * 2) 命名规则冲突检测与回填策略；
 * 3) 更丰富的差异字段合并策略（base/custom 字段级合并）。</p>
 */
@Data
public class EntityBatchCreateReqVO {

    @Schema(description = "公共模板（同批次公共字段）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公共模板不能为空")
    @Valid
    private EntityCreateReqVO base;

    @Schema(description = "批次执行范围（用于声明规则应用上下文）")
    @Valid
    private Scope scope;

    @Schema(description = "命名规则（可选）")
    @Valid
    private NamingRule namingRule;

    @Schema(description = "差异明细列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "差异明细不能为空")
    @Valid
    private List<Item> items;

    @Data
    public static class Scope {
        @Schema(description = "目标分类ID（可选；用于后续自动关联）", example = "10")
        private Long categoryId;

        @Schema(description = "父实体ID（可选；覆盖 base.parentId 时生效）", example = "1000")
        private Long parentEntityId;
    }

    @Data
    public static class NamingRule {
        @Schema(description = "是否允许名称重复", example = "true")
        private Boolean allowDuplicateName = Boolean.TRUE;

        @Schema(description = "是否启用序号命名", example = "false")
        private Boolean sequenceEnabled = Boolean.FALSE;

        @Schema(description = "名称分隔符", example = "-")
        private String separator = "-";

        @Schema(description = "起始序号", example = "1")
        @Min(value = 1, message = "起始序号必须大于0")
        private Integer startNo = 1;

        @Schema(description = "步长", example = "1")
        @Min(value = 1, message = "步长必须大于0")
        private Integer step = 1;

        @Schema(description = "序号补零位数（0 表示不补零）", example = "3")
        @Min(value = 0, message = "补零位数不能小于0")
        private Integer padLength = 0;
    }

    @Data
    public static class Item {
        @Schema(description = "名称（为空时继承 base.name 或命名规则）", example = "摄像头")
        private String name;

        @Schema(description = "父实体ID（可选，优先级高于 scope.parentEntityId）", example = "1000")
        private Long parentId;

        @Schema(description = "固定列字段JSON（可选，优先级高于 base.baseFields）")
        private String baseFields;

        @Schema(description = "自定义字段JSON（可选，优先级高于 base.customFields）")
        private String customFields;

        @Schema(description = "状态（可选，优先级高于 base.status）", example = "1")
        private Integer status;
    }
}
