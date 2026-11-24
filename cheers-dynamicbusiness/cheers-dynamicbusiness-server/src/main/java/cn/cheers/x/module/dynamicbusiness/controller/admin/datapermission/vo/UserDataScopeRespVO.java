package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "一个人数据范围里的一个管理单元")
@Data
public class UserDataScopeRespVO {

    private Long id;
    private Long userId;
    private String registryCode;
    private Long unitId;
    private String unitName;
}
