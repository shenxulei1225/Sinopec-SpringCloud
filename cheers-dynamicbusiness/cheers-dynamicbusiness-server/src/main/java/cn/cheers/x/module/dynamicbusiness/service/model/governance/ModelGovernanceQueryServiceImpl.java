package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 型号治理可见性实现。
 *
 * <p>权威字段只来自 {@code governance_status} 与 {@code origin_facility_id}；
 * 不读取实体设施字段，也不为缺失治理状态生成兼容值。</p>
 */
@Service
public class ModelGovernanceQueryServiceImpl implements ModelGovernanceQueryService {

    private static final String GOVERNANCE_LOCAL = "LOCAL";
    private static final String GOVERNANCE_COMPANY = "COMPANY";
    private static final int STATUS_ENABLED = 1;

    @Override
    public ModelDO assertVisible(ModelDO model, Long effectiveFacilityId, boolean networkDataAdmin) {
        if (!isVisible(model, effectiveFacilityId, networkDataAdmin)) {
            throw new ServiceException(403, "无权访问该型号");
        }
        return model;
    }

    @Override
    public List<ModelDO> filterVisible(List<ModelDO> candidates, Long effectiveFacilityId,
                                       boolean networkDataAdmin) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
                .filter(Objects::nonNull)
                .filter(model -> isVisible(model, effectiveFacilityId, networkDataAdmin))
                .toList();
    }

    @Override
    public PageResult<ModelDO> filterVisiblePage(List<ModelDO> candidates, Long effectiveFacilityId,
                                                 boolean networkDataAdmin, Integer pageNo, Integer pageSize) {
        List<ModelDO> visible = filterVisible(candidates, effectiveFacilityId, networkDataAdmin);
        int resolvedPageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int resolvedPageSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int from = (resolvedPageNo - 1) * resolvedPageSize;
        List<ModelDO> page = from >= visible.size()
                ? List.of()
                : visible.subList(from, Math.min(from + resolvedPageSize, visible.size()));
        return new PageResult<>(page, (long) visible.size());
    }

    private boolean isVisible(ModelDO model, Long effectiveFacilityId, boolean networkDataAdmin) {
        if (model == null) {
            return false;
        }
        if (GOVERNANCE_COMPANY.equals(model.getGovernanceStatus())) {
            return Objects.equals(STATUS_ENABLED, model.getStatus());
        }
        if (GOVERNANCE_LOCAL.equals(model.getGovernanceStatus())) {
            return networkDataAdmin
                    || effectiveFacilityId != null
                    && Objects.equals(effectiveFacilityId, model.getOriginFacilityId());
        }
        return false;
    }
}
