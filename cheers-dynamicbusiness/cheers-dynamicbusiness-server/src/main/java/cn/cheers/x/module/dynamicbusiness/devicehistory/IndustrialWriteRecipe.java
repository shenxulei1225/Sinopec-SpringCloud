package cn.cheers.x.module.dynamicbusiness.devicehistory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 一条设备动作要写成哪些型号字段、写成开还是关。
 * 写在动作的 attrs.industrialEquipment 里。不存节点标识，也不存协议报文。
 * 没有这一段时返回 null，调用方拒绝下发。
 */
public record IndustrialWriteRecipe(List<String> equipmentModelCodes, List<Write> writes) {

    /** 这台设备的型号在这条动作记下的型号里。 */
    public boolean appliesTo(String modelCode) {
        return modelCode != null && equipmentModelCodes.contains(modelCode);
    }

    private static final ObjectMapper JSON = new ObjectMapper();

    public record Write(String fieldCode, int value) {
    }

    public static IndustrialWriteRecipe parse(String attrsJson) {
        JsonNode book = book(attrsJson);
        if (book == null) {
            return null;
        }
        List<String> modelCodes = modelCodes(book);
        JsonNode rows = book.get("writes");
        if (modelCodes.isEmpty() || rows == null || !rows.isArray()) {
            return null;
        }
        if (rows.isEmpty()) {
            return new IndustrialWriteRecipe(modelCodes, List.of());
        }
        List<Write> writes = new ArrayList<>();
        for (JsonNode row : rows) {
            Write write = one(row);
            if (write == null) {
                return null;
            }
            writes.add(write);
        }
        return new IndustrialWriteRecipe(modelCodes, List.copyOf(writes));
    }

    private static List<String> modelCodes(JsonNode book) {
        List<String> codes = new ArrayList<>();
        String one = text(book, "equipmentModelCode");
        if (!one.isBlank()) {
            codes.add(one);
        }
        JsonNode many = book.get("equipmentModelCodes");
        if (many != null && many.isArray()) {
            for (JsonNode code : many) {
                String value = code == null || code.isNull() ? "" : code.asText("").trim();
                if (!value.isBlank() && !codes.contains(value)) {
                    codes.add(value);
                }
            }
        }
        return List.copyOf(codes);
    }

    private static JsonNode book(String attrsJson) {
        if (attrsJson == null || attrsJson.isBlank()) {
            return null;
        }
        try {
            JsonNode root = JSON.readTree(attrsJson);
            JsonNode book = root.get("industrialEquipment");
            return book == null || book.isNull() ? null : book;
        } catch (Exception ex) {
            return null;
        }
    }

    private static Write one(JsonNode row) {
        String fieldCode = text(row, "fieldCode");
        JsonNode value = row.get("value");
        if (fieldCode.isBlank() || value == null || !value.isIntegralNumber()) {
            return null;
        }
        int number = value.intValue();
        if (number != 0 && number != 1) {
            return null;
        }
        return new Write(fieldCode, number);
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? "" : value.asText("").trim();
    }
}
