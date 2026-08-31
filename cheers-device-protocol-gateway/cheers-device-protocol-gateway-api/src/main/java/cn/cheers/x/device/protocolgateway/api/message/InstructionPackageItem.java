package cn.cheers.x.device.protocolgateway.api.message;

import java.util.Map;

/**
 * 指令包内单条动作（写入 packages 数组的元素）。
 * <p>{@code request} 为该动作参数对象，序列化进信封前由对接层组装。
 *
 * @param opcode   内层动作码
 * @param sequence 包内序号，从 0 递增
 * @param request  动作参数（如 pointId/lat/lng/heading）
 */
public record InstructionPackageItem(
        int opcode,
        int sequence,
        Map<String, Object> request
) {
}
