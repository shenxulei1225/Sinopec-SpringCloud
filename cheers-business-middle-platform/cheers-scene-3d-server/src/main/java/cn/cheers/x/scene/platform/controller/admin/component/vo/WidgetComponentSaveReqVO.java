package cn.cheers.x.scene.platform.controller.admin.component.vo;

import cn.cheers.x.scene.platform.dal.dataobject.component.WidgetComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Widget 组件保存请求 VO")
@Data
public class WidgetComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String widgetCode;

    private String uiConfigJson;

    private Boolean drawAtDesiredSize;

    private Boolean receiveHardwareInput;

    private String metadataJson;

    public WidgetComponentDO toDO() {
        WidgetComponentDO item = new WidgetComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setWidgetCode(widgetCode);
        item.setUiConfigJson(uiConfigJson);
        item.setDrawAtDesiredSize(drawAtDesiredSize);
        item.setReceiveHardwareInput(receiveHardwareInput);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
