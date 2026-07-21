package cn.cheers.x.inspection.inspection_content.service.profile.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.profile.InspectionObjectProfileDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.profile.InspectionObjectProfileMapper;
import cn.cheers.x.inspection.inspection_content.service.profile.ObjectProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 对象巡检类型台账查询服务实现。
 */
@Service
@RequiredArgsConstructor
public class ObjectProfileQueryServiceImpl implements ObjectProfileQueryService {

    private final InspectionObjectProfileMapper profileMapper;

    @Override
    public String requireConsistentInspectionType(Long facilityId, Collection<Long> objectIds) {
        if (facilityId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "facilityId 不能为空");
        }
        if (objectIds == null || objectIds.isEmpty()) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "objectIds 不能为空");
        }

        List<Long> requestedObjectIds = objectIds.stream()
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (requestedObjectIds.isEmpty()) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "objectIds 不能为空");
        }

        List<InspectionObjectProfileDO> profiles = profileMapper.selectByFacilityAndObjectIds(facilityId, requestedObjectIds);
        Map<Long, InspectionObjectProfileDO> profileByObjectId = profiles.stream()
                .collect(Collectors.toMap(InspectionObjectProfileDO::getObjectId, profile -> profile, (a, b) -> a));

        Set<Long> missingObjectIds = new LinkedHashSet<>();
        for (Long objectId : requestedObjectIds) {
            if (!profileByObjectId.containsKey(objectId)) {
                missingObjectIds.add(objectId);
            }
        }
        if (!missingObjectIds.isEmpty()) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST,
                    "缺少对象巡检类型台账，objectIds=" + missingObjectIds);
        }

        Set<String> inspectionTypes = profiles.stream()
                .map(InspectionObjectProfileDO::getInspectionType)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (inspectionTypes.size() != 1) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST,
                    "对象巡检类型不一致，types=" + inspectionTypes);
        }
        return inspectionTypes.iterator().next();
    }
}
