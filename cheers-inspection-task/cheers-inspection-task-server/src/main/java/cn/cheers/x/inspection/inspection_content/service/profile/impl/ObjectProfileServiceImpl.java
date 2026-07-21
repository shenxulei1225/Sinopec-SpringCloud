package cn.cheers.x.inspection.inspection_content.service.profile.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.profile.InspectionObjectProfileDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.profile.InspectionObjectProfileMapper;
import cn.cheers.x.inspection.inspection_content.enums.InspectionTypeEnum;
import cn.cheers.x.inspection.inspection_content.service.profile.ObjectProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 对象巡检类型台账写服务实现。
 */
@Service
@RequiredArgsConstructor
public class ObjectProfileServiceImpl implements ObjectProfileService {

    private final InspectionObjectProfileMapper profileMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsert(Long facilityId, Long objectId, String inspectionType, Integer defaultWorkMinutes) {
        if (facilityId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "facilityId 不能为空");
        }
        if (objectId == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "objectId 不能为空");
        }
        InspectionTypeEnum.validate(inspectionType);

        InspectionObjectProfileDO existing = profileMapper.selectByFacilityAndObjectId(facilityId, objectId);
        if (existing == null) {
            InspectionObjectProfileDO profile = new InspectionObjectProfileDO();
            profile.setFacilityId(facilityId);
            profile.setObjectId(objectId);
            profile.setInspectionType(inspectionType);
            profile.setDefaultWorkMinutes(defaultWorkMinutes);
            profileMapper.insert(profile);
            return;
        }

        existing.setInspectionType(inspectionType);
        existing.setDefaultWorkMinutes(defaultWorkMinutes);
        profileMapper.updateById(existing);
    }
}
