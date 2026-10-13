package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Schema(description = "一个数据目录上勾选的操作")
@Data
public class CatalogRoleGrantItemReqVO {

    @Schema(description = "数据目录注册编码", example = "equipment")
    @NotBlank(message = "请选择数据目录")
    private String registryCode;

    @Schema(description = "操作：query 查看，create 新增，update 修改，delete 删除")
    private List<String> operations;
}
