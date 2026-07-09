package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 实体写请求体：仅 {@code baseFields} + {@code customFields} 两 Map。
 */
@Data
public class EntityBaseVO {

    @Schema(description = "固定列（BaseField），key 为 fieldCode。含 entityTypeCode、modelId、name、status、parentId 及业务基础列")
    @NotNull(message = "baseFields 不能为空")
    private Map<String, Object> baseFields;

    @Schema(description = "扩展列（CustomField），key 为 fieldCode")
    private Map<String, Object> customFields;
}
