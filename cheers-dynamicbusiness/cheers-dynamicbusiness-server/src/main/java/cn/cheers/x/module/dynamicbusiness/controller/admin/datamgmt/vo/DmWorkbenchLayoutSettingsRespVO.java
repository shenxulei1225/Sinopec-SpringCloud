package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "数据管理 - 工作台布局设置响应")
@Data
public class DmWorkbenchLayoutSettingsRespVO {

    @Schema(description = "区段配置隐藏；缺键或非 true 均表示不隐藏")
    private Map<String, Boolean> sectionHidden;
}
