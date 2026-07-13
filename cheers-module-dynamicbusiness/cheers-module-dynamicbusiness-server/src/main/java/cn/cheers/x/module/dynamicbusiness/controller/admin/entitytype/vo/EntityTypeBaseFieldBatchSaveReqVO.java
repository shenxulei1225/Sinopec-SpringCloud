package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - 业务类型基础字段批量保存 Request VO")
@Data
public class EntityTypeBaseFieldBatchSaveReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "新增的基础字段（须带 libraryFieldId）")
    @Valid
    private List<EntityTypeBaseFieldSaveReqVO> creates = new ArrayList<>();

    @Schema(description = "更新的基础字段（须带 id）")
    @Valid
    private List<EntityTypeBaseFieldSaveReqVO> updates = new ArrayList<>();

    @Schema(description = "待删除的基础字段 id")
    private List<Long> deleteIds = new ArrayList<>();

    @Schema(description = "系统字段别名变更")
    @Valid
    private List<PlatformFieldLabelSaveItem> platformFieldLabels = new ArrayList<>();

    @Data
    public static class PlatformFieldLabelSaveItem {

        @Schema(description = "系统字段编码", example = "name")
        @NotBlank(message = "系统字段编码不能为空")
        private String fieldCode;

        @Schema(description = "显示别名，空字符串表示恢复默认")
        private String label;
    }
}
