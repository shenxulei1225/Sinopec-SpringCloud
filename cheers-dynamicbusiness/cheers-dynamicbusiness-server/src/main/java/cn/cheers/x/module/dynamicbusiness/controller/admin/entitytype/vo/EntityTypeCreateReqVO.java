package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 业务类型创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntityTypeCreateReqVO extends EntityTypeBaseVO {

    @Schema(description = "新建表单「数据怎么管」：按哪本目录管理（数据目录注册编码）。不传表示不分组织，公开", example = "facility")
    private String managementTargetRegistryCode;
}
