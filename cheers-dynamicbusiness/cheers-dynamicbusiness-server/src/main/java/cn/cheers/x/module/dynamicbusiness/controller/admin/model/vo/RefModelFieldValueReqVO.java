package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 读取引用型号字段要点名的取值。
 *
 * sourceModelId 有值时读这个型号。
 * 空着时用 host 这台设备主云台当前挂着的那台设备的型号。
 */
@Schema(description = "管理后台 - 引用型号字段取值请求")
@Data
public class RefModelFieldValueReqVO {

    @Schema(description = "被读型号所在的数据目录", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "请选择数据目录")
    private String entityTypeCode;

    @Schema(description = "要读的型号。空表示用主云台当前挂着的设备的型号")
    private Long sourceModelId;

    @Schema(description = "要点名的字段，顺序就是返回顺序。空着时只返回型号名")
    private List<Long> sourceFieldIds;

    @Schema(description = "当前打开的这台设备。型号留空时用它找主云台")
    private Long hostEntityId;

    @Schema(description = "当前打开的这台设备所在的数据目录")
    private String hostEntityTypeCode;
}
