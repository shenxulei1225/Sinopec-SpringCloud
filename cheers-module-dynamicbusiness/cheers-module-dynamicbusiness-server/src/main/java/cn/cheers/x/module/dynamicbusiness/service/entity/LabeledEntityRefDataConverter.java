package cn.cheers.x.module.dynamicbusiness.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 带标签的实体引用数据转换器
 *
 * 负责在用户界面数据格式和数据库存储格式之间进行转换。
 *
 * @author yudao
 */
public class LabeledEntityRefDataConverter {

    /**
     * 带标签的实体引用项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LabeledEntityRefItem {
        /** 关联对象的ID */
        private Long id;

        /** 关联对象的编码 */
        private String code;

        /** 关联对象的名称 */
        private String name;

        /** 用户定义的标签 */
        private String label;
    }

    /**
     * 将带标签的关联项列表转换为数据库存储格式
     *
     * @param items 带标签的关联项列表
     * @return 存储格式的Map对象
     */
    public static Object convertToStorageFormat(List<LabeledEntityRefItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        // 转换为存储格式
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (LabeledEntityRefItem item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("code", item.getCode());
            itemMap.put("name", item.getName());
            itemMap.put("label", item.getLabel());
            itemList.add(itemMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", itemList);
        return result;
    }

    /**
     * 从数据库存储格式转换为带标签的关联项列表
     *
     * @param data 存储格式的数据对象
     * @return 带标签的关联项列表
     */
    @SuppressWarnings("unchecked")
    public static List<LabeledEntityRefItem> convertFromStorageFormat(Object data) {
        if (data == null) {
            return new ArrayList<>();
        }

        List<LabeledEntityRefItem> result = new ArrayList<>();

        try {
            if (data instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) data;
                Object itemsObj = map.get("items");

                if (itemsObj instanceof List) {
                    List<?> items = (List<?>) itemsObj;
                    for (Object itemObj : items) {
                        if (itemObj instanceof Map) {
                            Map<String, Object> itemMap = (Map<String, Object>) itemObj;

                            LabeledEntityRefItem item = LabeledEntityRefItem.builder()
                                    .id(convertToLong(itemMap.get("id")))
                                    .code(convertToString(itemMap.get("code")))
                                    .name(convertToString(itemMap.get("name")))
                                    .label(convertToString(itemMap.get("label")))
                                    .build();

                            result.add(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 如果转换失败，返回空列表
            return new ArrayList<>();
        }

        return result;
    }

    /**
     * 将对象转换为Long类型
     */
    private static Long convertToLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 将对象转换为String类型
     */
    private static String convertToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    /**
     * 验证带标签的关联数据是否有效
     *
     * @param items 关联项列表
     * @return 验证结果，null表示通过，否则返回错误信息
     */
    public static String validateLabeledEntityRefData(List<LabeledEntityRefItem> items) {
        if (items == null) {
            return null; // null表示有效
        }

        for (LabeledEntityRefItem item : items) {
            if (item.getId() == null) {
                return "关联项ID不能为空";
            }
            if (item.getLabel() == null || item.getLabel().trim().isEmpty()) {
                return "关联项标签不能为空";
            }
            if (item.getName() == null || item.getName().trim().isEmpty()) {
                return "关联项名称不能为空";
            }
        }

        return null; // 验证通过
    }
}