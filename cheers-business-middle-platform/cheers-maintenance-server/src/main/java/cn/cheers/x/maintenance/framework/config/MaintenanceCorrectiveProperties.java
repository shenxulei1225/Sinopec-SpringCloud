package cn.cheers.x.maintenance.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "maintenance.corrective")
public class MaintenanceCorrectiveProperties {
    /** true：跳过 Zeebe，submitApproval 直接 APPROVED */
    private boolean skipApproval = true;
    private String bpmnProcessId = "corrective-approval";
}
