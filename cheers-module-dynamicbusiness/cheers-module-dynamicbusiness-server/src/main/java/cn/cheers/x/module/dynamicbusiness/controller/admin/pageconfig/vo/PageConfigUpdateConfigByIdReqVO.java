package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 按页面配置ID更新配置内容 Request VO")
@Data
public class PageConfigUpdateConfigByIdReqVO {

    @Schema(description = "页面配置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "页面配置ID不能为空")
    private Long id;

    @Schema(description = "配置内容(JSON)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "配置内容不能为空")
    private Map<String, Object> config;
}

