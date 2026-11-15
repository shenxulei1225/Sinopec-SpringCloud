package cn.cheers.x.device.protocolgateway.protocol.processor;

import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassUplinkApi;
import cn.cheers.x.device.protocolgateway.vehicle.VehicleHttpsCommandService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 登记车辆进出这一条上报入口。
 * <p>负责：识别到车、已经进出交给车辆进出。
 * <p>不负责：机器人、无人机的巡检上报。
 */
@Configuration
public class VehiclePassUplinkProcessorConfiguration {

    @Bean
    UplinkProcessor vehiclePassUplinkProcessor(
            VehiclePassUplinkApi vehiclePassUplinkApi,
            VehicleHttpsCommandService commands) {
        return new VehiclePassUplinkProcessor(vehiclePassUplinkApi, commands);
    }
}
