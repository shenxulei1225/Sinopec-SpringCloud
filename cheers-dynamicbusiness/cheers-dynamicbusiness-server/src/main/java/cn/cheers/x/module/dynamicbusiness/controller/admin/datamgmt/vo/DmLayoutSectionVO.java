package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "某一数据目录的页面布局上的一块区域（创建时按模板写入）")
@Data
public class DmLayoutSectionVO {

    @Schema(description = "区域编号；栏上 columnSection 存这个值")
    private String id;

    @Schema(description = "区域名称；本份布局写入的数据，不是系统固定三块")
    private String name;

    @Schema(description = "摆法：horizontal=水平并排画栏；free=自由摆放（现网暂用固定详情模版）。创建时写入，读路径不猜")
    private String arrange;
}
