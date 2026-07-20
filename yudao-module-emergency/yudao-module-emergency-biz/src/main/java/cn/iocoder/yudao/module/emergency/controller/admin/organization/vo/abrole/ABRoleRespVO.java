package cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - A/B角响应")
@Data
public class ABRoleRespVO extends ABRoleBaseVO {

    @Schema(description = "编号", example = "1")
    private Long id;
}
