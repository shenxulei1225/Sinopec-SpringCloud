package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 关联字段数据
 *
 * 用于在API响应中提供按属性自动分组的关联数据展示。
 * 前端可以直接使用grouped数据进行分组展示。
 */
@Schema(description = "关联字段数据")
@Data
@Builder
public class AssociationData {

    @Schema(description = "原始实体ID列表", example = "[101, 102, 201]")
    private List<Long> rawIds;

    @Schema(description = "按属性分组的实体数据")
    private Map<String, List<AssociatedEntityData>> grouped;
}