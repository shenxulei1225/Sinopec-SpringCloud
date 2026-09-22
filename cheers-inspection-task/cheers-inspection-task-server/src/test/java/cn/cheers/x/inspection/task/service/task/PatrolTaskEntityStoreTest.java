package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.task.InspectionTaskUpdateReqVO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatrolTaskEntityStoreTest {

    private EntityRpcApi entityRpcApi;
    private ObjectMapper objectMapper;
    private PatrolTaskEntityStore store;

    @BeforeEach
    void setUp() {
        entityRpcApi = mock(EntityRpcApi.class);
        objectMapper = new ObjectMapper();
        store = new PatrolTaskEntityStore(entityRpcApi, objectMapper);
    }

    @Test
    void createDraft_writesSelectedEquipmentAsJsonText() throws Exception {
        when(entityRpcApi.createEntity(any())).thenReturn(CommonResult.success(88L));

        Long id = store.createDraft(createReq(selectedTank()));
        assertEquals(88L, id);

        ArgumentCaptor<EntityWriteReqDTO> captor = ArgumentCaptor.forClass(EntityWriteReqDTO.class);
        verify(entityRpcApi).createEntity(captor.capture());
        assertSelectedTankWritten(captor.getValue().getFields());
        assertFacilityRefWritten(captor.getValue().getFields());
        assertUnlockedWritten(captor.getValue().getFields(), null);
        assertAnchorsWritten(captor.getValue().getFields(), "pad-a", "pad-b");
    }

    @Test
    void updateDraft_writesSelectedEquipmentAsJsonText() throws Exception {
        EntityRespDTO existing = new EntityRespDTO();
        existing.setId(88L);
        existing.setCustomFields(Map.of(
                PatrolTaskEntityStore.FIELD_DRAFT,
                Map.of("patrolExecutionMode", "UAV", PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 2)
        ));
        when(entityRpcApi.getEntity(88L, PatrolTaskEntityStore.TASK_TYPE))
                .thenReturn(CommonResult.success(existing));
        when(entityRpcApi.updateEntityFields(any())).thenReturn(CommonResult.success(true));

        InspectionTaskUpdateReqVO req = new InspectionTaskUpdateReqVO();
        req.setId(88L);
        req.setTaskName("日常储罐巡检");
        req.setFacilityId(2L);
        req.setPatrolExecutionMode("UAV");
        req.setStartStopId("pad-a");
        req.setEndStopId("pad-b");
        req.setInspectionContent(selectedTank());
        store.updateDraft(req);

        ArgumentCaptor<EntityWriteReqDTO> captor = ArgumentCaptor.forClass(EntityWriteReqDTO.class);
        verify(entityRpcApi).updateEntityFields(captor.capture());
        assertEquals(88L, captor.getValue().getId());
        assertSelectedTankWritten(captor.getValue().getFields());
        assertFacilityRefWritten(captor.getValue().getFields());
        assertUnlockedWritten(captor.getValue().getFields(), 2);
        assertAnchorsWritten(captor.getValue().getFields(), "pad-a", "pad-b");
    }

    @Test
    void require_readsSelectedEquipmentFromJsonText() {
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(88L);
        entity.setCustomFields(Map.of(
                PatrolTaskEntityStore.FIELD_CONTENT,
                "{\"customObjects\":[{\"objectId\":900104,\"objectName\":\"北罐组 1#储罐\",\"sourceType\":\"equipment\"}]}",
                PatrolTaskEntityStore.FIELD_DRAFT,
                "{\"patrolExecutionMode\":\"UAV\",\"startStopId\":\"pad-a\",\"endStopId\":\"pad-b\"}",
                PatrolTaskEntityStore.FIELD_FACILITY,
                Map.of("entityTypeCode", PatrolTaskEntityStore.FACILITY_TYPE, "id", 2L)
        ));
        when(entityRpcApi.getEntity(eq(88L), eq(PatrolTaskEntityStore.TASK_TYPE)))
                .thenReturn(CommonResult.success(entity));

        PatrolTaskDraft draft = store.require(88L);
        assertNotNull(draft.inspectionContent());
        assertEquals(1, draft.inspectionContent().getCustomObjects().size());
        assertEquals(900104L, draft.inspectionContent().getCustomObjects().get(0).getObjectId());
        assertEquals(2L, draft.facilityId());
        assertEquals(0, draft.createUnlockedStep());
        assertEquals("pad-a", draft.startStopId());
        assertEquals("pad-b", draft.endStopId());
    }

    private static InspectionTaskCreateReqVO createReq(InspectionContent content) {
        InspectionTaskCreateReqVO req = new InspectionTaskCreateReqVO();
        req.setTaskName("日常储罐巡检");
        req.setFacilityId(2L);
        req.setPatrolExecutionMode("UAV");
        req.setStartStopId("pad-a");
        req.setEndStopId("pad-b");
        req.setInspectionContent(content);
        return req;
    }

    private static InspectionContent selectedTank() {
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(900104L);
        object.setObjectName("北罐组 1#储罐");
        object.setSourceType("equipment");
        InspectionContent content = new InspectionContent();
        content.setCustomObjects(List.of(object));
        return content;
    }

    private void assertSelectedTankWritten(Map<String, Object> fields) throws Exception {
        Object raw = fields.get(PatrolTaskEntityStore.FIELD_CONTENT);
        assertInstanceOf(String.class, raw);
        InspectionContent stored = objectMapper.readValue((String) raw, InspectionContent.class);
        assertEquals(1, stored.getCustomObjects().size());
        assertEquals(900104L, stored.getCustomObjects().get(0).getObjectId());
        assertEquals("equipment", stored.getCustomObjects().get(0).getSourceType());
    }

    @SuppressWarnings("unchecked")
    private static void assertFacilityRefWritten(Map<String, Object> fields) {
        Object raw = fields.get(PatrolTaskEntityStore.FIELD_FACILITY);
        assertInstanceOf(Map.class, raw);
        Map<String, Object> ref = (Map<String, Object>) raw;
        assertEquals(PatrolTaskEntityStore.FACILITY_TYPE, ref.get("entityTypeCode"));
        assertEquals(2L, ref.get("id"));
    }

    @SuppressWarnings("unchecked")
    private static Object readUnlockedSlice(Map<String, Object> fields, String means) {
        Object raw = fields.get(PatrolTaskEntityStore.FIELD_DRAFT);
        assertInstanceOf(Map.class, raw);
        Map<String, Object> draft = (Map<String, Object>) raw;
        return PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED), means);
    }

    private static void assertUnlockedWritten(Map<String, Object> fields, Integer expected) {
        assertEquals(expected, readUnlockedSlice(fields, "UAV"));
    }

    @SuppressWarnings("unchecked")
    private static void assertAnchorsWritten(Map<String, Object> fields, String start, String end) {
        Object raw = fields.get(PatrolTaskEntityStore.FIELD_DRAFT);
        assertInstanceOf(Map.class, raw);
        Map<String, Object> draft = (Map<String, Object>) raw;
        assertEquals(start, PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_START), "UAV"));
        assertEquals(end, PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_END), "UAV"));
    }

    @Test
    void updateDraft_switchMeansKeepsPreviousStartEnd() throws Exception {
        EntityRespDTO existing = new EntityRespDTO();
        existing.setId(88L);
        existing.setCustomFields(Map.of(
                PatrolTaskEntityStore.FIELD_DRAFT,
                Map.of(
                        "patrolExecutionMode", "ROBOT",
                        PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 2,
                        PatrolTaskEntityStore.DRAFT_KEY_START, "gate-1",
                        PatrolTaskEntityStore.DRAFT_KEY_END, "gate-2")
        ));
        when(entityRpcApi.getEntity(88L, PatrolTaskEntityStore.TASK_TYPE))
                .thenReturn(CommonResult.success(existing));
        when(entityRpcApi.updateEntityFields(any())).thenReturn(CommonResult.success(true));

        InspectionTaskUpdateReqVO req = new InspectionTaskUpdateReqVO();
        req.setId(88L);
        req.setTaskName("日常储罐巡检");
        req.setFacilityId(2L);
        req.setPatrolExecutionMode("UAV");
        req.setInspectionContent(selectedTank());
        store.updateDraft(req);

        ArgumentCaptor<EntityWriteReqDTO> captor = ArgumentCaptor.forClass(EntityWriteReqDTO.class);
        verify(entityRpcApi).updateEntityFields(captor.capture());
        @SuppressWarnings("unchecked")
        Map<String, Object> draft = (Map<String, Object>) captor.getValue().getFields()
                .get(PatrolTaskEntityStore.FIELD_DRAFT);
        assertEquals("gate-1", PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_START), "ROBOT"));
        assertEquals("gate-2", PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_END), "ROBOT"));
        assertNull(PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_START), "UAV"));
        assertEquals("UAV", draft.get("patrolExecutionMode"));
        assertEquals(2, PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED), "ROBOT"));
        assertNull(PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED), "UAV", "ROBOT"));
    }

    @Test
    void updateDraft_switchMeansDoesNotCopyRouteToNext() throws Exception {
        Map<String, Object> savedRoute = Map.of(
                "stopIds", List.of("gate-1", "tank-1", "gate-2"),
                "startStopId", "gate-1",
                "endStopId", "gate-2");
        EntityRespDTO existing = new EntityRespDTO();
        existing.setId(88L);
        existing.setCustomFields(Map.of(
                PatrolTaskEntityStore.FIELD_DRAFT,
                Map.of(
                        "patrolExecutionMode", "ROBOT",
                        PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, 3,
                        "orchestrationCommitted", true,
                        "runtimeJobId", "job-robot"),
                PatrolTaskEntityStore.FIELD_ROUTE,
                savedRoute
        ));
        when(entityRpcApi.getEntity(88L, PatrolTaskEntityStore.TASK_TYPE))
                .thenReturn(CommonResult.success(existing));
        when(entityRpcApi.updateEntityFields(any())).thenReturn(CommonResult.success(true));

        InspectionTaskUpdateReqVO req = new InspectionTaskUpdateReqVO();
        req.setId(88L);
        req.setTaskName("日常储罐巡检");
        req.setFacilityId(2L);
        req.setPatrolExecutionMode("UAV");
        req.setInspectionContent(selectedTank());
        store.updateDraft(req);

        ArgumentCaptor<EntityWriteReqDTO> captor = ArgumentCaptor.forClass(EntityWriteReqDTO.class);
        verify(entityRpcApi).updateEntityFields(captor.capture());
        Map<String, Object> fields = captor.getValue().getFields();
        @SuppressWarnings("unchecked")
        Map<String, Object> draft = (Map<String, Object>) fields.get(PatrolTaskEntityStore.FIELD_DRAFT);
        Object routeRaw = fields.get(PatrolTaskEntityStore.FIELD_ROUTE);
        assertNotNull(PatrolMeansKeyedSupport.readSlice(routeRaw, "ROBOT"));
        assertNull(PatrolMeansKeyedSupport.readSlice(routeRaw, "UAV", "ROBOT"));
        assertEquals(3, PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED), "ROBOT"));
        assertNull(PatrolMeansKeyedSupport.readSlice(
                draft.get(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED), "UAV", "ROBOT"));
        assertEquals("ROBOT", draft.get(PatrolTaskEntityStore.DRAFT_KEY_COMMITTED_MEANS));
    }

    @Test
    void require_currentMeansWithoutOwnRoute_doesNotReturnOtherMeansProgress() {
        Map<String, Object> savedRoute = Map.of(
                "stopIds", List.of("gate-1", "tank-1", "gate-2"),
                "startStopId", "gate-1",
                "endStopId", "gate-2");
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(88L);
        entity.setCustomFields(Map.of(
                PatrolTaskEntityStore.FIELD_DRAFT,
                Map.of(
                        "patrolExecutionMode", "UAV",
                        PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, Map.of("ROBOT", 3),
                        PatrolTaskEntityStore.DRAFT_KEY_COMMITTED_MEANS, "ROBOT"),
                PatrolTaskEntityStore.FIELD_ROUTE,
                savedRoute
        ));
        when(entityRpcApi.getEntity(eq(88L), eq(PatrolTaskEntityStore.TASK_TYPE)))
                .thenReturn(CommonResult.success(entity));

        PatrolTaskDraft draft = store.require(88L);
        assertEquals("UAV", draft.patrolExecutionMode());
        assertEquals(0, draft.createUnlockedStep());
        assertTrue(draft.plannedRoute() == null
                || !PatrolPlannedRouteSupport.hasSavedRoute(draft.plannedRoute()));
    }

    @Test
    void require_robotMeansReadsOwnRouteAndUnlocked() {
        Map<String, Object> savedRoute = Map.of(
                "stopIds", List.of("gate-1", "tank-1", "gate-2"),
                "startStopId", "gate-1",
                "endStopId", "gate-2");
        EntityRespDTO entity = new EntityRespDTO();
        entity.setId(88L);
        entity.setCustomFields(Map.of(
                PatrolTaskEntityStore.FIELD_DRAFT,
                Map.of(
                        "patrolExecutionMode", "ROBOT",
                        PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED, Map.of("ROBOT", 3),
                        PatrolTaskEntityStore.DRAFT_KEY_ORCHESTRATION_PREVIEW_SLOTS,
                        Map.of("ROBOT", List.of(Map.of("slotId", "s1"))),
                        PatrolTaskEntityStore.DRAFT_KEY_COMMITTED_MEANS, "ROBOT"),
                PatrolTaskEntityStore.FIELD_ROUTE,
                Map.of("ROBOT", savedRoute)
        ));
        when(entityRpcApi.getEntity(eq(88L), eq(PatrolTaskEntityStore.TASK_TYPE)))
                .thenReturn(CommonResult.success(entity));

        PatrolTaskDraft draft = store.require(88L);
        assertEquals("ROBOT", draft.patrolExecutionMode());
        assertEquals(3, draft.createUnlockedStep());
        assertTrue(PatrolPlannedRouteSupport.hasSavedRoute(draft.plannedRoute()));
        assertNotNull(draft.orchestrationPreviewSlots());
    }

    @Test
    void emptyStepTreePlaceholder_skipsDefaultEmptyArray() {
        assertTrue(PatrolTaskEntityStore.isEmptyStepTreePlaceholder(List.of()));
        assertTrue(PatrolTaskEntityStore.isEmptyStepTreePlaceholder("[]"));
        assertTrue(PatrolTaskEntityStore.isEmptyStepTreePlaceholder(Map.of()));
        assertFalse(PatrolTaskEntityStore.isEmptyStepTreePlaceholder(
                Map.of("version", 1, "nodes", List.of(Map.of("nodeKey", "n1")))));
    }
}
