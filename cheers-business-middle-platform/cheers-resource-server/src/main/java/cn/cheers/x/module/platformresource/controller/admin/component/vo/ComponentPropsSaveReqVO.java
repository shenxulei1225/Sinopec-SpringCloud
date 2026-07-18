package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 保存组件配置（模板 props / 实例 propsOverride）")
@Data
public class ComponentPropsSaveReqVO {

    private Map<String, Object> props;
    private Map<String, Object> propsOverride;
    private ComponentDataSourceVO dataSource;

    private String name;
    private Integer status;
    private Integer sort;
    private String description;
    private String schemaVersion;
}
