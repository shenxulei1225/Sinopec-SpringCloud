package cn.cheers.x.device.protocolgateway.djicloud;

/**
 * 上云绑定用的组织。编号和名称都有才允许回答「已绑定」。
 */
public record DjiCloudOrganization(String id, String name) {

    public static DjiCloudOrganization none() {
        return new DjiCloudOrganization("", "");
    }

    public boolean complete() {
        return id != null && !id.isBlank() && name != null && !name.isBlank();
    }
}
