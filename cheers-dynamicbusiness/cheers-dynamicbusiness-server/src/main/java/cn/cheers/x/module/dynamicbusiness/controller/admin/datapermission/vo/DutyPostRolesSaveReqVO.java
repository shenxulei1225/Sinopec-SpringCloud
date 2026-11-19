package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "职务管理整份保存这个职务绑定的角色。空列表表示这个职务不再绑定角色。")
@Data
public class DutyPostRolesSaveReqVO {

    @NotNull(message = "请选择职务")
    private Long dutyPostId;

    @NotNull(message = "角色列表不能为空，没有就传空列表")
    private List<Long> roleIds;
}
