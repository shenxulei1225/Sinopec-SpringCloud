package cn.cheers.x.module.dynamicbusiness.service.orchestration;

import cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo.BusinessOrchestrationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo.BusinessOrchestrationUpsertReqVO;

/**
 * 业务编排命令/查询（5W）。
 *
 * <p>负责：按 businessCode 读写编排权威与画布坐标。</p>
 * <p>禁止：读路径用种子猜补已落库缺口；缺行返回 null 由调用方决定是否用种子。</p>
 */
public interface BusinessOrchestrationService {

    /**
     * @return 已落库编排；不存在返回 null
     */
    BusinessOrchestrationRespVO getByBusinessCode(String businessCode);

    /**
     * 按 businessCode upsert；返回行 id
     */
    Long upsert(BusinessOrchestrationUpsertReqVO reqVO);
}
