package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "当前登录人在一个数据目录上的场站范围和四项操作")
@Data
public class CatalogMyStationScopeRespVO {

    @Schema(description = "查看范围")
    private String viewScope;

    @Schema(description = "写入范围")
    private String writeScope;

    @Schema(description = "能否查看")
    private Boolean canQuery;

    @Schema(description = "能否创建")
    private Boolean canCreate;

    @Schema(description = "能否修改")
    private Boolean canUpdate;

    @Schema(description = "能否删除")
    private Boolean canDelete;

    @Schema(description = "这条目录上指向场站的字段。空表示记录不按站分开")
    private List<String> stationFieldCodes;
}
