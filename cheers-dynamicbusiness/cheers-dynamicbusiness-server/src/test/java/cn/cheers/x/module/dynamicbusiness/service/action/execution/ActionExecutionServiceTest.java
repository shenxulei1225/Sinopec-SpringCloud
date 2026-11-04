package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionExecutionServiceTest {

    @Mock
    private ActionExecutionMapper actionExecutionMapper;
    @Mock
    private EntityRepository entityRepository;
    @InjectMocks
    private ActionExecutionService service;

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    @Test
    void duplicateIdempotencyKeyReturnsExistingExecution() {
        TenantContextHolder.setTenantId(123L);
        ActionExecutionDO existing = new ActionExecutionDO();
        existing.setId(88L);
        existing.setStatus(ActionExecutionStatus.PENDING);
        when(actionExecutionMapper.selectByIdempotencyKey("evt-1:9:12")).thenReturn(existing);

        ActionExecutionReceipt receipt = service.submit(request());

        assertEquals(88L, receipt.actionExecutionId());
        assertEquals(ActionExecutionStatus.PENDING, receipt.status());
        verify(actionExecutionMapper, never()).insert(org.mockito.ArgumentMatchers.any(ActionExecutionDO.class));
    }

    @Test
    void newCallValidatesReferencesAndWritesPendingExecution() {
        TenantContextHolder.setTenantId(123L);
        when(actionExecutionMapper.selectByIdempotencyKey("evt-1:9:12")).thenReturn(null);
        when(entityRepository.findById(31L, "action")).thenReturn(EntityDO.builder().id(31L).build());
        when(entityRepository.findById(77L, "equipment")).thenReturn(EntityDO.builder().id(77L).build());
        when(actionExecutionMapper.insert(org.mockito.ArgumentMatchers.any(ActionExecutionDO.class))).thenAnswer(invocation -> {
            ActionExecutionDO row = invocation.getArgument(0);
            row.setId(99L);
            return 1;
        });

        ActionExecutionReceipt receipt = service.submit(request());

        assertEquals(99L, receipt.actionExecutionId());
        assertEquals(ActionExecutionStatus.PENDING, receipt.status());
        verify(entityRepository).findById(31L, "action");
        verify(entityRepository).findById(77L, "equipment");
    }

    private static ActionExecutionRequest request() {
        return new ActionExecutionRequest(
                "evt-1:9:12", "LINKAGE", "linkage-execution:5",
                9L,
                31L, "equipment", 77L, Map.of("speed", 2));
    }
}
