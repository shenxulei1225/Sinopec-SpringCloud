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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        assertUnlockedWritten(captor.getValue().getFields(), 0);
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
    private static void assertUnlockedWritten(Map<String, Object> fields, int expected) {
        Object raw = fields.get(PatrolTaskEntityStore.FIELD_DRAFT);
        assertInstanceOf(Map.class, raw);
        Map<String, Object> draft = (Map<String, Object>) raw;
        assertEquals(expected, draft.get(PatrolTaskEntityStore.DRAFT_KEY_UNLOCKED));
    }

    @SuppressWarnings("unchecked")
    private static void assertAnchorsWritten(Map<String, Object> fields, String start, String end) {
        Object raw = fields.get(PatrolTaskEntityStore.FIELD_DRAFT);
        assertInstanceOf(Map.class, raw);
        Map<String, Object> draft = (Map<String, Object>) raw;
        assertEquals(start, draft.get(PatrolTaskEntityStore.DRAFT_KEY_START));
        assertEquals(end, draft.get(PatrolTaskEntityStore.DRAFT_KEY_END));
    }
}
