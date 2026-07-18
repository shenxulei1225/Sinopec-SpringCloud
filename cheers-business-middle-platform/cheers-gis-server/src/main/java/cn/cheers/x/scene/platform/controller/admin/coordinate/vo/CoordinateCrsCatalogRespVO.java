package cn.cheers.x.scene.platform.controller.admin.coordinate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 常用坐标系目录响应 VO")
@Data
public class CoordinateCrsCatalogRespVO {

    @Schema(description = "坐标系代码")
    private String crsCode;

    @Schema(description = "坐标系名称")
    private String crsName;

    @Schema(description = "坐标系类型")
    private String crsType;

    @Schema(description = "EPSG 代码")
    private String epsgCode;

    @Schema(description = "启用状态")
    private Boolean enabledFlag;

    @Schema(description = "警告信息")
    private String warningMessage;
}
