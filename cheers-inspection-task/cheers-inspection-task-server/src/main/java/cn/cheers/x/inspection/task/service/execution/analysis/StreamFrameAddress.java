package cn.cheers.x.inspection.task.service.execution.analysis;

/**
 * 把流媒体网关的抽帧地址模板和流名称拼成一张当前画面的地址。
 * <p>只拼地址。不连接网关，不调用分析算法，不写执行步骤。
 */
public final class StreamFrameAddress {

    private StreamFrameAddress() {
    }

    /**
     * 模板里的 {streamName} 换成这次收流的名称。缺模板或流名称时返回 null。
     */
    public static String jpegUrl(String template, String streamName) {
        if (template == null || template.isBlank() || streamName == null || streamName.isBlank()) {
            return null;
        }
        if (!template.contains("{streamName}")) {
            return null;
        }
        return template.replace("{streamName}", streamName.trim());
    }
}
