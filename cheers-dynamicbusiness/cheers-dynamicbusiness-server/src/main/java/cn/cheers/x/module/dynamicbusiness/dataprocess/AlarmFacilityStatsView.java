package cn.cheers.x.module.dynamicbusiness.dataprocess;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 一座站当前的告警统计。分类来自告警实际挂上的分类树，不是写死的三类。
 * 每一级的数字已经含它下面各级。级别只有紧急、重要、一般。
 */
@Data
public class AlarmFacilityStatsView {

    private long facilityId;
    private Long rootId;
    private List<CategoryStat> categories = new ArrayList<>();
    /** 这一次写入或改处理的那一条。没有变动时为空，画面不弹窗。 */
    private ChangedAlarm changed;

    @Data
    public static class CategoryStat {
        private long id;
        private Long parentId;
        private String name;
        private String code;
        private Integer sort;
        private Map<String, AlarmFacilityCount.Cell> levels;
    }

    @Data
    public static class ChangedAlarm {
        private Long id;
        private Long categoryId;
        private String name;
        private String level;
        private String status;
        private String content;
        private String device;
        private String location;
        private String triggerValue;
        private String threshold;
        private String point;
        private String review;
        private String trace;
        private String atText;
    }
}
