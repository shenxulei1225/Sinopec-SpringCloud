package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - 统一实体查询（按场景）Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntitySceneQueryReqVO extends PageParam {

    @Schema(description = "查询场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENTITIES_BY_MODEL")
    @NotNull(message = "scene 不能为空")
    private EntityQueryScene scene;

    @Schema(description = "结果形态（PAGE/TREE 等）", example = "PAGE")
    private String resultShape = "PAGE";

    @Schema(description = "结果详情（FULL/LIGHT）。列表默认 LIGHT（基础字段）；需 customFields 等明细时传 FULL", example = "LIGHT")
    private String resultDetail = "LIGHT";

    @Schema(description = "分类体系编码", example = "equipment_category")
    private String categoryTypeCode;

    @Schema(description = "业务类型编码", example = "equipment")
    private String entityTypeCode;

    @Schema(description = "模型 ID 列表", example = "[48, 47]")
    private List<Long> modelIds;

    @Schema(description = "分类 ID 列表", example = "[1, 2]")
    private List<Long> categoryIds;

    @Schema(description = "经 REF 反查路径编码（白名单）；非空时按分类→目标→REF 查主体，不做主体直接挂靠",
            example = "task_via_equipment_category")
    private String categoryViaRefPathCode;

    @Schema(description = "实体 ID", example = "100")
    private Long entityId;

    @Schema(description = "根实体 ID", example = "100")
    private Long rootEntityId;

    @Schema(description = "实体来源业务类型编码（模式 C）", example = "equipment")
    private String entitySourceEntityType;

    @Schema(description = "搜索关键词", example = "泵")
    private String keyword;

    @Schema(description = "结构化字段筛选条件")
    private List<FieldFilterReqVO> fieldFilters;

    @Schema(description = "业务域（Domain），可选；子数据类型入口未传时回落到入口自身业务域", example = "巡检")
    private String domain;

}
