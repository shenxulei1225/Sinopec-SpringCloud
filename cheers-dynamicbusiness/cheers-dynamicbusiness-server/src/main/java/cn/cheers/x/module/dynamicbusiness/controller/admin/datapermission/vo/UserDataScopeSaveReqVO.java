package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "整份保存一个人的数据范围。空列表表示清空。")
@Data
public class UserDataScopeSaveReqVO {

    @NotNull(message = "请选择人员")
    private Long userId;

    @Valid
    private List<Unit> units;

    @Data
    public static class Unit {

        @NotNull(message = "请选择管理单元目录")
        private String registryCode;

        @NotNull(message = "请选择管理单元")
        private Long unitId;
    }
}
