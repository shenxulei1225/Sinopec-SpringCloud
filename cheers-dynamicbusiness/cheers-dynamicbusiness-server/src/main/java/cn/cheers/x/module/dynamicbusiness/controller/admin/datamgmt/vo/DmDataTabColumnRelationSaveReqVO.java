package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Schema(description = "数据管理 - 批量保存栏间关系声明")
@Data
public class DmDataTabColumnRelationSaveReqVO {

    @Schema(description = "工作台布局实例 id（优先）")
    private Long layoutId;

    @Schema(description = "目录编码；无 layoutId 时用其 dataLayoutId")
    private String entityTypeCode;

    @Schema(description = "本页全部栏间关系；空列表表示清空")
    @Valid
    private List<DmDataTabColumnRelationSaveItemVO> relations;
}
