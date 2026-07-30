package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entitytype.StorageTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private EntityDedicatedColumnService service;

    @Test
    void listEnabledPhysicalFields_usesFieldCodes_notInformationSchema() {
        EntityTypeDO type = new EntityTypeDO();
        type.setCode("equipment");
        type.setStorageType(StorageTypeEnum.DEDICATED.getCode());
        type.setDedicatedTableName("ent_equipment_t1");
        when(entityTypeMapper.selectByCode("equipment")).thenReturn(type);

        EntityTypeBaseFieldDO zone = new EntityTypeBaseFieldDO();
        zone.setFieldCode("FLD-BASE-equipment-REF_ZONE");
        zone.setDataType("ENTITY_REF");
        zone.setStatus(1);
        zone.setSortOrder(1);

        EntityTypeBaseFieldDO region = new EntityTypeBaseFieldDO();
        region.setFieldCode("FLD-BASE-equipment-REF_REGION");
        region.setDataType("ENTITY_REF");
        region.setStatus(1);
        region.setSortOrder(2);

        when(baseFieldMapper.selectByEntityTypeCode("equipment")).thenReturn(List.of(zone, region));

        List<EntityDedicatedColumnService.PhysicalFieldSpec> specs =
                service.listEnabledPhysicalFields("equipment");

        assertEquals(2, specs.size());
        assertEquals("FLD-BASE-equipment-REF_ZONE", specs.get(0).fieldCode());
        assertEquals("fld_base_equipment_ref_zone", specs.get(0).columnName());
        assertEquals("ENTITY_REF", specs.get(0).dataType());
        assertEquals("fld_base_equipment_ref_region", specs.get(1).columnName());

        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void applyDedicatedBaseFieldValues_convertsRefToApiShape_withoutJdbc() {
        EntityTypeBaseFieldDO zone = new EntityTypeBaseFieldDO();
        zone.setFieldCode("FLD-BASE-equipment-REF_ZONE");
        zone.setDataType("ENTITY_REF");
        zone.setStatus(1);
        when(baseFieldMapper.selectByEntityTypeCode("equipment")).thenReturn(List.of(zone));

        EntityDO entity = new EntityDO();
        entity.setEntityTypeCode("equipment");
        Map<String, Object> dedicated = new LinkedHashMap<>();
        dedicated.put("FLD-BASE-equipment-REF_ZONE", 101L);
        entity.setDedicatedBaseFieldValues(dedicated);

        Map<String, Object> baseFields = new LinkedHashMap<>();
        service.applyDedicatedBaseFieldValues(entity, baseFields);

        @SuppressWarnings("unchecked")
        Map<String, Object> ref = (Map<String, Object>) baseFields.get("FLD-BASE-equipment-REF_ZONE");
        assertEquals("zone", ref.get("entityTypeCode"));
        assertEquals(101L, ref.get("id"));
        verifyNoInteractions(jdbcTemplate);
    }
}
