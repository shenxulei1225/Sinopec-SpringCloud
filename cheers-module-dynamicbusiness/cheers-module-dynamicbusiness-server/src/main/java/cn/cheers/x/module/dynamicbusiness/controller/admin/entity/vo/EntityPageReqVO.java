package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Schema(description = "管理后台 - 实体分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntityPageReqVO extends PageParam {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String entityTypeCode;

    @Schema(description = "模型ID（可选，用于查询特定模型下的实体）", example = "1")
    private Long modelId;

    @Schema(description = "状态（可选，0-禁用，1-启用）", example = "1")
    private Integer status;

    @Schema(description = "关键词（可选，模糊匹配实体名称）", example = "设备")
    private String keyword;

    @Schema(description = "结构化筛选条件（可选）")
    private List<FieldFilterReqVO> filters;

}

