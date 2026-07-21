package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import cn.cheers.x.module.platform.topology.dal.dataobject.PathNetworkDO;
import cn.cheers.x.module.platform.topology.dal.mysql.PathNetworkMapper;
import cn.cheers.x.module.platform.topology.dal.mysql.PathPortalMapper;
import cn.cheers.x.module.platform.topology.enums.GraphStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathNetworkServicePublishedFilterTest {

    @InjectMocks
    private PathNetworkServiceImpl pathNetworkService;

    @Mock
    private PathNetworkMapper pathNetworkMapper;

    @Mock
    private PathPortalMapper pathPortalMapper;

    @Test
    void listPublished_excludesDraft() {
        Long facilityId = 7L;
        PathNetworkDO draft = PathNetworkDO.builder()
                .id("net_7_site_draft")
                .facilityId(facilityId)
                .status(GraphStatus.DRAFT)
                .displayName("Draft network")
                .nodes("[]")
                .edges("[]")
                .build();
        PathNetworkDO published = PathNetworkDO.builder()
                .id("net_7_abc123")
                .facilityId(facilityId)
                .status(GraphStatus.PUBLISHED)
                .displayName("Published network")
                .applicableEquipmentTypes("[\"HUMAN\"]")
                .nodes("[]")
                .edges("[]")
                .build();
        when(pathNetworkMapper.selectAllByFacilityId(facilityId)).thenReturn(List.of(draft, published));

        List<PathNetworkSummaryDTO> list = pathNetworkService.listPublished(facilityId);

        assertEquals(1, list.size());
        assertEquals(GraphStatus.PUBLISHED, list.get(0).getStatus());
        assertEquals("net_7_abc123", list.get(0).getNetworkRef());
    }

    @Test
    void listPublished_includesPublishedCompanionRows() {
        Long facilityId = 7L;
        PathNetworkDO draft = PathNetworkDO.builder()
                .id("net_7_site_draft")
                .facilityId(facilityId)
                .status(GraphStatus.DRAFT)
                .displayName("Draft network")
                .nodes("[]")
                .edges("[]")
                .build();
        PathNetworkDO publishedCompanion = PathNetworkDO.builder()
                .id("net_7_site_published")
                .facilityId(facilityId)
                .status(GraphStatus.PUBLISHED)
                .displayName("Published companion")
                .applicableEquipmentTypes("[\"UAV\"]")
                .nodes("[]")
                .edges("[]")
                .build();
        when(pathNetworkMapper.selectAllByFacilityId(facilityId))
                .thenReturn(List.of(draft, publishedCompanion));

        List<PathNetworkSummaryDTO> list = pathNetworkService.listPublished(facilityId);

        assertEquals(1, list.size());
        assertEquals("net_7_site_published", list.get(0).getNetworkRef());
    }

    @Test
    void listPublished_nullFacilityId_returnsEmpty() {
        assertEquals(List.of(), pathNetworkService.listPublished(null));
    }
}
