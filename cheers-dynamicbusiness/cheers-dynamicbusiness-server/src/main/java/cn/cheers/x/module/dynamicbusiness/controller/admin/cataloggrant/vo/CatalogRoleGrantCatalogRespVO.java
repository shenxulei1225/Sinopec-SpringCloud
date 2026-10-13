package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色页右侧的一行数据目录。
 * 只有注册编码和名称，用来勾选查看、新增、修改、删除。
 */
@Schema(description = "角色页上的数据目录")
@Data
public class CatalogRoleGrantCatalogRespVO {

    @Schema(description = "数据目录注册编码")
    private String code;

    @Schema(description = "数据目录名称")
    private String name;
}
