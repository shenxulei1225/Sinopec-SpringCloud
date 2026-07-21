package cn.cheers.x.inspection.inspection_content.service.binding.impl;

import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.binding.ObjectStationBindingDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.binding.ObjectStationBindingMapper;
import cn.cheers.x.inspection.inspection_content.service.binding.ObjectStationBindingQueryService;
import cn.cheers.x.inspection.inspection_content.service.binding.model.BindingResolveResult;
import cn.cheers.x.inspection.inspection_content.service.binding.model.ObjectStationBindingView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 对象↔停靠点绑定查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class ObjectStationBindingQueryServiceImpl implements ObjectStationBindingQueryService {

    private final ObjectStationBindingMapper bindingMapper;

    @Override
    public BindingResolveResult listByObjectIds(Long facilityId, Collection<Long> objectIds) {
        BindingResolveResult result = new BindingResolveResult();
        if (facilityId == null || objectIds == null || objectIds.isEmpty()) {
            return result;
        }

        List<Long> requestedObjectIds = objectIds.stream()
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (requestedObjectIds.isEmpty()) {
            return result;
        }

        List<ObjectStationBindingDO> bindings = bindingMapper.selectByFacilityAndObjectIds(facilityId, requestedObjectIds);
        Map<Long, List<ObjectStationBindingView>> grouped = new LinkedHashMap<>();
        for (ObjectStationBindingDO binding : bindings) {
            grouped.computeIfAbsent(binding.getObjectId(), ignored -> new ArrayList<>())
                    .add(toView(binding));
        }

        Set<Long> missingObjectIds = new LinkedHashSet<>();
        for (Long objectId : requestedObjectIds) {
            List<ObjectStationBindingView> objectBindings = grouped.get(objectId);
            if (objectBindings == null || objectBindings.isEmpty()) {
                missingObjectIds.add(objectId);
            }
        }

        result.setBindingsByObjectId(grouped);
        result.setMissingObjectIds(new ArrayList<>(missingObjectIds));
        return result;
    }

    @Override
    public List<ObjectStationBindingView> listByObjectId(Long facilityId, Long objectId) {
        if (facilityId == null || objectId == null) {
            return List.of();
        }
        return bindingMapper.selectByFacilityAndObjectId(facilityId, objectId).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    private ObjectStationBindingView toView(ObjectStationBindingDO binding) {
        return BeanUtils.toBean(binding, ObjectStationBindingView.class);
    }
}
