package cn.iocoder.yudao.module.inspection.task.service.query;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.InspectionTaskRespVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.InspectionTaskSimpleRespVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task.InspectionTaskPageReqVO;

/**
 * 巡检任务查询服务。
 */
public interface InspectionTaskQueryService {

    /**
     * 分页查询任务列表（轻量级）。
     *
     * @param pageReqVO 分页参数
     * @return 分页结果
     */
    PageResult<InspectionTaskSimpleRespVO> getTaskPage(InspectionTaskPageReqVO pageReqVO);

    /**
     * 获取任务详情（包含子任务列表）。
     *
     * @param id 任务ID
     * @return 任务详情
     */
    InspectionTaskRespVO getTaskDetail(Long id);
}
