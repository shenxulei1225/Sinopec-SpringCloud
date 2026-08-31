package cn.cheers.x.device.protocolgateway.protocol.adapter;

import cn.cheers.x.device.protocolgateway.api.message.CommandSendEnvelope;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;

/**
 * 对接协议适配器：把动态执行意图译成该协议的指令包信封。
 * <p>每个实现对应一个对接协议编码；禁止按业务设备类型 if 分支代替注册。
 */
public interface ProtocolAdapter {

    /** 本适配器注册的对接协议编码 */
    String protocolCode();

    /**
     * 将本次执行意图翻译为 500104 信封（packages 为动态生成，非预制包）。
     */
    CommandSendEnvelope translate(DeviceMissionPlan plan);
}
