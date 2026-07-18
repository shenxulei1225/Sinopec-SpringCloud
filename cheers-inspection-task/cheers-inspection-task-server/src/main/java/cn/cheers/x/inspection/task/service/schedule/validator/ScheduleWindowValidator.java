package cn.cheers.x.inspection.task.service.schedule.validator;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO.ScheduleTemplateConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 时间窗验证器。
 *
 * <p>验证排期需求中各模板组合时间配置的合法性。</p>
 */
@Component
public class ScheduleWindowValidator {

    public void validate(InspectionTaskScheduleRequirementDO requirement) {
        if (requirement == null || requirement.getScheduleTemplates() == null) {
            return;
        }

        for (ScheduleTemplateConfig templateConfig : requirement.getScheduleTemplates()) {
            validateTemplateConfig(templateConfig);
        }
    }

    private void validateTemplateConfig(ScheduleTemplateConfig templateConfig) {
        if (templateConfig == null) {
            return;
        }

        Map<String, Object> effectiveConfig = resolveEffectiveConfig(templateConfig);

        // 验证重复范围
        Object repeatRangeObj = effectiveConfig.get("repeatRange");
        if (repeatRangeObj instanceof Map<?, ?> repeatRange) {
            Object startDate = repeatRange.get("startDate");
            Object endDate = repeatRange.get("endDate");
            if (startDate instanceof String start && endDate instanceof String end) {
                if (start.compareTo(end) > 0) {
                    throw ServiceExceptionUtil.exception(BAD_REQUEST, "重复范围开始日期不能晚于结束日期");
                }
            }
        }

        // 验证固定时间点
        Object timePointsObj = effectiveConfig.get("timePoints");
        if (timePointsObj instanceof List<?> timePoints) {
            for (Object timePoint : timePoints) {
                if (timePoint == null) {
                    throw ServiceExceptionUtil.exception(BAD_REQUEST, "固定时间点不能为空");
                }
            }
        }
    }

    private Map<String, Object> resolveEffectiveConfig(ScheduleTemplateConfig templateConfig) {
        Map<String, Object> effectiveConfig = new HashMap<>();
        if (templateConfig.getOriginalConfig() != null) {
            effectiveConfig.putAll(templateConfig.getOriginalConfig());
        }
        if (templateConfig.getChangedFields() != null) {
            effectiveConfig.putAll(templateConfig.getChangedFields());
        }
        return effectiveConfig;
    }

}
