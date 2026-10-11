package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.module.dynamicbusiness.action.paramslots.ActionParamSlotsJsonCore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 读动作库 {@code param_slots_json} 的字段名单与默认值。
 * <p>供翻译前按对照补动作参数、系统生成项；不负责检查参数（步骤参数袋）写入。
 */
final class ActionParamSlotReader {

    private ActionParamSlotReader() {
    }

    static Map<String, Object> defaultValues(
            Object raw, String means, Long equipmentModelId, ObjectMapper objectMapper) {
        return ActionParamSlotsJsonCore.defaultValues(raw, means, equipmentModelId, objectMapper);
    }

    static void fillBlankFromDefaults(Map<String, Object> params, Map<String, Object> defaults) {
        if (params == null || defaults == null || defaults.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null) {
                continue;
            }
            if (!isFilled(params.get(entry.getKey()))) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
    }

    static void writePayloadPositionFromSelectedCamera(Map<String, Object> params) {
        if (params == null || isFilled(params.get("payloadPositionIndex"))) {
            return;
        }
        String gimbal = firstMountIndex(params, "selected_camera", "selected_searchlight", "selected_lidar");
        if (gimbal != null) {
            params.put("payloadPositionIndex", Integer.valueOf(gimbal));
        }
    }

    /**
     * 喊话器或探照灯这一行选中之后，把编号最后一段写成实时指令要的负载序号。
     * 已经写过负载序号就不再改。两边都没选，这一格保持空，下发时报缺。
     */
    static void writeSpeakerIndexFromSelectedSpeaker(Map<String, Object> params) {
        if (params == null || isFilled(params.get("psdk_index"))) {
            return;
        }
        String index = gimbalIndexOf(params.get("selected_speaker"));
        if (index == null) {
            index = gimbalIndexOf(params.get("selected_searchlight"));
        }
        if (index != null) {
            params.put("psdk_index", Integer.valueOf(index));
        }
    }

    /**
     * 手动播放喊话文本时，用这段文字的校验和填官网要求的 md5。
     * 文字还没写，就不编一个校验和。
     */
    static void writeSpeakerTtsMd5(Map<String, Object> params) {
        if (params == null || isFilled(params.get("speaker_tts_md5"))) {
            return;
        }
        String spoken = text(params.get("speaker_tts_text"));
        if (spoken.isBlank()) {
            return;
        }
        params.put("speaker_tts_md5", md5Hex(spoken));
    }

    private static String md5Hex(String spoken) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(spoken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("无法计算喊话文本校验和", ex);
        }
    }

    static boolean isFilled(Object raw) {
        return ActionParamSlotsJsonCore.isFilled(raw);
    }

    static Map<String, Object> orientedResourceDefaults(Object raw, ObjectMapper objectMapper) {
        Object value = ActionParamSlotsJsonCore.unwrap(raw, objectMapper);
        if (!(value instanceof Map<?, ?> root)) {
            return Map.of();
        }
        Object methodsRaw = root.get("methods");
        if (!(methodsRaw instanceof List<?> methods)) {
            return Map.of();
        }
        List<String> codes = List.of("orientedFilePath", "orientedFileMD5", "orientedFileSize");
        Map<String, String> agreed = new LinkedHashMap<>();
        java.util.Set<String> conflict = new java.util.HashSet<>();
        for (Object row : methods) {
            if (!(row instanceof Map<?, ?> method) || !(method.get("fields") instanceof List<?> fields)) {
                continue;
            }
            for (Object fieldRow : fields) {
                if (!(fieldRow instanceof Map<?, ?> field)) {
                    continue;
                }
                String code = text(first(field, "fieldCode", "code"));
                if (!codes.contains(code) || conflict.contains(code)) {
                    continue;
                }
                String written = text(first(field, "defaultValue", "value"));
                if (written.isBlank()) {
                    continue;
                }
                String prior = agreed.get(code);
                if (prior == null) {
                    agreed.put(code, written);
                } else if (!prior.equals(written)) {
                    agreed.remove(code);
                    conflict.add(code);
                }
            }
        }
        return new LinkedHashMap<>(agreed);
    }

    private static String firstMountIndex(Map<String, Object> params, String... fields) {
        for (String field : fields) {
            String index = gimbalIndexOf(params.get(field));
            if (index != null) {
                return index;
            }
        }
        return null;
    }

    private static String gimbalIndexOf(Object raw) {
        String text = text(raw);
        if (!text.matches("\\d+-\\d+-\\d+")) {
            return null;
        }
        int dash = text.lastIndexOf('-');
        return dash < 0 ? null : text.substring(dash + 1);
    }

    private static Object first(Map<?, ?> row, String... keys) {
        for (String key : keys) {
            if (row.containsKey(key) && row.get(key) != null) {
                return row.get(key);
            }
        }
        return null;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }
}
