package cn.cheers.x.module.dynamicbusiness.service.capability;

import java.util.List;

/**
 * System 模块固定资源的能力定义（与前端 systemListAdapters 对齐）。
 */
public record SystemCapabilityResourceDef(
        String resourceCode,
        String label,
        String listUrl,
        boolean paginated,
        List<FilterSpec> filters,
        List<DisplaySpec> displayFields,
        boolean supportsTree) {

    public String instanceKey() {
        return "system:" + resourceCode;
    }

    public record FilterSpec(
            String fieldKey,
            String label,
            String control,
            boolean searchable,
            String dictType,
            String refTargetKey) {
    }

    public record DisplaySpec(
            String fieldKey,
            String label,
            String renderAs,
            int sortOrder,
            boolean defaultVisible) {
    }
}
