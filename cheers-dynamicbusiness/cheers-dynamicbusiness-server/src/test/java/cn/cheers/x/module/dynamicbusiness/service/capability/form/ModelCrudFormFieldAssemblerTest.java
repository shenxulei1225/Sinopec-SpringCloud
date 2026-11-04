package cn.cheers.x.module.dynamicbusiness.service.capability.form;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelCrudFormFieldAssemblerTest {

    private static final String HIDDEN =
            "{\"createVisible\":false,\"editVisible\":false,\"detailVisible\":false}";

    @Test
    void hiddenCatalogFieldStaysOffModelsThatDoNotAssignIt() {
        Map<String, Object> form = ModelCrudFormFieldAssembler.buildFormRoot(
                1929L,
                "action",
                true,
                List.of(),
                Map.of(1L, capabilityField()),
                Map.of("analysis_capability_id", capabilityBaseField()),
                List.of(),
                ModelCrudFormFieldAssembler.RefResolveContext.empty(),
                Map.of());

        assertFalse(fieldKeys(form).contains("analysis_capability_id"));
    }

    @Test
    void intelligentAnalysisModelShowsTheCapabilityField() {
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setFieldId(1L);
        assignment.setFieldCode("analysis_capability_id");
        assignment.setRequired(true);
        assignment.setSort(12);
        assignment.setValidationRules(
                "{\"createVisible\":true,\"editVisible\":true,\"detailVisible\":false}");

        Map<String, Object> form = ModelCrudFormFieldAssembler.buildFormRoot(
                1930L,
                "action",
                true,
                List.of(assignment),
                Map.of(1L, capabilityField()),
                Map.of("analysis_capability_id", capabilityBaseField()),
                List.of(),
                ModelCrudFormFieldAssembler.RefResolveContext.empty(),
                Map.of());

        Map<String, Object> field = field(form, "analysis_capability_id");
        assertEquals(Boolean.TRUE, field.get("createVisible"));
        assertEquals(Boolean.TRUE, field.get("required"));
        assertEquals(Boolean.FALSE, field.get("detailVisible"));
    }

    @Test
    void assignedHiddenFieldRemainsOnThatModel() {
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setFieldId(2L);
        assignment.setFieldCode("execution_means");
        assignment.setRequired(false);
        assignment.setSort(10);

        EntityTypeBaseFieldDO base = new EntityTypeBaseFieldDO();
        base.setFieldCode("execution_means");
        base.setFieldName("适用检查方式");
        base.setDataType("JSON");
        base.setStatus(1);
        base.setLibraryFieldId(2L);
        base.setTypeConfig(HIDDEN);

        FieldDO field = new FieldDO();
        field.setId(2L);
        field.setCode("execution_means");
        field.setName("适用检查方式");
        field.setType("JSON");

        Map<String, Object> form = ModelCrudFormFieldAssembler.buildFormRoot(
                1929L,
                "action",
                true,
                List.of(assignment),
                Map.of(2L, field),
                Map.of("execution_means", base),
                List.of(),
                ModelCrudFormFieldAssembler.RefResolveContext.empty(),
                Map.of());

        assertTrue(fieldKeys(form).contains("execution_means"));
        assertEquals(Boolean.FALSE, field(form, "execution_means").get("createVisible"));
    }

    @Test
    void collectionPointShowsFieldDescriptionInDetail() {
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setFieldId(9L);
        assignment.setFieldCode("F-remote");
        assignment.setRequired(false);
        assignment.setCollectionPoint(true);

        FieldDO field = new FieldDO();
        field.setId(9L);
        field.setCode("F-remote");
        field.setName("远程");
        field.setType("BOOLEAN");
        field.setDescription("处于远程时为 1，平台可以控制。");

        Map<String, Object> form = ModelCrudFormFieldAssembler.buildFormRoot(
                2594L,
                "equipment",
                true,
                List.of(assignment),
                Map.of(9L, field),
                Map.of(),
                List.of(),
                ModelCrudFormFieldAssembler.RefResolveContext.empty(),
                Map.of());

        Map<String, Object> item = customField(form, "F-remote");
        assertEquals("field-note", item.get("renderAs"));
        assertEquals("处于远程时为 1，平台可以控制。", item.get("description"));
        assertEquals(Boolean.TRUE, item.get("detailVisible"));
        assertEquals(Boolean.FALSE, item.get("createVisible"));
        assertEquals(Boolean.FALSE, item.get("editVisible"));
    }

    @Test
    void pointModelFormIncludesTheNoteRow() {
        ModelFieldAssignmentDO assignment = new ModelFieldAssignmentDO();
        assignment.setFieldId(3L);
        assignment.setFieldCode("point_field_code");

        FieldDO field = new FieldDO();
        field.setId(3L);
        field.setCode("point_field_code");
        field.setName("点编号");
        field.setType("TEXT");

        Map<String, Object> form = ModelCrudFormFieldAssembler.buildFormRoot(
                2600L,
                "point_binding",
                true,
                List.of(assignment),
                Map.of(3L, field),
                Map.of(),
                List.of(),
                ModelCrudFormFieldAssembler.RefResolveContext.empty(),
                Map.of());

        Map<String, Object> note = customField(form, "collectionPointNote");
        assertEquals("说明", note.get("label"));
        assertEquals("service-call", note.get("renderAs"));
        assertEquals(Boolean.TRUE, note.get("detailVisible"));
        assertEquals(Boolean.FALSE, note.get("createVisible"));
    }

    private static FieldDO capabilityField() {
        FieldDO field = new FieldDO();
        field.setId(1L);
        field.setCode("analysis_capability_id");
        field.setName("智能分析能力");
        field.setType("NUMBER");
        return field;
    }

    private static EntityTypeBaseFieldDO capabilityBaseField() {
        EntityTypeBaseFieldDO base = new EntityTypeBaseFieldDO();
        base.setFieldCode("analysis_capability_id");
        base.setFieldName("智能分析能力");
        base.setDataType("NUMBER");
        base.setStatus(1);
        base.setLibraryFieldId(1L);
        base.setSortOrder(12);
        base.setTypeConfig(HIDDEN);
        return base;
    }

    @SuppressWarnings("unchecked")
    private static List<String> fieldKeys(Map<String, Object> form) {
        List<Map<String, Object>> fields = (List<Map<String, Object>>) form.get("baseFieldDefs");
        return fields.stream().map(item -> String.valueOf(item.get("fieldKey"))).toList();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> customField(Map<String, Object> form, String fieldKey) {
        List<Map<String, Object>> fields = (List<Map<String, Object>>) form.get("customFieldDefs");
        return fields.stream()
                .filter(item -> fieldKey.equals(item.get("fieldKey")))
                .findFirst()
                .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> field(Map<String, Object> form, String fieldKey) {
        List<Map<String, Object>> fields = (List<Map<String, Object>>) form.get("baseFieldDefs");
        return fields.stream()
                .filter(item -> fieldKey.equals(item.get("fieldKey")))
                .findFirst()
                .orElseThrow();
    }
}
