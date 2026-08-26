package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;

import java.util.List;

/**
 * 型号治理查询服务。
 *
 * <p>本服务是型号列表可见性的唯一权威：公司规格仅在启用时进入列表；
 * 本地型号仅对发起站场或全网数据管理员可见。调用方不得再按设施字段补一套过滤。</p>
 */
public interface ModelGovernanceQueryService {

    /**
     * 断言单个型号对当前调用方可见。
     *
     * @return 原型号，便于详情、更新、复制与覆盖范围入口复用同一可见性门禁
     * @throws cn.cheers.x.framework.common.exception.ServiceException 不可见时返回 403
     */
    ModelDO assertVisible(ModelDO model, Long effectiveFacilityId, boolean networkDataAdmin);

    /**
     * 按当前有效站场与全网治理能力过滤候选型号，不改变候选顺序。
     *
     * @param candidates 候选型号
     * @param effectiveFacilityId 当前有效站场；为空时普通调用方看不到本地型号
     * @param networkDataAdmin 是否具备全网数据管理能力
     * @return 可进入列表的型号
     */
    List<ModelDO> filterVisible(List<ModelDO> candidates, Long effectiveFacilityId, boolean networkDataAdmin);

    /**
     * 先执行治理可见性过滤，再按请求页码切片；总数以过滤后的结果为准。
     */
    PageResult<ModelDO> filterVisiblePage(List<ModelDO> candidates, Long effectiveFacilityId,
                                          boolean networkDataAdmin, Integer pageNo, Integer pageSize);
}
