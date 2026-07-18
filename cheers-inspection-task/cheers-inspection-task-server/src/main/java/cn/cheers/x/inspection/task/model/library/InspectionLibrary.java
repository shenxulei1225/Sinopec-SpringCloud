package cn.cheers.x.inspection.task.model.library;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检目标/内容库。
 *
 * <p>用于维护系统内可被巡检任务选择的巡检目标，以及每个巡检目标下可选的巡检内容。</p>
 *
 * <p>说明：</p>
 * <ul>
 *     <li>左侧区域、设备类型等树形分类属于该库的查询与组织方式，不属于任务定义结果。</li>
 *     <li>本模型当前仅承载目标与内容，不提前绑定巡检点位。</li>
 *     <li>巡检标准与巡检内容是约束/参考关系，不在本模型中做强绑定。</li>
 * </ul>
 */
@Data
public class InspectionLibrary {

    /**
     * 分类 ID。
     */
    private Long categoryId;

    /**
     * 分类名称。
     */
    private String categoryName;

    /**
     * 巡检目标列表。
     */
    private List<LibraryObject> objects = new ArrayList<>();

    @Data
    public static class LibraryObject {

        /**
         * 巡检目标 ID。
         */
        private Long objectId;

        /**
         * 巡检目标编码。
         */
        private String objectCode;

        /**
         * 巡检目标名称。
         */
        private String objectName;

        /**
         * 分类 ID。
         */
        private Long categoryId;

        /**
         * 分类名称。
         */
        private String categoryName;

        /**
         * 所属站场 ID。
         */
        private Long siteId;

        /**
         * 站场名称。
         */
        private String siteName;

        /**
         * 来源设施 ID。
         */
        private Long facilityId;

        /**
         * 设施名称。
         */
        private String facilityName;

        /**
         * 状态。
         */
        private String status;

        /**
         * 备注。
         */
        private String remark;

        /**
         * 巡检内容列表。
         */
        private List<LibraryItem> items = new ArrayList<>();
    }

    @Data
    public static class LibraryItem {

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
         * 分类 ID。
         */
        private Long categoryId;

        /**
         * 分类名称。
         */
        private String categoryName;

        /**
         * 巡检项说明。
         */
        private String description;

        /**
         * 状态。
         */
        private String status;

        /**
         * 备注。
         */
        private String remark;
    }
}
