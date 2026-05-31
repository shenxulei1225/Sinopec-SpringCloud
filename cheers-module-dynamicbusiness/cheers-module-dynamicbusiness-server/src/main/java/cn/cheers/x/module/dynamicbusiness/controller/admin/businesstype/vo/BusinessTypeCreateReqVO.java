package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 业务类型创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessTypeCreateReqVO extends BusinessTypeBaseVO {
}
