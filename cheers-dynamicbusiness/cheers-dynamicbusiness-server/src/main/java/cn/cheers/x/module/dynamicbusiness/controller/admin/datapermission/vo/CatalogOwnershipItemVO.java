package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "一种管理方式：按哪本目录管理")
@Data
public class CatalogOwnershipItemVO {

    @Schema(description = "按哪本目录管理（数据目录注册编码）", example = "facility")
    @NotBlank(message = "请选择按哪本目录管理")
    private String targetRegistryCode;

    @Schema(description = "目录上有几个字段都指向这本目录时，用户挑的那一个；其余情况不传", example = "facility_id")
    private String fieldCode;
}
