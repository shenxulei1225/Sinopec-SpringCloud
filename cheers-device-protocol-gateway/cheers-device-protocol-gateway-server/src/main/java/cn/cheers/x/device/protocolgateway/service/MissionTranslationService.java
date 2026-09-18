package cn.cheers.x.device.protocolgateway.service;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.instructiondispatch.InstructionDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 下发翻译入口：转给指令下发功能按对照表填包。
 * <p>不负责：发明头尾动作、按厂商名写死填包。
 */
@Service
@RequiredArgsConstructor
public class MissionTranslationService {

    private final InstructionDispatchService instructionDispatchService;

    public CommandSendEnvelope translate(DeviceMissionPlan plan) {
        return instructionDispatchService.translate(plan);
    }
}
