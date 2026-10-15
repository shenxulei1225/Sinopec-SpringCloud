package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityFieldAvailabilityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.capability.projection.CapabilityBlockProjectionBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityFieldUniqueCheckTest {

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private EntityServiceImpl service;

    @Test
    void name_staysAvailable_whenAnotherRowAlreadyUsesIt() {
        EntityFieldAvailabilityRespVO result = service.checkFieldUnique(
                "equipment", 1L, "name", "1号泵", null);

        assertTrue(result.getAvailable());
        verify(entityRepository, never()).existsByExactName("equipment", 1L, "1号泵", null);
    }

    @Test
    void code_stillRejectsADuplicateInTheSameTable() {
        when(entityRepository.existsByExactCode("equipment", "EQ-1", null)).thenReturn(true);

        EntityFieldAvailabilityRespVO result = service.checkFieldUnique(
                "equipment", null, "code", "EQ-1", null);

        assertFalse(result.getAvailable());
        assertEquals("编码已存在", result.getMessage());
    }

    @Test
    void entityForm_checksCodeOnly() {
        Map<String, Object> projection = CapabilityBlockProjectionBuilder.buildDynamicEntity(
                "equipment", "list", 1L, List.of(), List.of(), List.of(), List.of());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> checks = (List<Map<String, Object>>) projection.get("asyncChecks");
        assertEquals(1, checks.size());
        assertEquals("code", checks.get(0).get("fieldKey"));
    }
}
