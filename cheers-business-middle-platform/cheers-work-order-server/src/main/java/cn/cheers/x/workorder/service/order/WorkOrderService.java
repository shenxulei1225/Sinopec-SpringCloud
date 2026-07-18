package cn.cheers.x.workorder.service.order;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderPageReqVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderRespVO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderStepCompleteReqVO;
import jakarta.validation.Valid;

/**
 * 工单生命周期 Service
 */
public interface WorkOrderService {

    /**
     * 派工创建工单：快照已发布标准步骤，初始化步骤结果，状态为 DISPATCHED
     *
     * @param reqDTO 创建请求
     * @return 工单 ID
     */
    Long createFromDispatch(@Valid WorkOrderCreateReqDTO reqDTO);

    /**
     * 工单详情（含步骤结果）
     *
     * @param id 工单 ID
     * @return 详情
     */
    WorkOrderRespVO getWorkOrder(Long id);

    /**
     * 工单分页
     *
     * @param pageReqVO 查询条件
     * @return 分页结果
     */
    PageResult<WorkOrderRespVO> getWorkOrderPage(WorkOrderPageReqVO pageReqVO);

    /**
     * 开工：DISPATCHED → IN_PROGRESS
     *
     * @param id 工单 ID
     */
    void start(Long id);

    /**
     * 完成指定步骤
     *
     * @param id      工单 ID
     * @param stepCode 步骤编码
     * @param reqVO   可选结果载荷
     */
    void completeStep(Long id, String stepCode, WorkOrderStepCompleteReqVO reqVO);

    /**
     * 完工：所有必填步骤已完成后 IN_PROGRESS → COMPLETED
     *
     * @param id 工单 ID
     */
    void complete(Long id);

    /**
     * 取消工单
     *
     * @param id 工单 ID
     */
    void cancel(Long id);

}
