package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * FactoryTalk Linx Gateway 的这一条连接。
 * 读点订在这条会话上。已经对上的写点也走这条会话，不另开一条。
 * 不把采样写进设备行。
 */
@ConfigurationProperties(prefix = "point-gateway")
public class GatewaySubscriptionProperties {

    /** 关掉之后不连 Gateway，历史里也不会出现新采样，控制也发不出去。 */
    private boolean enabled = true;

    /** Gateway 这一台 OPC UA 服务器。不是 PLC 身上的 4840。 */
    private String serverUrl = "opc.tcp://192.168.10.16:4990/FactoryTalkLinxGateway1/";

    /**
     * 哪些租户的点位对照挂在这台 Gateway 上。
     * 空着表示现有的点位对照都在这台上。巡检的点若在另一台网关，这里只留管廊的租户编号。
     */
    private List<Long> tenantIds = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public boolean hasServerUrl() {
        return serverUrl != null && !serverUrl.isBlank();
    }

    public List<Long> getTenantIds() {
        return tenantIds;
    }

    public void setTenantIds(List<Long> tenantIds) {
        this.tenantIds = tenantIds == null ? new ArrayList<>() : tenantIds;
    }
}
