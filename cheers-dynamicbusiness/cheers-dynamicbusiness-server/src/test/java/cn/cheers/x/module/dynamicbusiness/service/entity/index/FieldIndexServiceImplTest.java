package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityFieldIndexDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldIndexServiceImplTest {

    @Mock
    private FieldMapper fieldMapper;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Mock
    private EntityRepository entityRepository;
    @Mock
    private EntityFieldIndexMapper entityFieldIndexMapper;

    @InjectMocks
    private FieldIndexServiceImpl service;

    @Test
    void createFieldIndex_deletesThenBatchInsertsThisFieldOnly() {
        ModelDO model = new ModelDO();
        model.setEntityTypeCode("equipment");
        when(modelMapper.selectById(1L)).thenReturn(model);

        FieldDO field = new FieldDO();
        field.setId(9L);
        field.setCode("record_time");
        field.setType("DATETIME");
        when(fieldMapper.selectByCode("record_time")).thenReturn(field);

        EntityDO entity = new EntityDO();
        entity.setId(11L);
        entity.setModelId(1L);
        entity.setCustomFields(Map.of("record_time", "2026-09-16 10:00:00"));
        when(entityRepository.findByModelId(1L, "equipment")).thenReturn(List.of(entity));

        service.createFieldIndex(1L, "record_time", "DATETIME");

        verify(entityFieldIndexMapper).deleteByModelIdAndFieldCode(1L, "record_time");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<EntityFieldIndexDO>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(entityFieldIndexMapper).insertBatch(captor.capture(), eq(500));
        assertEquals(1, captor.getValue().size());
        EntityFieldIndexDO row = captor.getValue().iterator().next();
        assertEquals(11L, row.getEntityId());
        assertEquals("record_time", row.getFieldCode());
    }

    @Test
    void isFieldFilterable_doesNotRequireSearchable() {
        FieldDO field = new FieldDO();
        field.setId(9L);
        field.setCode("record_time");
        field.setType("DATETIME");
        when(fieldMapper.selectByCode("record_time")).thenReturn(field);

        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setIsSearchable(false);
        assignment.setIsFilterable(true);
        when(modelFieldAssignmentMapper.selectByModelIdAndFieldId(1L, 9L)).thenReturn(assignment);

        assertTrue(service.isFieldFilterable(1L, "record_time"));
        assertFalse(service.isFieldSearchable(1L, "record_time"));
    }

    @Test
    void isFieldFilterable_rejectsTextEvenIfFlagDirty() {
        FieldDO field = new FieldDO();
        field.setId(8L);
        field.setCode("remark");
        field.setType("TEXT");
        when(fieldMapper.selectByCode("remark")).thenReturn(field);

        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setIsSearchable(true);
        assignment.setIsFilterable(true);
        when(modelFieldAssignmentMapper.selectByModelIdAndFieldId(1L, 8L)).thenReturn(assignment);

        assertFalse(service.isFieldFilterable(1L, "remark"));
        assertTrue(service.isFieldSearchable(1L, "remark"));
    }
}
