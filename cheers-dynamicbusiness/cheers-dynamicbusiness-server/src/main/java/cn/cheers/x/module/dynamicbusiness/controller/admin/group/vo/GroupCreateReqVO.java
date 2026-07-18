package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 通用分组创建 Request VO")
public class GroupCreateReqVO extends GroupBaseVO {

    @Schema(description = "编码前缀", example = "FG")
    private String codePrefix;
}
