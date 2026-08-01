package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据管理 - 模型管理左侧分类栏响应")
@Data
public class DmModelTabCategoryRespVO {

    @Schema(description = "数据类型编码")
    private String entityTypeCode;

    @Schema(description = "是否启用左侧分类栏")
    private Boolean enabled;

    @Schema(description = "栏标题")
    private String label;

    @Schema(description = "分类种类编码")
    private String categoryTypeCode;

    @Schema(description = "分类树 propsId")
    private Long propsId;

    @Schema(description = "模型管理 Tab 型号列表 propsId（与数据 Tab 型号列分离）")
    private Long modelListPropsId;
}
