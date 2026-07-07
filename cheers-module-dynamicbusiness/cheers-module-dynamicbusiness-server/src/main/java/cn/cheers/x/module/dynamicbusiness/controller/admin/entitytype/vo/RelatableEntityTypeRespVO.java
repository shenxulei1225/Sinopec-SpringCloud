package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 可关联业务类型 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatableBusinessTypeRespVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String code;

    @Schema(description = "业务类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备管理")
    private String name;

}
