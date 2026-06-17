package cn.cheers.x.module.platformresource.controller.admin.component.vo;

import cn.cheers.x.module.platformresource.service.component.ComponentDataSource;
import lombok.Data;

@Data
public class ComponentPropsListReqVO {

    private String componentCode;
    private Boolean isTemplate;
    private Boolean onlyEnabled;
    private ComponentDataSourceVO dataSource;

    public ComponentDataSource.Normalized resolveDataSourceFilter() {
        return ComponentDataSource.fromVo(dataSource);
    }
}
