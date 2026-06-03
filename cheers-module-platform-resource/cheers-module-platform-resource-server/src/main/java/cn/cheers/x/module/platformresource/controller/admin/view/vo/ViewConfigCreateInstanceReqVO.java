package cn.cheers.x.module.platformresource.controller.admin.view.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ViewConfigCreateInstanceReqVO {

    @NotNull(message = "templateId 不能为空")
    private Long templateId;

    /** 不传时沿用模板名称 */
    private String name;

    private String viewCode;
    private String description;

    /** 差量配置，可为 null（暂不覆盖任何配置） */
    private Map<String, Object> configOverride;
}
