package cn.cheers.x.inspection.task.model.task;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检内容。
 *
 * <p>表示任务或模板中选择的巡检对象及其巡检项。采用统一结构，模板和任务复用。</p>
 *
 * <p>结构说明：</p>
 * <ul>
 *     <li>name - 显示名称（如"罐区巡检"）</li>
 *     <li>groups - 模板组列表，每个组对应一个模板</li>
 *     <li>customObjects - 用户自行添加的对象</li>
 * </ul>
 */
@Data
public class InspectionContent {

    /**
     * 显示名称。
     */
    private String name;

    /**
     * 模板组列表。
     */
    private List<ObjectGroup> groups = new ArrayList<>();

    /**
     * 自定义对象列表（用户自行添加）。
     */
    private List<ObjectContent> customObjects = new ArrayList<>();

    // ==================== 内部类 ====================

    /**
     * 巡检对象组。
     *
     * <p>对应一个模板，包含模板信息和该模板下的对象列表。</p>
     */
    @Data
    public static class ObjectGroup {

        /**
         * 模板 ID。
         */
        private Long templateId;

        /**
         * 模板名称（显示用）。
         */
        private String templateName;

        /**
         * 该模板下的巡检对象列表。
         */
        private List<ObjectContent> objects = new ArrayList<>();
    }

    /**
     * 巡检对象。
     */
    @Data
    public static class ObjectContent {

        /**
         * 对象 ID。
         */
        private Long objectId;

        /**
         * 对象编码。
         */
        private String objectCode;

        /**
         * 对象名称。
         */
        private String objectName;

        /**
         * 来源类型（如 facility, device）。
         */
        private String sourceType;

        /**
         * 分类 ID。
         */
        private Long categoryId;

        /**
         * 对象模型。
         */
        private String objectModel;

        /**
         * 巡检项列表。
         */
        private List<ItemContent> items = new ArrayList<>();

        /**
         * 对象详情（查询原始业务系统获得，非持久化字段）。
         */
        private transient ObjectDetail detail;
    }

    /**
     * 巡检项。
     */
    @Data
    public static class ItemContent {

        /**
         * 巡检项 ID。
         */
        private Long itemId;

        /**
         * 巡检项编码。
         */
        private String itemCode;

        /**
         * 巡检项名称。
         */
        private String itemName;

        /**
         * 巡检项描述。
         */
        private String description;

        /**
         * 是否必填。
         */
        private Boolean required;

        /**
         * 输入类型（如 text, number, select, photo）。
         */
        private String inputType;

        /**
         * 选项列表（输入类型为 select 时使用）。
         */
        private List<String> options;
    }

    /**
     * 对象详情。
     *
     * <p>非持久化字段，仅在查询时从业务系统补充。</p>
     */
    @Data
    public static class ObjectDetail {

        /**
         * 位置。
         */
        private String location;

        /**
         * 负责人。
         */
        private String manager;

        /**
         * 负责人联系方式。
         */
        private String managerPhone;

        /**
         * 状态。
         */
        private String status;

        /**
         * 扩展信息（JSON 格式）。
         */
        private String extra;
    }
}
