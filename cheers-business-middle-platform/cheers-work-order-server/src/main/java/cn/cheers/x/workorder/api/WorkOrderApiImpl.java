package cn.cheers.x.workorder.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import cn.cheers.x.workorder.service.order.WorkOrderService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 工单 RPC 实现
 */
@RestController
@Validated
public class WorkOrderApiImpl implements WorkOrderApi {

    @Resource
    private WorkOrderService workOrderService;

    @Override
    public CommonResult<Long> create(WorkOrderCreateReqDTO reqDTO) {
        return success(workOrderService.createFromDispatch(reqDTO));
    }

}
