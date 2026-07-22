package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.profile.InspectionObjectProfileDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.profile.InspectionObjectProfileMapper;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.inspection_content.service.binding.ObjectStationBindingQueryService;
import cn.cheers.x.inspection.inspection_content.service.binding.model.BindingResolveResult;
import cn.cheers.x.inspection.inspection_content.service.binding.model.ObjectStationBindingView;
import cn.cheers.x.inspection.inspection_content.service.orchestration.impl.PatrolExpandMapServiceImpl;
import cn.cheers.x.inspection.inspection_content.service.profile.ObjectProfileQueryService;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatrolExpandMapService 单元测试")
class PatrolExpandMapServiceTest {

    private static final Long FACILITY_ID = 1L;

    @Mock
    private ObjectProfileQueryService objectProfileQueryService;
    @Mock
    private ObjectStationBindingQueryService bindingQueryService;
    @Mock
    private InspectionObjectProfileMapper profileMapper;
    @Mock
    private InspectionTaskMapper taskMapper;
    @Mock
    private InspectionRoutePlanMapper routePlanMapper;
    @Mock
    private PathNetworkApi pathNetworkApi;

    private PatrolExpandMapServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PatrolExpandMapServiceImpl(
                objectProfileQueryService,
                bindingQueryService,
                profileMapper,
                taskMapper,
                routePlanMapper,
                pathNetworkApi,
                new ObjectMapper());
    }

    @Test
    @DisplayName("0 条匹配路网 → 显式失败")
    void expand_zeroMatchingNetworks_fails() {
        stubHappyBindings();
        when(objectProfileQueryService.requireConsistentInspectionType(FACILITY_ID, List.of(10L)))
                .thenReturn("HUMAN");
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-uav", List.of("UAV"), "PUBLISHED"))));

        assertThrows(ServiceException.class, () -> service.expand(req(List.of(10L), null)));
    }

    @Test
    @DisplayName("1 条匹配路网 → 选用并展开 stopIds")
    void expand_oneMatchingNetwork_succeeds() {
        stubHappyBindings();
        when(objectProfileQueryService.requireConsistentInspectionType(FACILITY_ID, List.of(10L)))
                .thenReturn("HUMAN");
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));
        when(profileMapper.selectByFacilityAndObjectIds(eq(FACILITY_ID), eq(List.of(10L))))
                .thenReturn(List.of(profile(10L, 5)));

        PatrolExpandRespDTO resp = service.expand(req(List.of(10L), null));

        assertEquals("net-a", resp.getNetworkRef());
        assertEquals(List.of("sta-1"), resp.getStopIds());
        assertEquals("HUMAN", resp.getInspectionType());
        assertEquals(5, resp.getWorkItems().get(0).getPayload().get("workMinutes"));
    }

    @Test
    @DisplayName("expand：透传 startStopId 到工作项 payload")
    void expand_forwardsStartStopId() {
        stubHappyBindings();
        when(objectProfileQueryService.requireConsistentInspectionType(FACILITY_ID, List.of(10L)))
                .thenReturn("HUMAN");
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"))));
        when(profileMapper.selectByFacilityAndObjectIds(eq(FACILITY_ID), eq(List.of(10L))))
                .thenReturn(List.of(profile(10L, 5)));

        PatrolExpandReqDTO request = req(List.of(10L), null);
        request.setStartStopId("sta-depot");
        request.setReturnToStart(Boolean.TRUE);

        PatrolExpandRespDTO resp = service.expand(request);

        Map<String, Object> payload = resp.getWorkItems().get(0).getPayload();
        assertEquals("sta-depot", payload.get("startStopId"));
        assertEquals(Boolean.TRUE, payload.get("returnToStart"));
    }

    @Test
    @DisplayName("多条匹配路网且未指定 preferred → 显式失败")
    void expand_manyNetworksWithoutPreferred_fails() {
        stubHappyBindings();
        when(objectProfileQueryService.requireConsistentInspectionType(FACILITY_ID, List.of(10L)))
                .thenReturn("HUMAN");
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"),
                network("net-b", List.of("HUMAN"), "PUBLISHED"))));

        assertThrows(ServiceException.class, () -> service.expand(req(List.of(10L), null)));
    }

    @Test
    @DisplayName("多条匹配路网且 preferred 命中 → 选用指定路网")
    void expand_manyNetworksWithPreferred_succeeds() {
        stubHappyBindings();
        when(objectProfileQueryService.requireConsistentInspectionType(FACILITY_ID, List.of(10L)))
                .thenReturn("HUMAN");
        when(pathNetworkApi.listPublished(FACILITY_ID)).thenReturn(CommonResult.success(List.of(
                network("net-a", List.of("HUMAN"), "PUBLISHED"),
                network("net-b", List.of("HUMAN"), "PUBLISHED"))));
        when(profileMapper.selectByFacilityAndObjectIds(eq(FACILITY_ID), eq(List.of(10L))))
                .thenReturn(List.of(profile(10L, 8)));

        PatrolExpandRespDTO resp = service.expand(req(List.of(10L), "net-b"));

        assertEquals("net-b", resp.getNetworkRef());
    }

    @Test
    @DisplayName("缺对象↔停靠点绑定 → 列出缺口 objectId")
    void expand_missingBinding_fails() {
        BindingResolveResult result = new BindingResolveResult();
        result.setMissingObjectIds(List.of(11L));
        when(objectProfileQueryService.requireConsistentInspectionType(FACILITY_ID, List.of(10L, 11L)))
                .thenReturn("HUMAN");
        when(bindingQueryService.listByObjectIds(FACILITY_ID, List.of(10L, 11L))).thenReturn(result);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.expand(req(List.of(10L, 11L), null)));
        assertTrue(ex.getMessage() != null && ex.getMessage().contains("11"));
    }

    @Test
    @DisplayName("对象巡检类型不一致 → 显式失败")
    void expand_inconsistentInspectionType_fails() {
        when(objectProfileQueryService.requireConsistentInspectionType(FACILITY_ID, List.of(10L, 11L)))
                .thenThrow(new ServiceException(400, "对象巡检类型不一致"));

        assertThrows(ServiceException.class, () -> service.expand(req(List.of(10L, 11L), null)));
    }

    private void stubHappyBindings() {
        ObjectStationBindingView binding = new ObjectStationBindingView();
        binding.setObjectId(10L);
        binding.setStationNodeId("sta-1");
        binding.setWorkMinutes(5);
        binding.setSortNo(0);

        BindingResolveResult result = new BindingResolveResult();
        Map<Long, List<ObjectStationBindingView>> grouped = new LinkedHashMap<>();
        grouped.put(10L, List.of(binding));
        result.setBindingsByObjectId(grouped);
        result.setMissingObjectIds(List.of());

        when(bindingQueryService.listByObjectIds(FACILITY_ID, List.of(10L))).thenReturn(result);
    }

    private static PatrolExpandReqDTO req(List<Long> objectIds, String preferredNetworkRef) {
        return PatrolExpandReqDTO.builder()
                .facilityId(FACILITY_ID)
                .objectIds(objectIds)
                .preferredNetworkRef(preferredNetworkRef)
                .build();
    }

    private static PathNetworkSummaryDTO network(String ref, List<String> types, String status) {
        return PathNetworkSummaryDTO.builder()
                .networkRef(ref)
                .facilityId(FACILITY_ID)
                .status(status)
                .applicableEquipmentTypes(types)
                .build();
    }

    private static InspectionObjectProfileDO profile(Long objectId, Integer defaultWorkMinutes) {
        InspectionObjectProfileDO profile = new InspectionObjectProfileDO();
        profile.setFacilityId(FACILITY_ID);
        profile.setObjectId(objectId);
        profile.setInspectionType("HUMAN");
        profile.setDefaultWorkMinutes(defaultWorkMinutes);
        return profile;
    }
}
