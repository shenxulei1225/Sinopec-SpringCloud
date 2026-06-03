package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "List 等组件可选的 CRUD 写端点")
public class ComponentCapabilityWriteRespVO {

    private ComponentCapabilityWriteEndpointRespVO create;
    private ComponentCapabilityWriteEndpointRespVO update;
    private ComponentCapabilityWriteEndpointRespVO delete;

    @Schema(description = "异步校验规则（唯一性等），与 requestBody.fields.asyncCheckId 关联")
    private List<Map<String, Object>> asyncChecks;
}
