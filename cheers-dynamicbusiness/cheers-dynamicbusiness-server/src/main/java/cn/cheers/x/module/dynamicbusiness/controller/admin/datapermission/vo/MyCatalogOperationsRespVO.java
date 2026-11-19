package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "登录人在一本数据目录上能做的四种操作。只决定按钮，不含数据范围")
@Data
public class MyCatalogOperationsRespVO {

    private Boolean canQuery;
    private Boolean canCreate;
    private Boolean canUpdate;
    private Boolean canDelete;
}
