package cn.cheers.x.device.protocolgateway.api.dto;

/**
 * 监控页模拟下发：指定设备和指令。报文由服务端按空包生成。
 * <p>启动或关闭定时上报时，开关、频率和重复次数都由页面选定。没选开关时，服务端不得把空包里的占位 false 当成关闭发出。
 * <p>点了已生成任务时，任务指令和启动推流用这一条上已经生成的编号、时刻、指令包和收流地址。页面没点任务时，启动推流仍可用手填地址。
 * <p>启动、下发指令包、取包、删包可以另带模板编号。带了就写入这次下发包的模板编号。没带时，启动和删包用这条任务指令包上记下的模板编号。
 * <p>终止任务可以另带任务编号。带了就写入这次下发包的任务编号。没带时用选中的执行记录编号。
 *
 * @param deviceId  当前选中的逻辑设备标识
 * @param opcode    要下发的操作码
 * @param enable    定时上报开关。只有 500102 使用；true 启动，false 关闭
 * @param frequency 上报频率，单位秒。启动时必须大于 0
 * @param total     重复次数。正整数为次数，-1 表示不限次数
 * @param streamUrl           启动推流要写入的地址。页面选了任务时可以不填，改用那条任务上的收流地址
 * @param executionRecordId   监控页点中的已生成任务。任务指令和启动推流用这一条，不再另挑
 * @param templateId          页面手填的模板编号。非空时替换这次下发包里的模板编号
 * @param taskId              页面手填的任务编号。终止任务非空时替换这次下发包里的任务编号
 */
public record ProtocolMonitorDownlinkRequest(
        String deviceId,
        int opcode,
        Boolean enable,
        Integer frequency,
        Integer total,
        String streamUrl,
        Long executionRecordId,
        String templateId,
        String taskId
) {
}
