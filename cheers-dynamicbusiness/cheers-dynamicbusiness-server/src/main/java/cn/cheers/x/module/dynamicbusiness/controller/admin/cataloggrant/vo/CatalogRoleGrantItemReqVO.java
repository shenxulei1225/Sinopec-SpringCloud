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

    @Schema(description = "对象：category 分类，model 模型，entity 实体，detail 详情", example = "entity")
    @NotBlank(message = "请选择分类、模型、实体或详情")
    private String subject;

    @Schema(description = "操作：query 查看；create、update、delete 是角色页上的一项维护")
    private List<String> operations;
}
