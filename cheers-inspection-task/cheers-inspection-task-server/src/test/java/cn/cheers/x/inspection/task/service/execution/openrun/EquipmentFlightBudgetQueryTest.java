package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EquipmentFlightBudgetQueryTest {

    @Test
    void instanceMinutesWinOverModelDefault() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        EntityRespDTO equipment = new EntityRespDTO();
        equipment.setCustomFields(Map.of("max_flight_time", 30));
        equipment.setBaseFields(Map.of("modelId", 1867L));
        when(api.getEntity(9L, "equipment")).thenReturn(CommonResult.success(equipment));
        when(api.getModelFieldDefault(1867L, "max_flight_time")).thenReturn(CommonResult.success("41"));

        EquipmentFlightBudgetQuery.Snapshot snapshot = new EquipmentFlightBudgetQuery(api).load(9L);

        assertEquals(1800, snapshot.fullBatterySeconds());
    }

    @Test
    void emptyInstanceUsesModelDefault() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        EntityRespDTO equipment = new EntityRespDTO();
        equipment.setCustomFields(Map.of());
        equipment.setBaseFields(Map.of("modelId", 1867L));
        when(api.getEntity(9L, "equipment")).thenReturn(CommonResult.success(equipment));
        when(api.getModelFieldDefault(1867L, "max_flight_time")).thenReturn(CommonResult.success("41"));

        EquipmentFlightBudgetQuery.Snapshot snapshot = new EquipmentFlightBudgetQuery(api).load(9L);

        assertEquals(2460, snapshot.fullBatterySeconds());
    }

    @Test
    void bothEmptyStayEmpty() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        EntityRespDTO equipment = new EntityRespDTO();
        equipment.setCustomFields(Map.of());
        equipment.setBaseFields(Map.of("modelId", 1773L));
        when(api.getEntity(9L, "equipment")).thenReturn(CommonResult.success(equipment));
        when(api.getModelFieldDefault(1773L, "max_flight_time")).thenReturn(CommonResult.success(null));

        EquipmentFlightBudgetQuery.Snapshot snapshot = new EquipmentFlightBudgetQuery(api).load(9L);

        assertNull(snapshot.fullBatterySeconds());
    }
}
