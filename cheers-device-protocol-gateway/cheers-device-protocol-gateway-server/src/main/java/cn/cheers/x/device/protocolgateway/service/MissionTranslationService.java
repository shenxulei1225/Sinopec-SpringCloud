package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.protocol.adapter.ProtocolAdapterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 执行意图翻译：按对接协议编码选适配器，动态生成指令包。
 * <p>不读巡检库；不按业务设备类型分支。
 */
@Service
@RequiredArgsConstructor
public class MissionTranslationService {

    private final ProtocolAdapterRegistry protocolAdapterRegistry;

    public CommandSendEnvelope translate(DeviceMissionPlan plan) {
        if (plan.protocolCode() == null || plan.protocolCode().isBlank()) {
            throw new IllegalArgumentException("protocolCode 必填（对接协议编码）");
        }
        return protocolAdapterRegistry.require(plan.protocolCode()).translate(plan);
    }
}
