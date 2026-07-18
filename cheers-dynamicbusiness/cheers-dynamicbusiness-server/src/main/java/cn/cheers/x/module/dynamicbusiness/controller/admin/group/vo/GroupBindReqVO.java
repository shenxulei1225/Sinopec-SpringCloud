package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 分组绑定 Request VO")
public class GroupBindReqVO {

    @NotBlank
    private String groupType;

    @NotNull
    private Long groupId;

    @NotNull
    private Long targetId;
}
