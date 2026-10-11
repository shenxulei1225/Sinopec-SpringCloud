package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelModelRelationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityModelRelationSyncTest {

    @Mock
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Mock
    private FieldMapper fieldMapper;
    @Mock
    private ModelModelRelationService modelModelRelationService;

    @InjectMocks
    private EntityModelRelationSync sync;

    @Test
    void outgoingSaveReplacesTargetsAndStripsTheKey() {
        assign(7L, "applicable_host_models", EntityModelRelationSync.DIRECTION_OUT);
        Map<String, Object> persisted = new LinkedHashMap<>();
        persisted.put("applicable_host_models", List.of(22L));
        persisted.put("name_note", "keep");
        Map<String, Object> submitted = new LinkedHashMap<>(persisted);

        sync.stripPersisted(11L, persisted);
        sync.syncSubmitted("equipment", 11L, submitted);

        assertFalse(persisted.containsKey("applicable_host_models"));
        assertEquals("keep", persisted.get("name_note"));
        verify(modelModelRelationService).replaceTargets("equipment", 11L, List.of(22L));
    }

    @Test
    void incomingSaveReplacesSources() {
        assign(8L, "mountable_device_models", EntityModelRelationSync.DIRECTION_IN);
        Map<String, Object> submitted = new LinkedHashMap<>();
        submitted.put("mountable_device_models", List.of(31L));

        sync.syncSubmitted("equipment", 22L, submitted);

        verify(modelModelRelationService).replaceSources("equipment", 22L, List.of(31L));
    }

    @Test
    void missingKeyDoesNotClearRelations() {
        assign(7L, "applicable_host_models", EntityModelRelationSync.DIRECTION_OUT);
        sync.syncSubmitted("equipment", 11L, Map.of("name_note", "only"));
        verify(modelModelRelationService, never()).replaceTargets(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void missingDirectionRejects() {
        assign(7L, "applicable_host_models", "");
        Map<String, Object> submitted = Map.of("applicable_host_models", List.of(22L));
        assertThrows(ServiceException.class, () -> sync.syncSubmitted("equipment", 11L, submitted));
    }

    @Test
    void readOutgoingUsesTargetList() {
        assign(7L, "applicable_host_models", EntityModelRelationSync.DIRECTION_OUT);
        when(modelModelRelationService.listTargetModelIds(11L, "equipment", "equipment"))
                .thenReturn(List.of(22L));
        assertEquals(List.of(22L), sync.readForModel("equipment", 11L).get("applicable_host_models"));
    }

    private void assign(Long fieldId, String code, String direction) {
        when(modelFieldAssignmentMapper.selectByModelId(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(
                ModelFieldAssignmentDO.builder().fieldId(fieldId).build()));
        when(fieldMapper.selectById(fieldId)).thenReturn(FieldDO.builder()
                .id(fieldId)
                .code(code)
                .type("MODEL_RELATION")
                .semanticType(direction)
                .build());
    }
}
