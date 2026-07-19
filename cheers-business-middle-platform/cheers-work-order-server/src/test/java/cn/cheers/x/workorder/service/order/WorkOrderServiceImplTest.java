package cn.cheers.x.workorder.service.order;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.maintenance.api.MaintenanceApi;
import cn.cheers.x.workorder.api.dto.WorkOrderCreateReqDTO;
import cn.cheers.x.workorder.controller.admin.vo.order.WorkOrderStepCompleteReqVO;
import cn.cheers.x.workorder.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.workorder.dal.dataobject.WorkOrderDO;
import cn.cheers.x.workorder.dal.dataobject.WorkOrderStepResultDO;
import cn.cheers.x.workorder.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.workorder.dal.mysql.WorkOrderMapper;
import cn.cheers.x.workorder.dal.mysql.WorkOrderStepResultMapper;
import cn.cheers.x.workorder.enums.FieldWorkStandardStatusEnum;
import cn.cheers.x.workorder.enums.WorkOrderStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * WorkOrderService 生命周期单元测试（Mockito，不启 Spring）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WorkOrderService 单元测试")
class WorkOrderServiceImplTest {

    @Mock
    private WorkOrderMapper workOrderMapper;
    @Mock
    private WorkOrderStepResultMapper workOrderStepResultMapper;
    @Mock
    private FieldWorkStandardMapper fieldWorkStandardMapper;
    @Mock
    private MaintenanceApi maintenanceApi;

    @InjectMocks
    private WorkOrderServiceImpl workOrderService;

    @Test
    @DisplayName("create→start→completeStep→complete 成功路径")
    void lifecycle_createStartCompleteStepComplete_success() {
        FieldWorkStandardDO published = publishedStandard();
        when(fieldWorkStandardMapper.selectById(20L)).thenReturn(published);
        when(workOrderMapper.selectLatestByWoNoPrefix(any())).thenReturn(null);

        AtomicLong woId = new AtomicLong();
        doAnswer(invocation -> {
            WorkOrderDO row = invocation.getArgument(0);
            row.setId(100L);
            woId.set(100L);
            return 1;
        }).when(workOrderMapper).insert(any(WorkOrderDO.class));
        doAnswer(invocation -> {
            WorkOrderStepResultDO row = invocation.getArgument(0);
            if (row.getId() == null) {
                row.setId("s1".equals(row.getStepCode()) ? 1001L : 1002L);
            }
            return 1;
        }).when(workOrderStepResultMapper).insert(any(WorkOrderStepResultDO.class));

        Long id = workOrderService.createFromDispatch(buildCreateReq());
        assertEquals(100L, id);

        ArgumentCaptor<WorkOrderDO> woCaptor = ArgumentCaptor.forClass(WorkOrderDO.class);
        verify(workOrderMapper).insert(woCaptor.capture());
        WorkOrderDO created = woCaptor.getValue();
        assertEquals(WorkOrderStatusEnum.DISPATCHED.getStatus(), created.getStatus());
        assertTrue(created.getWoNo().startsWith("WO-"));
        assertTrue(created.getStandardSnapshotJson().contains("s1"));
        assertTrue(created.getStandardSnapshotJson().contains("s2"));
        verify(workOrderStepResultMapper, times(2)).insert(any(WorkOrderStepResultDO.class));

        when(workOrderMapper.selectById(100L)).thenReturn(WorkOrderDO.builder()
                .id(100L)
                .woNo(created.getWoNo())
                .status(WorkOrderStatusEnum.DISPATCHED.getStatus())
                .standardSnapshotJson(created.getStandardSnapshotJson())
                .build());
        workOrderService.start(100L);
        ArgumentCaptor<WorkOrderDO> startCaptor = ArgumentCaptor.forClass(WorkOrderDO.class);
        verify(workOrderMapper, times(1)).updateById(startCaptor.capture());
        assertEquals(WorkOrderStatusEnum.IN_PROGRESS.getStatus(), startCaptor.getValue().getStatus());

        when(workOrderMapper.selectById(100L)).thenReturn(WorkOrderDO.builder()
                .id(100L)
                .woNo(created.getWoNo())
                .status(WorkOrderStatusEnum.IN_PROGRESS.getStatus())
                .standardSnapshotJson(created.getStandardSnapshotJson())
                .build());
        when(workOrderStepResultMapper.selectByWorkOrderIdAndStepCode(100L, "s1"))
                .thenReturn(WorkOrderStepResultDO.builder().id(1001L).workOrderId(100L).stepCode("s1")
                        .completed(false).build());
        when(workOrderStepResultMapper.selectByWorkOrderIdAndStepCode(100L, "s2"))
                .thenReturn(WorkOrderStepResultDO.builder().id(1002L).workOrderId(100L).stepCode("s2")
                        .completed(false).build());

        workOrderService.completeStep(100L, "s1", new WorkOrderStepCompleteReqVO());
        workOrderService.completeStep(100L, "s2", null);

        when(workOrderStepResultMapper.selectListByWorkOrderId(100L)).thenReturn(List.of(
                WorkOrderStepResultDO.builder().stepCode("s1").completed(true).build(),
                WorkOrderStepResultDO.builder().stepCode("s2").completed(true).build()
        ));
        workOrderService.complete(100L);

        ArgumentCaptor<WorkOrderDO> completeCaptor = ArgumentCaptor.forClass(WorkOrderDO.class);
        verify(workOrderMapper, times(2)).updateById(completeCaptor.capture());
        assertEquals(WorkOrderStatusEnum.COMPLETED.getStatus(),
                completeCaptor.getAllValues().get(1).getStatus());
    }

