package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateProgressRespVO;

/**
 * 建任务向导进度。
 *
 * <p>负责：读已放到哪一步、放行下一步、前面改了把后面作废。</p>
 * <p>不负责：算路、排期占窗、开跑；不把条件策略当成创建流程。</p>
 */
public interface PatrolTaskCreateProcessService {

    InspectionTaskCreateProgressRespVO getProgress(Long taskId);

    InspectionTaskCreateProgressRespVO advance(Long taskId, Integer toStep);

    InspectionTaskCreateProgressRespVO invalidate(Long taskId, Integer keepThroughStep);
}
