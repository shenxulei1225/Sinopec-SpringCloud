package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "组件读能力：单读端点 + 筛选 + 展示字段")
public class ComponentCapabilityReadRespVO {

    @Schema(description = "list/page 或 tree 读端点")
    private ComponentCapabilityEndpointRespVO endpoint;

    @Schema(description = "筛选字段")
    private List<Map<String, Object>> filters;

    @Schema(description = "展示字段")
    private List<Map<String, Object>> displayFields;
}
