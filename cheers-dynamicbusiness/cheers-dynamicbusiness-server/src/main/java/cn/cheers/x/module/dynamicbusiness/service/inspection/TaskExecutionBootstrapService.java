package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.TaskExecutionBootstrapRespVO;

/**
 * 开跑：锁定 SOP merge 快照到执行记录，并批量创建步骤实例。
 * 不调用路径服务。
 */
public interface TaskExecutionBootstrapService {

    TaskExecutionBootstrapRespVO bootstrap(TaskExecutionBootstrapReqVO req);
}
