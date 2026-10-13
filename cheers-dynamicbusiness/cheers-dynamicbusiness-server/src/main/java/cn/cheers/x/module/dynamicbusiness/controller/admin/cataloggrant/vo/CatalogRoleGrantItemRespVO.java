package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "一个数据目录上已保存的操作")
@Data
public class CatalogRoleGrantItemRespVO {

    @Schema(description = "数据目录注册编码")
    private String registryCode;

    @Schema(description = "已勾选操作")
    private List<String> operations;
}
