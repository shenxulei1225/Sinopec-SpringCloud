package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "数据管理 - 批量保存数据 Tab 布局")
@Data
public class DmDataTabLayoutSaveReqVO {

    @Schema(description = "工作台布局实例 id（优先）")
    private Long layoutId;

    @Schema(description = "数据类型编码；无 layoutId 时用其 dataLayoutId")
    private String entityTypeCode;

    @Schema(description = "数据 Tab 布局列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "layouts 不能为空")
    @Valid
    private List<DmDataTabLayoutSaveItemVO> layouts;
}
