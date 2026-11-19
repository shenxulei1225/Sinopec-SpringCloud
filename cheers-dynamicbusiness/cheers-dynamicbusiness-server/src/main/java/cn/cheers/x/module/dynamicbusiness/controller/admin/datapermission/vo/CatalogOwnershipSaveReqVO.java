package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "保存或预览一本数据目录的管理方式（整份替换）")
@Data
public class CatalogOwnershipSaveReqVO {

    @Schema(description = "数据目录注册编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择数据目录")
    private String registryCode;

    @Schema(description = "按哪些目录管理；空列表表示不分组织，公开")
    @NotNull(message = "管理方式不能为空，不分组织就传空列表")
    private List<@Valid CatalogOwnershipItemVO> targets;
}
