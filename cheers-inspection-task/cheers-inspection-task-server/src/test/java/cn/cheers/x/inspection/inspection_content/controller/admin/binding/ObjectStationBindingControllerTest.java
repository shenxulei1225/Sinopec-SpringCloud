package cn.cheers.x.inspection.inspection_content.controller.admin.binding;

import cn.cheers.x.inspection.inspection_content.service.binding.ObjectStationBindingQueryService;
import cn.cheers.x.inspection.inspection_content.service.binding.model.BindingResolveResult;
import cn.cheers.x.inspection.inspection_content.service.binding.model.ObjectStationBindingView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObjectStationBindingController 单元测试")
class ObjectStationBindingControllerTest {

    @Mock
    private ObjectStationBindingQueryService objectStationBindingQueryService;

    @InjectMocks
    private ObjectStationBindingController controller;

    @Test
    @DisplayName("GET /list-by-objects 返回绑定与缺口 objectId")
    void listByObjects_returnsBindingsAndMissingObjectIds() {
        ObjectStationBindingView bound = new ObjectStationBindingView();
        bound.setObjectId(10L);
        bound.setStationNodeId("n_sta_1");
        bound.setSortNo(0);

        BindingResolveResult resolveResult = new BindingResolveResult();
        resolveResult.setBindingsByObjectId(Map.of(10L, List.of(bound)));
        resolveResult.setMissingObjectIds(List.of(11L));

        when(objectStationBindingQueryService.listByObjectIds(1L, List.of(10L, 11L)))
                .thenReturn(resolveResult);

        var result = controller.listByObjects(1L, List.of(10L, 11L));

        assertEquals(0, result.getCode());
        assertEquals(List.of(11L), result.getData().getMissingObjectIds());
        assertTrue(result.getData().getBindingsByObjectId().containsKey(10L));
        assertEquals("n_sta_1",
                result.getData().getBindingsByObjectId().get(10L).get(0).getStationNodeId());
        verify(objectStationBindingQueryService).listByObjectIds(1L, List.of(10L, 11L));
    }
}
