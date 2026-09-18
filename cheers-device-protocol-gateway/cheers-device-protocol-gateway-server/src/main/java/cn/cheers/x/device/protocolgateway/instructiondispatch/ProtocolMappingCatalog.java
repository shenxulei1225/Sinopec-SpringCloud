package cn.cheers.x.device.protocolgateway.instructiondispatch;

import java.util.Optional;

/**
 * 指令协议对接目录。
 * <p>负责：按动作 + 协议版本取对照，再取该指令空包。
 * <p>不负责：填包、编开跑顺序。
 * <p>禁止：没有对照时猜一条指令。
 */
public interface ProtocolMappingCatalog {

    Optional<MappingRow> find(long actionId, String protocolVersion);

    InstructionTemplate loadInstruction(long instructionId);
}
