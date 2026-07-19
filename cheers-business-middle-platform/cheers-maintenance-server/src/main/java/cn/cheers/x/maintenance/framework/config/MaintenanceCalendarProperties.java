package cn.cheers.x.maintenance.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "maintenance.calendar")
public class MaintenanceCalendarProperties {
    /** 演示用排程模式，缺省则 trigger 报错 */
    private String schedulingMode;
    private String conflictStrategy = "none";
    private String orchestrationRef = "orch.standard_expand_solve_persist_v1";
}
