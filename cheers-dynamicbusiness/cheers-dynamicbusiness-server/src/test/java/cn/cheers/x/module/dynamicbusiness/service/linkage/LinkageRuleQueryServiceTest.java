package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkageRuleQueryServiceTest {

    @Mock
    private EntityRelationService entityRelationService;
    @Mock
    private EntityRepository entityRepository;
    @InjectMocks
    private LinkageRuleQueryService service;

    @Test
    void eventTopicUsesRelationProjectionAndFiltersDomainFacilityAndStatus() {
        when(entityRelationService.listSubjectEntityIdsByRefFieldAndTargetIds(
                LinkageFieldCodes.EVENT_TOPICS, LinkageFieldCodes.LINKAGE_DIRECTORY, List.of(41L)))
                .thenReturn(List.of(101L, 102L, 103L, 104L, 105L));
        when(entityRepository.findByIdsWithDedicatedBaseFields(
                List.of(101L, 102L, 103L, 104L, 105L), LinkageFieldCodes.LINKAGE_DIRECTORY))
                .thenReturn(List.of(
                        linkage(101L, "alarm", 1, 9L, LinkageFieldCodes.TRIGGER_ANY),
                        linkage(102L, "inspection", 1, 9L, LinkageFieldCodes.TRIGGER_ANY),
                        linkage(103L, "alarm", 0, 9L, LinkageFieldCodes.TRIGGER_ANY),
                        linkage(104L, "alarm", 1, 10L, LinkageFieldCodes.TRIGGER_ANY),
                        linkage(105L, "alarm", 1, 9L, "ALL")));

        List<EntityDO> found = service.findEnabledAlarmRules(41L, 9L);

        assertEquals(List.of(101L), found.stream().map(EntityDO::getId).toList());
        verify(entityRelationService).listSubjectEntityIdsByRefFieldAndTargetIds(
                LinkageFieldCodes.EVENT_TOPICS, LinkageFieldCodes.LINKAGE_DIRECTORY, List.of(41L));
    }

    private static EntityDO linkage(long id, String domain, int status, long facilityId, String triggerMode) {
        return EntityDO.builder()
                .id(id)
                .entityTypeCode(LinkageFieldCodes.LINKAGE_DIRECTORY)
                .domain(domain)
                .status(status)
                .dedicatedBaseFieldValues(Map.of(
                        FacilityOwningFieldCodes.FIELD_CODE,
                        FacilityOwningFieldCodes.toApiRef(facilityId),
                        LinkageFieldCodes.TRIGGER_MODE, triggerMode))
                .build();
    }
}
