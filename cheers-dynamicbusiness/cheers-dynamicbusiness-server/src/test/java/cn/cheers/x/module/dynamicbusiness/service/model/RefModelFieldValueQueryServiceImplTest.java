package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.RefModelFieldValueReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.RefModelFieldValueRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefModelFieldValueQueryServiceImplTest {

    @Test
    void returnsOnlyNamedFieldsInOrder() {
        ModelFieldAssignmentService assignments = mock(ModelFieldAssignmentService.class);
        when(assignments.getModelFields(12L)).thenReturn(List.of(
                field(8L, "夜视", "星光级", null),
                field(3L, "俯仰角", "{\"min\":-120,\"max\":60}", "°"),
                field(9L, "重量", "100", "g")
        ));
        ModelMapper models = mock(ModelMapper.class);
        ModelDO model = new ModelDO();
        model.setName("机身相机");
        when(models.selectById(12L)).thenReturn(model);
        RefModelFieldValueQueryServiceImpl service = new RefModelFieldValueQueryServiceImpl(
                mock(EntityService.class), assignments, models);

        RefModelFieldValueReqVO req = new RefModelFieldValueReqVO();
        req.setEntityTypeCode("equipment");
        req.setSourceModelId(12L);
        req.setSourceFieldIds(List.of(3L, 3L, 8L, 1L));

        RefModelFieldValueRespVO resp = service.read(req);

        assertEquals(List.of("型号", "俯仰角", "夜视", "字段 1"),
                resp.getLines().stream().map(RefModelFieldValueRespVO.Line::getLabel).toList());
        assertEquals("机身相机", resp.getLines().get(0).getValue());
        assertEquals("-120° ~ 60°", resp.getLines().get(1).getValue());
        assertEquals("星光级", resp.getLines().get(2).getValue());
        assertEquals("", resp.getLines().get(3).getValue());
    }

    @Test
    void emptyModelUsesTheCameraOnTheMainGimbal() {
        EntityService entities = mock(EntityService.class);
        EntityRespVO host = new EntityRespVO();
        host.setCustomFields(Map.of("payload_mounts", List.of(Map.of("mountPoint", "0", "equipmentId", 44))));
        EntityRespVO camera = new EntityRespVO();
        camera.setBaseFields(Map.of("modelId", 77));
        when(entities.get(9L, "equipment")).thenReturn(host);
        when(entities.get(44L, "equipment")).thenReturn(camera);

        ModelFieldAssignmentService assignments = mock(ModelFieldAssignmentService.class);
        when(assignments.getModelFields(77L)).thenReturn(List.of(field(5L, "焦距", "24", "mm")));
        ModelMapper models = mock(ModelMapper.class);
        ModelDO model = new ModelDO();
        model.setName("禅思 H30");
        when(models.selectById(77L)).thenReturn(model);

        RefModelFieldValueQueryServiceImpl service = new RefModelFieldValueQueryServiceImpl(
                entities, assignments, models);
        RefModelFieldValueReqVO req = new RefModelFieldValueReqVO();
        req.setEntityTypeCode("equipment");
        req.setHostEntityId(9L);
        req.setHostEntityTypeCode("equipment");
        req.setSourceFieldIds(List.of(5L));

        RefModelFieldValueRespVO resp = service.read(req);

        assertEquals(List.of("型号", "焦距"), resp.getLines().stream().map(RefModelFieldValueRespVO.Line::getLabel).toList());
        assertEquals(List.of("禅思 H30", "24 mm"), resp.getLines().stream().map(RefModelFieldValueRespVO.Line::getValue).toList());
        verify(assignments).getModelFields(77L);
    }

    @Test
    void saysSoWhenTheMainGimbalIsEmpty() {
        EntityService entities = mock(EntityService.class);
        EntityRespVO host = new EntityRespVO();
        host.setCustomFields(Map.of());
        when(entities.get(9L, "equipment")).thenReturn(host);
        RefModelFieldValueQueryServiceImpl service = new RefModelFieldValueQueryServiceImpl(
                entities, mock(ModelFieldAssignmentService.class), mock(ModelMapper.class));
        RefModelFieldValueReqVO req = new RefModelFieldValueReqVO();
        req.setEntityTypeCode("equipment");
        req.setHostEntityId(9L);
        req.setHostEntityTypeCode("equipment");
        req.setSourceFieldIds(List.of(5L));

        assertEquals("主云台还没有挂设备。", service.read(req).getNotice());
    }

    @Test
    void readsCustomFieldsOnTheMountedModel() {
        EntityService entities = mock(EntityService.class);
        EntityRespVO host = new EntityRespVO();
        host.setCustomFields(Map.of("payload_mounts", List.of(Map.of("mountPoint", "0", "equipmentId", 9))));
        when(entities.get(4L, "equipment")).thenReturn(host);
        EntityRespVO mounted = new EntityRespVO();
        mounted.setBaseFields(Map.of("modelId", 77));
        mounted.setCustomFields(Map.of("wide_focal_length", "不该读这台设备上的数"));
        when(entities.get(9L, "equipment")).thenReturn(mounted);
        ModelDO model = new ModelDO();
        model.setName("禅思 H30");
        ModelMapper models = mock(ModelMapper.class);
        when(models.selectById(77L)).thenReturn(model);
        ModelFieldAssignmentService assignments = mock(ModelFieldAssignmentService.class);
        when(assignments.getModelFields(77L)).thenReturn(List.of(
                field(5L, "焦距", "24", "mm", ModelFieldAssignmentRespVO.FIELD_SOURCE_CUSTOM, 2),
                field(6L, "名称", "设备自己的名字", null, ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE, 1)));

        RefModelFieldValueQueryServiceImpl service = new RefModelFieldValueQueryServiceImpl(entities, assignments, models);
        RefModelFieldValueReqVO req = new RefModelFieldValueReqVO();
        req.setEntityTypeCode("equipment");
        req.setHostEntityId(4L);
        req.setHostEntityTypeCode("equipment");

        RefModelFieldValueRespVO result = service.read(req);

        assertEquals(List.of("型号"), result.getLines().stream().map(RefModelFieldValueRespVO.Line::getLabel).toList());
        assertEquals(List.of("禅思 H30"), result.getLines().stream().map(RefModelFieldValueRespVO.Line::getValue).toList());
        assertEquals(77L, result.getModelId());
    }

    private static ModelFieldAssignmentRespVO field(long id, String name, String value, String unit) {
        return field(id, name, value, unit, null, null);
    }

    private static ModelFieldAssignmentRespVO field(long id, String name, String value, String unit,
                                                    String fieldSource, Integer sort) {
        FieldRespVO field = new FieldRespVO();
        field.setName(name);
        field.setUnit(unit);
        ModelFieldAssignmentRespVO row = new ModelFieldAssignmentRespVO();
        row.setFieldId(id);
        row.setField(field);
        row.setDefaultValue(value);
        row.setFieldSource(fieldSource);
        row.setSort(sort);
        return row;
    }
}
