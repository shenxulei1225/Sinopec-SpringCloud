package cn.iocoder.yudao.module.inspection.task.service.schedule.validator;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO;

import org.springframework.stereotype.Component;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 时间窗验证器。
 *
 * <p>验证排期需求中时间配置的合法性。</p>
 */
@Component
public class ScheduleWindowValidator {

    public void validate(InspectionTaskScheduleRequirementDO requirement) {
        if (requirement == null) {
            return;
        }

        // 验证每日执行时间窗口
        if (requirement.getWindowStartTime() != null && requirement.getWindowEndTime() != null) {
            if (!requirement.getWindowStartTime().isBefore(requirement.getWindowEndTime())) {
                throw ServiceExceptionUtil.exception(BAD_REQUEST, "每日执行开始时间必须早于结束时间");
            }
        }

        // 验证固定时间点
        if (requirement.getTimePoints() != null) {
            for (int i = 0; i < requirement.getTimePoints().size(); i++) {
                if (requirement.getTimePoints().get(i) == null) {
                    throw ServiceExceptionUtil.exception(BAD_REQUEST, "固定时间点不能为空");
                }
            }
        }
    }
}
