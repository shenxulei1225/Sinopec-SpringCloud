package cn.cheers.x.device.protocolgateway.datacollection;

import java.util.List;

/**
 * 按操作码取主包字段说明的结果。
 * <p>对不上、读不到、同一操作码多份不同说明书，都带原因，不猜一份。
 */
public record CatalogLookup(List<FieldDescriptionRow> rows, String error, String forwardTopic) {

    public CatalogLookup(List<FieldDescriptionRow> rows, String error) {
        this(rows, error, "");
    }

    public static CatalogLookup found(List<FieldDescriptionRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return failed("该指令没有主包字段说明");
        }
        return new CatalogLookup(List.copyOf(rows), null, "");
    }

    public static CatalogLookup failed(String error) {
        return new CatalogLookup(List.of(), error, "");
    }

    /** 同一份说明书上写了转发到哪个标准事件。 */
    public CatalogLookup withForwardTopic(String topic) {
        String code = topic == null ? "" : topic.trim();
        return new CatalogLookup(rows, error, code);
    }

    public boolean ok() {
        return error == null && !rows.isEmpty();
    }
}
