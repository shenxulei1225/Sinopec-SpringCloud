package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "数据管理 - 工作台布局设置更新请求")
@Data
public class DmWorkbenchLayoutSettingsUpdateReqVO {

    @Schema(description = "区段配置隐藏；仅 FILTER、OBJECT、WHAT，显式 true 才隐藏")
    private Map<String, Boolean> sectionHidden;
}
