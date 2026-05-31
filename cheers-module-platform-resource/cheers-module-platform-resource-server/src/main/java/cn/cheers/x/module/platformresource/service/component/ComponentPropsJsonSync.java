package cn.cheers.x.module.platformresource.service.component;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentPropsRespVO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentPropsDO;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 治理字段：列 {@code data_source_key} 与 {@code props_json.dataSourceKey} 双向同步。
 */
public final class ComponentPropsJsonSync {

    public static final String JSON_KEY = "dataSourceKey";
    /** 历史 JSON 别名，读取时兼容 */
    public static final String LEGACY_JSON_KEY = "queryContractKey";

    private ComponentPropsJsonSync() {
    }

    public static String resolveDataSourceKey(String columnKey, Map<String, Object> propsJson) {
        if (StrUtil.isNotBlank(columnKey)) {
            return columnKey.trim();
        }
        if (propsJson == null || propsJson.isEmpty()) {
            return null;
        }
        Object fromJson = propsJson.get(JSON_KEY);
        if (fromJson != null && StrUtil.isNotBlank(String.valueOf(fromJson))) {
            return String.valueOf(fromJson).trim();
        }
        Object legacy = propsJson.get(LEGACY_JSON_KEY);
        if (legacy != null && StrUtil.isNotBlank(String.valueOf(legacy))) {
            return String.valueOf(legacy).trim();
        }
        return null;
    }

    public static void applyToRow(ComponentPropsDO row, String dataSourceKey, Map<String, Object> propsJson) {
        String key = resolveDataSourceKey(dataSourceKey, propsJson);
        row.setDataSourceKey(key);
        if (propsJson == null) {
            return;
        }
        Map<String, Object> merged = new LinkedHashMap<>(propsJson);
        if (StrUtil.isNotBlank(key)) {
            merged.put(JSON_KEY, key);
            merged.remove(LEGACY_JSON_KEY);
        }
        row.setPropsJson(ComponentPropsJsonHelper.toJson(merged));
    }

    public static Map<String, Object> mergeJsonWithKey(String existingJson, Map<String, Object> patch, String dataSourceKey) {
        Map<String, Object> base = ComponentPropsJsonHelper.parseJsonMap(existingJson);
        Map<String, Object> merged = new LinkedHashMap<>(base);
        if (patch != null) {
            merged.putAll(patch);
        }
        String key = resolveDataSourceKey(dataSourceKey, merged);
        if (StrUtil.isNotBlank(key)) {
            merged.put(JSON_KEY, key);
            merged.remove(LEGACY_JSON_KEY);
        }
        return merged;
    }

    public static void enrichRespVO(ComponentPropsRespVO vo) {
        if (vo == null) {
            return;
        }
        String key = resolveDataSourceKey(vo.getDataSourceKey(), vo.getPropsJson());
        vo.setDataSourceKey(key);
        if (StrUtil.isNotBlank(key) && vo.getPropsJson() != null) {
            vo.getPropsJson().put(JSON_KEY, key);
            vo.getPropsJson().remove(LEGACY_JSON_KEY);
        }
    }

    public static void enrichRow(ComponentPropsDO row) {
        if (row == null) {
            return;
        }
        Map<String, Object> json = ComponentPropsJsonHelper.parseJsonMap(row.getPropsJson());
        String key = resolveDataSourceKey(row.getDataSourceKey(), json);
        row.setDataSourceKey(key);
        if (StrUtil.isNotBlank(key)) {
            json.put(JSON_KEY, key);
            json.remove(LEGACY_JSON_KEY);
            row.setPropsJson(ComponentPropsJsonHelper.toJson(json));
        }
    }
}
