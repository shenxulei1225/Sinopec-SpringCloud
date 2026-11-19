package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "给一个职务分配一批人。不带管理单元。")
@Data
public class DutyPostUserCreateReqVO {

    @NotNull(message = "请选择职务")
    private Long dutyPostId;

    @NotEmpty(message = "请选择人员")
    private List<Long> userIds;
}
