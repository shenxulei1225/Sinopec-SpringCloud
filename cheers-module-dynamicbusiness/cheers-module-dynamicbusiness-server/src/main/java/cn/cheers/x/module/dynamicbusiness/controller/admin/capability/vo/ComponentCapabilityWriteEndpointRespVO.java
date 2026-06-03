package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "写操作端点 + 请求体字段 schema")
public class ComponentCapabilityWriteEndpointRespVO {

    private String url;
    private String method;
    private String paramStyle;

    @Schema(description = "请求体字段（含 required、validation、optionsSource 等）")
    private List<Map<String, Object>> fields;
}
