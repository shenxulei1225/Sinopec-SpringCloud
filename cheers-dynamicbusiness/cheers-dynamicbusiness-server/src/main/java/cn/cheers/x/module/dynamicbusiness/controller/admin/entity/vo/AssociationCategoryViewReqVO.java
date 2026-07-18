package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 详情请求中指定的「某个 REF 字段 + 分类视角（categoryTypeCode）」。
 * <p>可传多条，同一 fieldCode 可对应多个 categoryTypeCode，实现一次请求多视角。</p>
 */
@Schema(description = "关联展示视角：字段编码 + 分类类型编码")
@Data
public class AssociationCategoryViewReqVO {

    @Schema(description = "字段编码，与 customFields、关系表 fieldCode 一致", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fieldCode;

    @Schema(description = "分类类型编码，对应分类树维度（如任务分类、设备分类）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryTypeCode;
}
