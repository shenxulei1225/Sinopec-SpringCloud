package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "整份保存可选的管理方式")
@Data
public class ManagementMethodSaveReqVO {

    @Schema(description = "用来管理数据的目录注册编码，顺序就是表格列顺序", example = "[\"facility\",\"pipeline\",\"organization\"]")
    @NotNull(message = "管理方式不能为空，没有就传空列表")
    private List<String> registryCodes;
}
