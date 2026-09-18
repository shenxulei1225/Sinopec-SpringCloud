package cn.cheers.x.device.protocolgateway.api.opcode;

import java.util.Set;

/**
 * 交互方式编码表：与核对文档「方向 + 交互方式」对齐，供通信层分流。
 * <p>负责：按操作码给出交互方式。字段未入库前用本表过渡。
 * <p>不负责：填包、等几秒、读巡检库。
 * <p>禁止：另猜一套默认身份；指令入库后本表须改为读协议指令，不得长期冒充权威写入。
 */
public final class InstructionExchangeCatalog {

    private static final Set<Integer> SYNC_WAIT_RESULT = Set.of(
            500101, 500102, 500104, 500201, 500205,
            500301, 500302, 500303, 500401, 500402
    );
    private static final Set<Integer> ASYNC_RESULT = Set.of(500202);
    private static final Set<Integer> ACTIVE_UPLINK = Set.of(
            500103, 500203, 500204, 500403
    );

    private InstructionExchangeCatalog() {
    }

    /**
     * 按操作码给出交互方式。
     * <p>未入表的编号按主动上报处理（回执后进总线），不当同步等待。
     */
    public static InstructionExchangeMode modeOf(int opcode) {
        if (opcode == TransportOpcode.HEARTBEAT.code()) {
            return InstructionExchangeMode.HEARTBEAT;
        }
        if (opcode == TransportOpcode.ACK.code()) {
            return InstructionExchangeMode.ACK;
        }
        if (SYNC_WAIT_RESULT.contains(opcode)) {
            return InstructionExchangeMode.SYNC_WAIT_RESULT;
        }
        if (ASYNC_RESULT.contains(opcode)) {
            return InstructionExchangeMode.ASYNC_RESULT;
        }
        if (ACTIVE_UPLINK.contains(opcode)) {
            return InstructionExchangeMode.ACTIVE_UPLINK;
        }
        return InstructionExchangeMode.ACTIVE_UPLINK;
    }

    public static boolean isSyncWaitResult(int opcode) {
        return modeOf(opcode) == InstructionExchangeMode.SYNC_WAIT_RESULT;
    }
}
