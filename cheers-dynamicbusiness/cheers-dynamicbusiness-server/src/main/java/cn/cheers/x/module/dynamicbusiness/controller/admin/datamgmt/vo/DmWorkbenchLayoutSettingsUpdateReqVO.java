package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "数据管理 - 工作台布局设置更新请求")
@Data
public class DmWorkbenchLayoutSettingsUpdateReqVO {

    @Schema(description = "区域隐藏；键必须是本布局已有区域编号，显式 true 才隐藏。不经此接口改区域清单。")
    private Map<String, Boolean> sectionHidden;
}