    @Test
    @DisplayName("complete：必填步骤未完成时抛错")
    void complete_requiredStepIncomplete_throws() {
        String snapshot = "[{\"code\":\"s1\",\"title\":\"外观检查\",\"required\":true,\"controlType\":\"checkbox\"},"
                + "{\"code\":\"s2\",\"title\":\"备注\",\"required\":false,\"controlType\":\"text\"}]";
        when(workOrderMapper.selectById(100L)).thenReturn(WorkOrderDO.builder()
                .id(100L)
                .status(WorkOrderStatusEnum.IN_PROGRESS.getStatus())
                .standardSnapshotJson(snapshot)
                .build());
        when(workOrderStepResultMapper.selectListByWorkOrderId(100L)).thenReturn(List.of(
                WorkOrderStepResultDO.builder().stepCode("s1").completed(false).build(),
                WorkOrderStepResultDO.builder().stepCode("s2").completed(true).build()
        ));

        assertThrows(ServiceException.class, () -> workOrderService.complete(100L));
    }

    @Test
    @DisplayName("create：草稿标准不可派工")
    void create_draftStandard_throws() {
        when(fieldWorkStandardMapper.selectById(10L)).thenReturn(FieldWorkStandardDO.builder()
                .id(10L)
                .code("pump-monthly")
                .status(FieldWorkStandardStatusEnum.DRAFT.getStatus())
                .stepsJson("[{\"code\":\"s1\",\"title\":\"外观检查\",\"required\":true,\"controlType\":\"checkbox\"}]")
                .versionNo(1)
                .build());

        WorkOrderCreateReqDTO req = buildCreateReq();
        req.setStandardId(10L);
        assertThrows(ServiceException.class, () -> workOrderService.createFromDispatch(req));
    }

    @Test
    @DisplayName("create：按 code 取最新已发布版本并初始化步骤结果 completed=false")
    void create_byStandardCode_insertsStepResultsIncomplete() {
        when(fieldWorkStandardMapper.selectLatestPublishedByCode("pump-monthly")).thenReturn(publishedStandard());
        when(workOrderMapper.selectLatestByWoNoPrefix(any())).thenReturn(null);
        doAnswer(invocation -> {
            WorkOrderDO row = invocation.getArgument(0);
            row.setId(200L);
            return 1;
        }).when(workOrderMapper).insert(any(WorkOrderDO.class));

        ArgumentCaptor<WorkOrderStepResultDO> stepCaptor = ArgumentCaptor.forClass(WorkOrderStepResultDO.class);
        doAnswer(invocation -> 1).when(workOrderStepResultMapper).insert(stepCaptor.capture());

        WorkOrderCreateReqDTO req = buildCreateReq();
        req.setStandardId(null);
        req.setStandardCode("pump-monthly");
        Long id = workOrderService.createFromDispatch(req);

        assertEquals(200L, id);
        List<WorkOrderStepResultDO> steps = stepCaptor.getAllValues();
        assertEquals(2, steps.size());
        assertFalse(steps.get(0).getCompleted());
        assertFalse(steps.get(1).getCompleted());
        assertEquals("s1", steps.get(0).getStepCode());
        assertEquals("s2", steps.get(1).getStepCode());
    }

    private static WorkOrderCreateReqDTO buildCreateReq() {
        WorkOrderCreateReqDTO req = new WorkOrderCreateReqDTO();
        req.setScope("inspection");
        req.setTitle("离心泵月检工单");
        req.setStandardId(20L);
        req.setAssetId(1001L);
        req.setAssetTypeCode("pump");
        req.setFrequencyCode("MONTHLY");
        return req;
    }

    private static FieldWorkStandardDO publishedStandard() {
        return FieldWorkStandardDO.builder()
                .id(20L)
                .code("pump-monthly")
                .name("离心泵月检")
                .scope("inspection")
                .versionNo(2)
                .status(FieldWorkStandardStatusEnum.PUBLISHED.getStatus())
                .stepsJson("[{\"code\":\"s1\",\"title\":\"外观检查\",\"required\":true,\"controlType\":\"checkbox\"},"
                        + "{\"code\":\"s2\",\"title\":\"振动记录\",\"required\":true,\"controlType\":\"number\"}]")
                .build();
    }

}
