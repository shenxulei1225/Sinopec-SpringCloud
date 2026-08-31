package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 创建数据类型时写入的标准布局。前端只读这份配置，不在代码里再垫宽。
 * <p>
 * 区段宽写在该区段领头栏的 {@code sectionWidthPx}；栏宽写在各栏 {@code widthPx}。
 * 结果区段（What）始终吃剩余，不写标准宽。
 */
public final class DmDataTabLayoutBootstrapMeta {

    public static final int FILTER_SECTION_WIDTH_PX = 240;
    public static final int OBJECT_SECTION_WIDTH_PX = 400;
    public static final int CATEGORY_COLUMN_WIDTH_PX = 240;
    public static final int MODEL_COLUMN_WIDTH_PX = 192;
    public static final int ENTITY_COLUMN_WIDTH_PX = 240;

    public static final String SECTION_FILTER = "FILTER";
    public static final String SECTION_OBJECT = "OBJECT";

    private DmDataTabLayoutBootstrapMeta() {
    }

    public static Map<String, Object> categoryMeta(
            String label, String categoryTypeCode, String columnKey) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("label", label);
        meta.put("categoryTypeCode", categoryTypeCode);
        meta.put("columnKey", columnKey);
        meta.put("columnSection", SECTION_FILTER);
        meta.put("widthPx", CATEGORY_COLUMN_WIDTH_PX);
        meta.put("sectionWidthPx", FILTER_SECTION_WIDTH_PX);
        return meta;
    }

    /**
     * 型号栏默认落在筛选区段（与分类同区，降低上手成本）。
     * {@code sectionLead} 为 true 时写区段宽（仅当本区没有分类领头栏时用）。
     */
    public static Map<String, Object> modelMeta(boolean sectionLead) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("columnSection", SECTION_FILTER);
        meta.put("widthPx", MODEL_COLUMN_WIDTH_PX);
        if (sectionLead) {
            meta.put("sectionWidthPx", FILTER_SECTION_WIDTH_PX);
        }
        return meta;
    }

    /** 实体栏默认在 Who/对象区段；通常作为该区段领头栏写下区段宽 */
    public static Map<String, Object> entityMeta(boolean objectSectionLead) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("columnSection", SECTION_OBJECT);
        meta.put("widthPx", ENTITY_COLUMN_WIDTH_PX);
        if (objectSectionLead) {
            meta.put("sectionWidthPx", OBJECT_SECTION_WIDTH_PX);
        }
        return meta;
    }
}
