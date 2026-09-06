package cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "数据管理 - 工作台布局设置响应")
@Data
public class DmWorkbenchLayoutSettingsRespVO {

    @Schema(description = "本份页面布局上的区域清单（创建时按模板写入）；读路径不补默认三块")
    private List<DmLayoutSectionVO> sections;

    @Schema(description = "区域隐藏；键为本布局区域编号，显式 true 才隐藏")
    private Map<String, Boolean> sectionHidden;
}
