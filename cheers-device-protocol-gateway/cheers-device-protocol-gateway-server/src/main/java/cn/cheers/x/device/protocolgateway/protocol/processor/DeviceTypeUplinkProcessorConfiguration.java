package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.device.protocolgateway.datacollection.DeviceProtocolVersionCatalog;
import cn.cheers.x.inspection.task.api.InspectionDeviceUplinkApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 按协议版本各登记一个处理入口。
 * <p>负责：机器人、东古无人机、大疆机场、大疆 Pilot 各收各的上报。
 * <p>不负责：把别的协议书里的操作码送进其中一个入口。
 */
@Configuration
public class DeviceTypeUplinkProcessorConfiguration {

    static final String ROBOT = "robot-ws";
    static final String DONG_GU_UAV = "uav-ws";
    static final String DJI_DOCK = "dji-dock-1.16.1";
    static final String DJI_PILOT = "dji-pilot-1.16.1";

    @Bean
    UplinkProcessor robotUplinkProcessor(
            DeviceProtocolVersionCatalog deviceProtocolVersions,
            InspectionDeviceUplinkApi inspectionDeviceUplinkApi
    ) {
        return new DeviceTypeUplinkProcessor(ROBOT, deviceProtocolVersions, inspectionDeviceUplinkApi);
    }

    @Bean
    UplinkProcessor dongGuUavUplinkProcessor(
            DeviceProtocolVersionCatalog deviceProtocolVersions,
            InspectionDeviceUplinkApi inspectionDeviceUplinkApi
    ) {
        return new DeviceTypeUplinkProcessor(DONG_GU_UAV, deviceProtocolVersions, inspectionDeviceUplinkApi);
    }

    @Bean
    UplinkProcessor djiDockUplinkProcessor(
            DeviceProtocolVersionCatalog deviceProtocolVersions,
            InspectionDeviceUplinkApi inspectionDeviceUplinkApi
    ) {
        return new DeviceTypeUplinkProcessor(DJI_DOCK, deviceProtocolVersions, inspectionDeviceUplinkApi);
    }

    @Bean
    UplinkProcessor djiPilotUplinkProcessor(
            DeviceProtocolVersionCatalog deviceProtocolVersions,
            InspectionDeviceUplinkApi inspectionDeviceUplinkApi
    ) {
        return new DeviceTypeUplinkProcessor(DJI_PILOT, deviceProtocolVersions, inspectionDeviceUplinkApi);
    }
}
