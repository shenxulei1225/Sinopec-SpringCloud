package cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 页面配置草稿创建 Request VO")
@Data
public class PageConfigDraftCreateReqVO {

    @Schema(description = "配置代码（唯一标识），为空时后端自动生成", example = "equipment-shebei-guanli")
    private String configCode;

    @Schema(description = "页面代码（唯一标识），用于页面加载", example = "region-management-default")
    private String pageCode;

    @Schema(description = "页面类型：data_management-数据管理, dashboard-驾驶舱, statistics-统计, monitor-实时监控",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "data_management")
    @NotNull(message = "页面类型不能为空")
    private String pageType;

    @Schema(description = "配置内容(JSON)，结构取决于page_type", example = "{\"pattern\": \"B\", \"leftTreeType\": \"category-model\"}")
    private Map<String, Object> config;
}

