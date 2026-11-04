package cn.cheers.x.module.dynamicbusiness.service.linkage;

/**
 * 联动主记录上的执行方式。
 *
 * <p>空着或自动执行：事件一到就提交动作。</p>
 * <p>人工确认后执行：先记下等人确认，不立刻提交动作。</p>
 * <p>禁止：按中文名猜其它取值。</p>
 */
final class LinkageExecutionMode {

    private LinkageExecutionMode() {
    }

    static boolean waitForConfirm(Object raw) {
        if (raw == null) {
            return false;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return false;
        }
        if (raw instanceof java.util.Map<?, ?> map && map.containsKey("value")) {
            return waitForConfirm(map.get("value"));
        }
        return LinkageFieldCodes.EXECUTION_CONFIRM.equalsIgnoreCase(text)
                || "人工确认后执行".equals(text)
                || "等人确认".equals(text);
    }
}
