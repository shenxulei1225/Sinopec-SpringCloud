package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 实体详情中单个 REF / ENTITY_REF_MULTI 字段对应的关联数据块。
 * <p>多个此类块组成 {@link EntityRespVO#getAssociations()}，与多个 REFMulti 字段一一对应。</p>
 *
 * <p>{@code views} 的 key 为 {@code categoryTypeCode}；未请求分类视角时可为 {@code default} 键下的扁平行。</p>
 */
@Schema(description = "单个关联字段（REF/REFMulti）的关联数据块")
@Data
public class AssociationsVO {

    @Schema(description = "字段编码 fieldCode")
    private String fieldCode;

    @Schema(description = "字段名称（区块标题）")
    private String fieldName;

    @Schema(description = "字段类型，如 ENTITY_REF、ENTITY_REF_MULTI")
    private String fieldType;

    @Schema(description = "按分类视角（categoryTypeCode）分组的行列表；key 为 categoryTypeCode，或 default 表示未按分类解析")
    private Map<String, List<AssociationEntityRowVO>> views;
}
