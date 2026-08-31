package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 专用表物理列解析：仅以基础字段配置为准，禁止 information_schema 探列。
 */
@ExtendWith(MockitoExtension.class)
class EntityDedicatedColumnServicePhysicalFieldsTest {

    @Mock
    private EntityTypeMapper entityTypeMapper;
    @Mock
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Mock
    private FieldMapper fieldMapper;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private EntityDedicatedColumnService service;

    @Test
    void listEnabledPhysicalFields_usesFieldCodes_skipsMultiRef() {
        EntityTypeDO type = new EntityTypeDO();
        type.setCode("equipment");
        type.setStorageType(StorageTypeEnum.DEDICATED.getCode());
        type.setDedicatedTableName("ent_equipment_t1");
        when(entityTypeMapper.selectByCode("equipment")).thenReturn(type);

        EntityTypeBaseFieldDO zone = new EntityTypeBaseFieldDO();
        zone.setFieldCode("zone_id");
        zone.setDataType("ENTITY_REF");
        zone.setStatus(1);
        zone.setSortOrder(1);

        EntityTypeBaseFieldDO regionMulti = new EntityTypeBaseFieldDO();
        regionMulti.setFieldCode("region_ids");
        regionMulti.setDataType("REF_Multi");
        regionMulti.setStatus(1);
        regionMulti.setSortOrder(2);

        when(baseFieldMapper.selectByEntityTypeCode("equipment")).thenReturn(List.of(zone, regionMulti));

        List<EntityDedicatedColumnService.PhysicalFieldSpec> specs =
                service.listEnabledPhysicalFields("equipment");

        assertEquals(1, specs.size());
        assertEquals("zone_id", specs.get(0).fieldCode());
        assertEquals("zone_id", specs.get(0).columnName());
        assertEquals("ENTITY_REF", specs.get(0).dataType());

        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void applyDedicatedBaseFieldValues_convertsRefToApiShape_withoutJdbc() {
        EntityTypeBaseFieldDO zone = new EntityTypeBaseFieldDO();
        zone.setFieldCode("zone_id");
        zone.setDataType("ENTITY_REF");
        zone.setStatus(1);
        when(baseFieldMapper.selectByEntityTypeCode("equipment")).thenReturn(List.of(zone));

        EntityDO entity = new EntityDO();
        entity.setEntityTypeCode("equipment");
        Map<String, Object> dedicated = new LinkedHashMap<>();
        dedicated.put("zone_id", 101L);
        entity.setDedicatedBaseFieldValues(dedicated);

        Map<String, Object> baseFields = new LinkedHashMap<>();
        service.applyDedicatedBaseFieldValues(entity, baseFields);

        @SuppressWarnings("unchecked")
        Map<String, Object> ref = (Map<String, Object>) baseFields.get("zone_id");
        assertEquals("zone", ref.get("entityTypeCode"));
        assertEquals(101L, ref.get("id"));
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void applyDedicatedBaseFieldValues_prefersProviderCodeOverFieldCodeInfer() {
        EntityTypeBaseFieldDO methodTemplate = new EntityTypeBaseFieldDO();
        methodTemplate.setFieldCode("method_template_id");
        methodTemplate.setDataType("REF");
        methodTemplate.setLibraryFieldId(5405L);
        methodTemplate.setStatus(1);
        when(baseFieldMapper.selectByEntityTypeCode("inspection_item")).thenReturn(List.of(methodTemplate));

        FieldDO libraryField = new FieldDO();
        libraryField.setId(5405L);
        libraryField.setCode("method_template_id");
        libraryField.setProviderCode("dynamic-entity:inspection_method");
        when(fieldMapper.selectBatchIds(anyCollection())).thenReturn(List.of(libraryField));

        EntityDO entity = new EntityDO();
        entity.setEntityTypeCode("inspection_item");
        Map<String, Object> dedicated = new LinkedHashMap<>();
        dedicated.put("method_template_id", 1L);
        entity.setDedicatedBaseFieldValues(dedicated);

        Map<String, Object> baseFields = new LinkedHashMap<>();
        service.applyDedicatedBaseFieldValues(entity, baseFields);

        @SuppressWarnings("unchecked")
        Map<String, Object> ref = (Map<String, Object>) baseFields.get("method_template_id");
        // 不得推断成 method_template；须用字段库 provider → inspection_method
        assertEquals("inspection_method", ref.get("entityTypeCode"));
        assertEquals(1L, ref.get("id"));
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void extractPhysicalValuesAndStrip_wrapsJsonStringAsPgObjectForJsonbColumn() {
        EntityTypeDO type = new EntityTypeDO();
        type.setCode("sop");
        type.setStorageType(StorageTypeEnum.DEDICATED.getCode());
        type.setDedicatedTableName("ent_sop_t1");
        when(entityTypeMapper.selectByCode("sop")).thenReturn(type);

        EntityTypeBaseFieldDO tree = new EntityTypeBaseFieldDO();
        tree.setFieldCode("action_tree_json");
        tree.setDataType("TEXT");
        tree.setStatus(1);
        when(baseFieldMapper.selectByEntityTypeCode("sop")).thenReturn(List.of(tree));

        Map<String, Object> custom = new LinkedHashMap<>();
        custom.put(
                "action_tree_json",
                "[{\"nodeKey\":\"n1\",\"actionId\":\"act-arrive\",\"order\":1}]");

        Map<String, Object> physical = service.extractPhysicalValuesAndStrip("sop", custom);

        assertEquals(1, physical.size());
        Object val = physical.get("action_tree_json");
        assertInstanceOf(PGobject.class, val);
        assertEquals("jsonb", ((PGobject) val).getType());
        assertEquals(true, custom.isEmpty());
        verifyNoInteractions(jdbcTemplate);
    }
}
