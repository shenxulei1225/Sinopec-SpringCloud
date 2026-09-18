package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeCatalog;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;

/**
 * 监控列表上的识别文案：先告诉人这是哪一类包。
 * <p>不负责字段说明翻译。未知操作码写明编号，不猜成某条指令。
 */
public final class ProtocolMonitorIdentity {

    private ProtocolMonitorIdentity() {
    }

    public static String forInbound(Integer opcode) {
        if (opcode == null) {
            return "无法解析";
        }
        if (opcode == TransportOpcode.HEARTBEAT.code()) {
            return "心跳";
        }
        if (opcode == TransportOpcode.ACK.code()) {
            return "回执";
        }
        return describe(opcode);
    }

    public static String forOutbound(Integer opcode) {
        if (opcode == null) {
            return "无法识别";
        }
        if (opcode == TransportOpcode.HEARTBEAT.code()) {
            return "心跳回显";
        }
        if (opcode == TransportOpcode.ACK.code()) {
            return "回执";
        }
        return describe(opcode);
    }

    public static String exchangeModeName(Integer opcode) {
        if (opcode == null) {
            return "";
        }
        return InstructionExchangeCatalog.modeOf(opcode).name();
    }

    public static String describe(int opcode) {
        for (TransportOpcode value : TransportOpcode.values()) {
            if (value.code() == opcode) {
                return value.description();
            }
        }
        return "未知操作码 " + opcode;
    }
}
