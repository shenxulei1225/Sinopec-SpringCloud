package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 实体创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityCreateReqVO extends EntityBaseVO {

    /**
     * @deprecated 创建侧可不传：服务端按 {@link #relateModelIds} 反查型号表上的 entityTypeCode。
     * 仍传入时仅作校验提示，以型号主数据为准。
     */
    @Deprecated
    @Schema(description = "（可选，已弃用）关联型号所属数据类型；优先由 relateModelIds 反查",
            example = "equipment", deprecated = true)
    private String modelEntityTypeCode;

    /**
     * 需要挂靠的型号 ID（如场景 8 中间列已选的设备型号）。
     * <p>创建仍用 baseFields.modelId 作为本类归属型号；本列表由服务端按型号主数据反查类型，
     * 与实体类型不一致时写入型号—实体关联表，一致则跳过（归属已在 model_id）。</p>
     */
    @Schema(description = "关联型号 ID 列表（创建后按型号类型决定是否写关联表）", example = "[48]")
    private List<Long> relateModelIds;
}
