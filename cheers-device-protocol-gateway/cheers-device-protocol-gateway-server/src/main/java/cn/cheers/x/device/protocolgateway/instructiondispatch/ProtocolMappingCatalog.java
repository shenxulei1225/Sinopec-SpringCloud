package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;

import java.util.Optional;

/**
 * 指令协议对接目录。
 * <p>负责：按动作 + 协议版本取对照，再取该指令空包。
 * <p>不负责：填包、编开跑顺序。
 * <p>禁止：没有对照时猜一条指令；同一动作同一版本有多条对照时取第一条。
 */
public interface ProtocolMappingCatalog {

    Optional<MappingRow> find(long actionId, String protocolVersion);

    InstructionTemplate loadInstruction(long instructionId);

    /**
     * 试译/下发报错用人话：动作中文名、动作库编号（code）、内部 id。
     * 测试桩可沿用默认，只带 code 与 id。
     */
    default String describeActionForMessage(DispatchAction action) {
        if (action == null) {
            return "未知动作";
        }
        if (action.actionCode() != null && !action.actionCode().isBlank()) {
            return "「" + action.actionCode().trim() + "」（动作库编号 " + action.actionId() + "）";
        }
        return "动作库编号 " + action.actionId();
    }
}
