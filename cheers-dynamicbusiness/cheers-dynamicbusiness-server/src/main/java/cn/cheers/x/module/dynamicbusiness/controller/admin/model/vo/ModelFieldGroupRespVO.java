package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * 模型字段分组响应 VO
 */
@Schema(description = "管理后台 - 模型字段分组响应 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelFieldGroupRespVO extends ModelFieldGroupBaseVO {

    @Schema(description = "分组ID", example = "1024")
    private Long id;

    @Schema(description = "模型ID", example = "1024")
    private Long modelId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "分组下字段引用列表（按 sort 排序）")
    private List<FieldRefVO> fields;

    @Data
    public static class FieldRefVO {

        @Schema(description = "字段ID", example = "1")
        private Long fieldId;

        @Schema(description = "字段在分组内的排序", example = "1")
        private Integer sort;

        @Schema(description = "扩展信息（预留）")
        private Map<String, Object> extra;
    }
}
