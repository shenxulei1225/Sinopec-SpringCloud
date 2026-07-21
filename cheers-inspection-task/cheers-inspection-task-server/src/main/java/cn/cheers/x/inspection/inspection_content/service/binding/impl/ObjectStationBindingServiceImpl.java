package cn.cheers.x.inspection.inspection_content.service.binding.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.binding.ObjectStationBindingDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.binding.ObjectStationBindingMapper;
import cn.cheers.x.inspection.inspection_content.service.binding.ObjectStationBindingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 对象↔停靠点绑定写服务实现。
 */
@Service
@RequiredArgsConstructor
public class ObjectStationBindingServiceImpl implements ObjectStationBindingService {

    private final ObjectStationBindingMapper bindingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceBindings(Long facilityId, Long objectId, List<String> stationNodeIds, Integer workMinutesPerStop) {
        validateReplaceArgs(facilityId, objectId, stationNodeIds);

        bindingMapper.deleteByFacilityAndObjectId(facilityId, objectId);
        if (stationNodeIds.isEmpty()) {
            return;
        }

        for (int i = 0; i < stationNodeIds.size(); i++) {
            ObjectStationBindingDO binding = new ObjectStationBindingDO();
            binding.setFacilityId(facilityId);
            binding.setObjectId(objectId);
            binding.setStationNodeId(stationNodeIds.get(i));
            binding.setWorkMinutes(workMinutesPerStop);
            binding.setSortNo(i);
            bindingMapper.insert(binding);
        }
    }

    private static void validateReplaceArgs(Long facilityId, Long objectId, List<String> stationNodeIds) {
        if (facilityId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "facilityId 不能为空");
        }
        if (objectId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "objectId 不能为空");
        }
        if (stationNodeIds == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "stationNodeIds 不能为空");
        }
        for (String stationNodeId : stationNodeIds) {
            if (stationNodeId == null || stationNodeId.isBlank()) {
                throw ServiceExceptionUtil.exception(BAD_REQUEST, "stationNodeId 不能为空");
            }
        }
    }
}
