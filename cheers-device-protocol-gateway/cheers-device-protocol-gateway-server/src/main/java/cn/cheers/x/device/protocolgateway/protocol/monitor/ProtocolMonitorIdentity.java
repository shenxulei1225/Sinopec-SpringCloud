package cn.cheers.x.device.protocolgateway.protocol.monitor;

import cn.cheers.x.device.protocolgateway.api.opcode.InstructionExchangeCatalog;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;

/**
 * 监控列表上的识别文案：先告诉人这是哪一类包。
 * <p>同步指令的下发用指令名。同一操作码的答卷用「的响应」，不再沿用下发那个名字。
 * <p>主动上报仍用上报自己的名字。不负责字段说明翻译。未知操作码写明编号，不猜成某条指令。
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
        if (InstructionExchangeCatalog.isSyncWaitResult(opcode)) {
            return responseName(describe(opcode));
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

    /**
     * 答卷不要再叫下发。名字已以「下发」结尾时去掉这二字，再接「的响应」。
     */
    private static String responseName(String commandName) {
        String base = commandName.endsWith("下发")
                ? commandName.substring(0, commandName.length() - "下发".length())
                : commandName;
        return base + "的响应";
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
