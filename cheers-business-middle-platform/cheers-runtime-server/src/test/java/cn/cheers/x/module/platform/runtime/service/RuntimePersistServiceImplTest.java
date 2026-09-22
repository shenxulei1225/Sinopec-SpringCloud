package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.runtime.RuntimeJobDTO;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ResourceReservationDO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ResourceReservationMapper;
import cn.cheers.x.module.platform.runtime.dal.mysql.RuntimeJobMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuntimePersistServiceImplTest {

    @Mock
    private RuntimeJobMapper runtimeJobMapper;
    @Mock
    private ResourceReservationMapper resourceReservationMapper;

    @InjectMocks
    private RuntimePersistServiceImpl runtimePersistService;

    @Test
    void persist_existingCancelledSameId_updatesInsteadOfInsert() {
        ResourceReservationDO existing = ResourceReservationDO.builder()
                .id("bc40c82b-b9c8-4d6d-add6-316655e21a34")
                .runtimeJobId("old-job")
                .candidateStatus(SlotStatus.CANCELLED.name())
                .build();
        when(resourceReservationMapper.selectById("bc40c82b-b9c8-4d6d-add6-316655e21a34"))
                .thenReturn(existing);

        runtimePersistService.saveJobWithReservations(
                RuntimeJobDTO.builder()
                        .runtimeJobId("new-job")
                        .entityTypeCode("task")
                        .status(RuntimeJobStatus.SCHEDULED)
                        .build(),
                List.of(ResourceReservationDTO.builder()
                        .candidateId("bc40c82b-b9c8-4d6d-add6-316655e21a34")
                        .runtimeJobId("new-job")
                        .workId("patrol-task-1-0")
                        .candidateStatus(SlotStatus.PLANNED)
                        .build()),
                44L);

        verify(runtimeJobMapper).insert(any(RuntimeJobDO.class));
        ArgumentCaptor<ResourceReservationDO> captor = ArgumentCaptor.forClass(ResourceReservationDO.class);
        verify(resourceReservationMapper).updateById(captor.capture());
        verify(resourceReservationMapper, never()).insert(any(ResourceReservationDO.class));
        assertEquals("bc40c82b-b9c8-4d6d-add6-316655e21a34", captor.getValue().getId());
        assertEquals("new-job", captor.getValue().getRuntimeJobId());
        assertEquals(SlotStatus.PLANNED.name(), captor.getValue().getCandidateStatus());
    }

    @Test
    void persist_newId_inserts() {
        when(resourceReservationMapper.selectById("new-slot")).thenReturn(null);

        runtimePersistService.saveJobWithReservations(
                RuntimeJobDTO.builder()
                        .runtimeJobId("new-job")
                        .entityTypeCode("task")
                        .status(RuntimeJobStatus.SCHEDULED)
                        .build(),
                List.of(ResourceReservationDTO.builder()
                        .candidateId("new-slot")
                        .runtimeJobId("new-job")
                        .workId("patrol-task-1-0")
                        .candidateStatus(SlotStatus.PLANNED)
                        .build()),
                44L);

        verify(resourceReservationMapper).insert(any(ResourceReservationDO.class));
        verify(resourceReservationMapper, never()).updateById(any(ResourceReservationDO.class));
    }
}
