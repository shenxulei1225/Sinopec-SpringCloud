package cn.cheers.x.device.protocolgateway.djicloud;

/**
 * 上云许可。三项都有才允许回给机场或遥控器。
 */
public record DjiCloudCredentials(String appId, String appKey, String appLicense,
                                  String ntpServerHost, int ntpServerPort) {

    public boolean licenseComplete() {
        return notBlank(appId) && notBlank(appKey) && notBlank(appLicense);
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
