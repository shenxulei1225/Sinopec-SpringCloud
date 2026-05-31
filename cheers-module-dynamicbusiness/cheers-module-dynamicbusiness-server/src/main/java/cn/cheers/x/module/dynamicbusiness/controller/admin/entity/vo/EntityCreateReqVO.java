package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 实体创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityCreateReqVO extends EntityBaseVO {
}

