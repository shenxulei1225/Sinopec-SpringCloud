package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "页面 → 布局引用")
@Data
public class DmPageLayoutRefRespVO {

    private Long id;
    private String pageKey;
    private Long layoutId;
    private Long dataLayoutId;
}
