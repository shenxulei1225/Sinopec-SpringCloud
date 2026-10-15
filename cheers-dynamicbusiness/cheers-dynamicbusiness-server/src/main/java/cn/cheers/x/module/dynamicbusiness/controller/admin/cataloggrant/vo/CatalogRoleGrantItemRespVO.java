package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "一个数据目录上已保存的操作")
@Data
public class CatalogRoleGrantItemRespVO {

    @Schema(description = "数据目录注册编码")
    private String registryCode;

    @Schema(description = "对象：category 分类，model 模型，entity 实体，detail 详情")
    private String subject;

    @Schema(description = "已勾选操作。create、update、delete 同时存在时，角色页显示为一项维护")
    private List<String> operations;
}
