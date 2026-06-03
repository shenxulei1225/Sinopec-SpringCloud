package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "组件读/写 HTTP 端点（已投影，不含无关 endpoint）")
public class ComponentCapabilityEndpointRespVO {

    @Schema(description = "端点 id")
    private String id;

    @Schema(description = "用途：list / tree / search / create / update / delete")
    private String purpose;

    @Schema(description = "请求 URL")
    private String url;

    @Schema(description = "HTTP 方法")
    private String method;

    @Schema(description = "参数风格：page-req / plain / json-body / query-param 等")
    private String paramStyle;

    @Schema(description = "默认 query/body 参数")
    private Map<String, Object> defaultParams;

    @Schema(description = "响应映射：listPath / totalPath / idField / labelField")
    private Map<String, Object> responseMapping;

    @Schema(description = "激活条件")
    private String activateWhen;
}
