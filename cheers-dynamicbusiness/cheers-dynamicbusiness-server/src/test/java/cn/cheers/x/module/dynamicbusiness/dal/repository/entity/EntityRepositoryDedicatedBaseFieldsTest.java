package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService.PhysicalFieldSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 本页一次 SELECT 核心列 + 基础字段列：空 IN、保序、单次 query。
 */
@ExtendWith(MockitoExtension.class)
class EntityRepositoryDedicatedBaseFieldsTest {

    @Mock
    private EntityMapper entityMapper;
    @Mock
    private EntityTableNameHandler entityTableNameHandler;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private EntityDedicatedColumnService entityDedicatedColumnService;

    @InjectMocks
    private EntityRepositoryImpl repository;

    @Test
    void emptyOrderedIds_returnsEmpty_withoutQuery() {
        assertTrue(repository.findByIdsWithDedicatedBaseFields(List.of(), "equipment").isEmpty());
        assertTrue(repository.findByIdsWithDedicatedBaseFields(null, "equipment").isEmpty());
        verify(jdbcTemplate, never()).queryForList(anyString(), any(Object[].class));
        verify(entityDedicatedColumnService, never()).listEnabledPhysicalFields(anyString());
    }

    @Test
    void oneSelect_fillsDedicatedValues_andPreservesOrder() {
        when(entityDedicatedColumnService.listEnabledPhysicalFields("equipment")).thenReturn(List.of(
                new PhysicalFieldSpec("FLD-BASE-equipment-REF_ZONE", "fld_base_equipment_ref_zone", "ENTITY_REF"),
                new PhysicalFieldSpec("FLD-BASE-equipment-REF_REGION", "fld_base_equipment_ref_region", "ENTITY_REF")
        ));
        when(entityTableNameHandler.resolvePhysicalTableName("equipment")).thenReturn("ent_equipment_t1");

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("id", 2L);
        row2.put("entity_type_code", "equipment");
        row2.put("model_id", 10L);
        row2.put("name", "b");
        row2.put("code", "B");
        row2.put("status", 1);
        row2.put("parent_id", null);
        row2.put("domain", "d");
        row2.put("sort", 2);
        row2.put("custom_fields", "{}");
        row2.put("fld_base_equipment_ref_zone", 100L);
        row2.put("fld_base_equipment_ref_region", 200L);

        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("id", 1L);
        row1.put("entity_type_code", "equipment");
        row1.put("model_id", 10L);
        row1.put("name", "a");
        row1.put("code", "A");
        row1.put("status", 1);
        row1.put("parent_id", null);
        row1.put("domain", "d");
        row1.put("sort", 1);
        row1.put("custom_fields", "{}");
        row1.put("fld_base_equipment_ref_zone", 101L);
        row1.put("fld_base_equipment_ref_region", null);

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class))).thenReturn(List.of(row2, row1));

        List<EntityDO> result = repository.findByIdsWithDedicatedBaseFields(List.of(1L, 2L), "equipment");

        assertEquals(List.of(1L, 2L), result.stream().map(EntityDO::getId).toList());
        assertEquals(101L, result.get(0).getDedicatedBaseFieldValues().get("FLD-BASE-equipment-REF_ZONE"));
        assertEquals(100L, result.get(1).getDedicatedBaseFieldValues().get("FLD-BASE-equipment-REF_ZONE"));
        assertEquals(200L, result.get(1).getDedicatedBaseFieldValues().get("FLD-BASE-equipment-REF_REGION"));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForList(sqlCaptor.capture(), eq(new Object[]{1L, 2L}));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("fld_base_equipment_ref_zone"));
        assertTrue(sql.contains("fld_base_equipment_ref_region"));
        assertTrue(sql.contains("custom_fields"));
        assertTrue(sql.contains("FROM ent_equipment_t1"));
        assertTrue(sql.contains("deleted = false"));
    }
}
