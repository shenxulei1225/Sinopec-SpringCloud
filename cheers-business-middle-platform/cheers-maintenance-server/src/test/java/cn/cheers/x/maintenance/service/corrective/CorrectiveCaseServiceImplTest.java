package cn.cheers.x.maintenance.service.corrective;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.maintenance.controller.admin.vo.corrective.CorrectiveCaseCreateReqVO;
import cn.cheers.x.maintenance.controller.admin.vo.corrective.CorrectiveDispatchReqVO;
import cn.cheers.x.maintenance.dal.dataobject.CorrectiveCaseDO;
import cn.cheers.x.maintenance.dal.mysql.CorrectiveCaseMapper;
import cn.cheers.x.maintenance.framework.config.MaintenanceCorrectiveProperties;
import cn.cheers.x.workorder.api.WorkOrderApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrectiveCaseServiceImplTest {

    @Mock private CorrectiveCaseMapper correctiveCaseMapper;
    @Mock private WorkOrderApi workOrderApi;
    @Mock private MaintenanceApi maintenanceApi;
    @Mock private CorrectiveApprovalGateway approvalGateway;
    @Mock private MaintenanceCorrectiveProperties correctiveProperties;
    @InjectMocks private CorrectiveCaseServiceImpl service;

    @Test
    @DisplayName("skip-approval：submit 直接 APPROVED")
    void submitApproval_skip() {
        when(correctiveCaseMapper.selectById(1L)).thenReturn(CorrectiveCaseDO.builder()
                .id(1L).status(CorrectiveCaseServiceImpl.ACCEPTED).title("t").build());
        when(correctiveProperties.isSkipApproval()).thenReturn(true);
        service.submitApproval(1L);
        verify(correctiveCaseMapper).updateById(argThat((CorrectiveCaseDO u) ->
                CorrectiveCaseServiceImpl.APPROVED.equals(u.getStatus())));
        verify(approvalGateway, never()).startApproval(any(), any());
    }

    @Test
    @DisplayName("非法状态派工失败")
    void dispatch_wrongStatus() {
        when(correctiveCaseMapper.selectById(1L)).thenReturn(CorrectiveCaseDO.builder()
                .id(1L).status(CorrectiveCaseServiceImpl.ACCEPTED).build());
        assertThrows(ServiceException.class, () -> service.dispatch(1L, new CorrectiveDispatchReqVO()));
    }

    @Test
    @DisplayName("派工创建工单")
    void dispatch_ok() {
        when(correctiveCaseMapper.selectById(1L)).thenReturn(CorrectiveCaseDO.builder()
                .id(1L).caseNo("CR-1").title("漏油").status(CorrectiveCaseServiceImpl.APPROVED)
                .fieldStandardId(9L).assetId(10L).build());
        when(workOrderApi.create(any())).thenReturn(CommonResult.success(55L));
        assertEquals(55L, service.dispatch(1L, new CorrectiveDispatchReqVO()));
        verify(correctiveCaseMapper).updateById(argThat((CorrectiveCaseDO u) ->
                CorrectiveCaseServiceImpl.IN_WORK.equals(u.getStatus()) && Long.valueOf(55L).equals(u.getWorkOrderId())));
    }

    @Test
    void create_setsAccepted() {
        CorrectiveCaseCreateReqVO req = new CorrectiveCaseCreateReqVO();
        req.setTitle("报修");
        doAnswer(inv -> { inv.getArgument(0, CorrectiveCaseDO.class).setId(3L); return 1; })
                .when(correctiveCaseMapper).insert(any(CorrectiveCaseDO.class));
        assertEquals(3L, service.create(req));
    }
}
