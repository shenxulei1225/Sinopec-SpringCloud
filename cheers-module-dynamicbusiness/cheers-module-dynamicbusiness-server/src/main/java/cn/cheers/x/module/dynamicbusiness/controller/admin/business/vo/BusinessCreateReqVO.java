package cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 门户业务创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessCreateReqVO extends BusinessBaseVO {
}
