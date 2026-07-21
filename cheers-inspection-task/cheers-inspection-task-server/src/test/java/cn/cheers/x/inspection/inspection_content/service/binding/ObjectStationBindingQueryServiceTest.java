package cn.cheers.x.inspection.inspection_content.service.binding;

import cn.cheers.x.inspection.inspection_content.dal.dataobject.binding.ObjectStationBindingDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.binding.ObjectStationBindingMapper;
import cn.cheers.x.inspection.inspection_content.service.binding.impl.ObjectStationBindingQueryServiceImpl;
import cn.cheers.x.inspection.inspection_content.service.binding.model.BindingResolveResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjectStationBindingQueryServiceTest {

    @Mock
    private ObjectStationBindingMapper bindingMapper;

    @InjectMocks
    private ObjectStationBindingQueryServiceImpl queryService;

    @Test
    void listByObjectIds_missingBinding_reportsGap() {
        when(bindingMapper.selectByFacilityAndObjectIds(1L, List.of(10L, 11L)))
                .thenReturn(List.of(binding(10L, "n_sta_1")));
        BindingResolveResult r = queryService.listByObjectIds(1L, List.of(10L, 11L));
        assertEquals(List.of(11L), r.getMissingObjectIds());
        assertTrue(r.getBindingsByObjectId().containsKey(10L));
    }

    private static ObjectStationBindingDO binding(Long objectId, String stationNodeId) {
        ObjectStationBindingDO binding = new ObjectStationBindingDO();
        binding.setFacilityId(1L);
        binding.setObjectId(objectId);
        binding.setStationNodeId(stationNodeId);
        binding.setSortNo(0);
        return binding;
    }
}
