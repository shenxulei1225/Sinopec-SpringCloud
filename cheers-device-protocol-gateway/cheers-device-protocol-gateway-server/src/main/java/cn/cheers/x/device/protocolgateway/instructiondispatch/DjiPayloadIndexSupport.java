package cn.cheers.x.device.protocolgateway.instructiondispatch;

/**
 * 大疆负载索引：类型-子类型-挂载点。
 */
final class DjiPayloadIndexSupport {

    private DjiPayloadIndexSupport() {
    }

    static String format(Object mainType, Object subType, int mountPoint) {
        String type = lastNumberSegment(mainType);
        String sub = lastNumberSegment(subType);
        if (type == null || sub == null || mountPoint < 0) {
            return null;
        }
        return type + "-" + sub + "-" + mountPoint;
    }

    static String lastNumberSegment(Object raw) {
        if (raw == null) {
            return null;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty()) {
            return null;
        }
        String segment = text;
        int colon = text.lastIndexOf(':');
        if (colon >= 0 && colon < text.length() - 1) {
            segment = text.substring(colon + 1).trim();
        }
        return segment.matches("\\d+") ? segment : null;
    }
}
