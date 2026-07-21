package cn.cheers.x.inspection.inspection_content.service.binding;

import cn.cheers.x.inspection.inspection_content.service.binding.model.BindingResolveResult;
import cn.cheers.x.inspection.inspection_content.service.binding.model.ObjectStationBindingView;

import java.util.Collection;
import java.util.List;

/**
 * 对象↔停靠点绑定查询服务。
 */
public interface ObjectStationBindingQueryService {

    BindingResolveResult listByObjectIds(Long facilityId, Collection<Long> objectIds);

    List<ObjectStationBindingView> listByObjectId(Long facilityId, Long objectId);
}
