package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "数据管理 - 数据 Tab 布局与栏间关系合并响应")
@Data
public class DmDataTabLayoutBundleRespVO {

    @Schema(description = "栏布局行")
    private List<DmDataTabLayoutRespVO> layouts;

    @Schema(description = "栏间关系声明")
    private List<DmDataTabColumnRelationRespVO> columnRelations;
}
