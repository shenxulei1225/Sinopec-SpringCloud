package cn.cheers.x.device.protocolgateway.datacollection;

import java.util.List;

/**
 * 按操作码取主包字段说明的结果。
 * <p>对不上、读不到、同一操作码多份不同说明书，都带原因，不猜一份。
 */
public record CatalogLookup(List<FieldDescriptionRow> rows, String error) {

    public static CatalogLookup found(List<FieldDescriptionRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return failed("该指令没有主包字段说明");
        }
        return new CatalogLookup(List.copyOf(rows), null);
    }

    public static CatalogLookup failed(String error) {
        return new CatalogLookup(List.of(), error);
    }

    public boolean ok() {
        return error == null && !rows.isEmpty();
    }
}
