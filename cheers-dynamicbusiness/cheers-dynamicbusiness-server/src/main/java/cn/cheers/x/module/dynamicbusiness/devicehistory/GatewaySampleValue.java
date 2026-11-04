package cn.cheers.x.module.dynamicbusiness.devicehistory;

/**
 * 把 Gateway 送来的值收成历史库要的工程值和质量。
 * 不管节点对不对。没有数就不给工程值，禁止写成 0。
 */
public final class GatewaySampleValue {

    private GatewaySampleValue() {
    }

    /**
     * 布尔量记成 1 或 0。数值按原数。文字能看成数也按原数。其余返回空。
     */
    public static Double engineering(Object raw) {
        if (raw instanceof Boolean bit) {
            return bit ? 1.0d : 0.0d;
        }
        if (raw instanceof Number number) {
            double value = number.doubleValue();
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return null;
            }
            return value;
        }
        if (raw instanceof String text) {
            return parse(text.trim());
        }
        return null;
    }

    private static Double parse(String text) {
        if (text.isEmpty()) {
            return null;
        }
        try {
            double value = Double.parseDouble(text);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return null;
            }
            return value;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
