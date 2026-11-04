package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndustrialEquipmentControlTest {

    private static final String RECIPE = """
            {"industrialEquipment":{"equipmentModelCode":"MODEL-FAN","writes":[{"fieldCode":"F-open","value":1}]}}
            """;

    @Mock
    private IndustrialPointBooks books;
    @Mock
    private GatewaySessionWrites session;
    @Mock
    private IndustrialEquipmentBoard board;
    @InjectMocks
    private IndustrialEquipmentControl control;

    @Test
    void refusesWhenTheGatewaySessionIsNotOpen() {
        when(books.action(123L, "act-fan-on")).thenReturn(new IndustrialPointBooks.ActionRow("DEVICE", RECIPE));
        when(books.equipmentModelCode(123L, 25629L)).thenReturn("MODEL-FAN");
        when(session.attached()).thenReturn(false);

        ServiceException error = assertThrows(ServiceException.class,
                () -> control.submit(123L, "act-fan-on", 25629L));

        assertEquals(GatewaySessionWrites.OPC_UA_SERVER_OFFLINE, error.getMessage());
        verify(session, never()).nodeByBinding();
        verify(session, never()).write(anyList());
    }

    @Test
    void stillRefusesWhenTheOpenSessionHasNoNodeForThisWritePoint() {
        when(books.action(123L, "act-fan-on")).thenReturn(new IndustrialPointBooks.ActionRow("DEVICE", RECIPE));
        when(books.equipmentModelCode(123L, 25629L)).thenReturn("MODEL-FAN");
        when(session.attached()).thenReturn(true);
        when(books.writeRows(123L, 25629L)).thenReturn(List.of(
                new IndustrialWritePlan.BoundWrite("F-open", "HSDJ10/FJ2_PT_OPEN")));
        when(session.nodeByBinding()).thenReturn(Map.of());

        ServiceException error = assertThrows(ServiceException.class,
                () -> control.submit(123L, "act-fan-on", 25629L));

        assertEquals("这个写点还没有节点标识", error.getMessage());
        verify(session, never()).write(anyList());
    }
}
