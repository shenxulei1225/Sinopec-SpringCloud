package cn.cheers.x.inspection.inspection_content.service.profile;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.profile.InspectionObjectProfileDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.profile.InspectionObjectProfileMapper;
import cn.cheers.x.inspection.inspection_content.service.profile.impl.ObjectProfileQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjectProfileQueryServiceTest {

    @Mock
    private InspectionObjectProfileMapper profileMapper;

    @InjectMocks
    private ObjectProfileQueryServiceImpl queryService;

    @Test
    void requireConsistentInspectionType_allSame_returnsType() {
        when(profileMapper.selectByFacilityAndObjectIds(1L, List.of(10L, 11L)))
                .thenReturn(List.of(profile(10L, "HUMAN"), profile(11L, "HUMAN")));
        assertEquals("HUMAN", queryService.requireConsistentInspectionType(1L, List.of(10L, 11L)));
    }

    @Test
    void requireConsistentInspectionType_mixedTypes_throws() {
        when(profileMapper.selectByFacilityAndObjectIds(1L, List.of(10L, 11L)))
                .thenReturn(List.of(profile(10L, "HUMAN"), profile(11L, "UAV")));
        assertThrows(ServiceException.class,
                () -> queryService.requireConsistentInspectionType(1L, List.of(10L, 11L)));
    }

    private static InspectionObjectProfileDO profile(Long objectId, String inspectionType) {
        InspectionObjectProfileDO profile = new InspectionObjectProfileDO();
        profile.setFacilityId(1L);
        profile.setObjectId(objectId);
        profile.setInspectionType(inspectionType);
        return profile;
    }
}
