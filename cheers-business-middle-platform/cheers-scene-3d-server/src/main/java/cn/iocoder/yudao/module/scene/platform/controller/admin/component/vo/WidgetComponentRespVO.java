package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Widget 组件响应 VO")
@Data
public class WidgetComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "Widget 编码")
    private String widgetCode;

    @Schema(description = "UI 配置 JSON")
    private String uiConfigJson;

    @Schema(description = "按期望尺寸绘制")
    private Boolean drawAtDesiredSize;

    @Schema(description = "接收硬件输入")
    private Boolean receiveHardwareInput;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
