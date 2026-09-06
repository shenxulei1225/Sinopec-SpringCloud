package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用台账模版在<strong>创建</strong>某一数据目录的页面布局时写下的默认区域与栏宽。
 * <p>
 * 区域编号只是这份模版写入的 id，给栏上 {@code columnSection} 对齐用；
 * 名字是给人看的展示文本（可改），摆法是布局参数（水平并排 / 自由摆放）。
 * 前端只读布局头上的区域清单与栏上的区域编号，不得按名称或有没有栏猜区域身份。
 */
public final class DmDataTabLayoutBootstrapMeta {

    public static final String SECTION_ID_A = "section-a";
    public static final String SECTION_ID_B = "section-b";
    public static final String SECTION_ID_C = "section-c";

    /** 水平并排：这一格按栏行画分类 / 型号 / 实体。 */
    public static final String ARRANGE_HORIZONTAL = "horizontal";
    /** 自由摆放：这一格按区域上的内容铺；详情是栏，不是区域身份。 */
    public static final String ARRANGE_FREE = "free";

    public static final int FILTER_SECTION_WIDTH_PX = 240;
    public static final int OBJECT_SECTION_WIDTH_PX = 400;
    public static final int CATEGORY_COLUMN_WIDTH_PX = 240;
    public static final int MODEL_COLUMN_WIDTH_PX = 192;
    public static final int ENTITY_COLUMN_WIDTH_PX = 240;
    public static final int DETAIL_SECTION_WIDTH_PX = 360;

    private DmDataTabLayoutBootstrapMeta() {
    }

    /**
     * 通用台账模版创建时写入布局头的区域清单。
     * 默认展示名使用中性命名（区段A/B/C），前两块水平并排，第三块自由摆放。
     */
    public static List<Map<String, Object>> ledgerTemplateSections() {
        List<Map<String, Object>> sections = new ArrayList<>();
        sections.add(section(SECTION_ID_A, "区段A", ARRANGE_HORIZONTAL));
        sections.add(section(SECTION_ID_B, "区段B", ARRANGE_HORIZONTAL));
        sections.add(section(SECTION_ID_C, "区段C", ARRANGE_FREE));
        return sections;
    }

    public static Map<String, Object> settingsWithLedgerSections() {
        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("sections", ledgerTemplateSections());
        settings.put("sectionHidden", new LinkedHashMap<String, Boolean>());
        return settings;
    }

    public static Map<String, Object> categoryMeta(
            String label, String categoryTypeCode, String columnKey, String sectionId) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("label", label);
        if (categoryTypeCode != null && !categoryTypeCode.isBlank()) {
            meta.put("categoryTypeCode", categoryTypeCode);
        }
        meta.put("columnKey", columnKey);
        meta.put("columnSection", sectionId);
        meta.put("widthPx", CATEGORY_COLUMN_WIDTH_PX);
        meta.put("sectionWidthPx", FILTER_SECTION_WIDTH_PX);
        return meta;
    }

    /**
     * 型号栏默认与分类进同一块（本模版第一块）。
     * {@code sectionLead} 为 true 时写区段宽（仅当本区没有分类领头栏时用）。
     */
    public static Map<String, Object> modelMeta(String sectionId, boolean sectionLead) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("columnSection", sectionId);
        meta.put("widthPx", MODEL_COLUMN_WIDTH_PX);
        if (sectionLead) {
            meta.put("sectionWidthPx", FILTER_SECTION_WIDTH_PX);
        }
        return meta;
    }

    /** 此栏默认进本模版第三块；跟谁展示由关系图连线决定。 */
    public static Map<String, Object> detailMeta(String sectionId, String label) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("columnSection", sectionId);
        meta.put("label", (label == null || label.isBlank()) ? "区段栏" : label.trim());
        meta.put("sectionWidthPx", DETAIL_SECTION_WIDTH_PX);
        return meta;
    }

    /** 实体栏默认进本模版第二块；通常作为该区域领头栏写下区段宽。 */
    public static Map<String, Object> entityMeta(String sectionId, boolean objectSectionLead) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("columnSection", sectionId);
        meta.put("widthPx", ENTITY_COLUMN_WIDTH_PX);
        if (objectSectionLead) {
            meta.put("sectionWidthPx", OBJECT_SECTION_WIDTH_PX);
        }
        return meta;
    }

    private static Map<String, Object> section(String id, String name, String arrange) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", id);
        row.put("name", name);
        row.put("arrange", arrange);
        return row;
    }
}
