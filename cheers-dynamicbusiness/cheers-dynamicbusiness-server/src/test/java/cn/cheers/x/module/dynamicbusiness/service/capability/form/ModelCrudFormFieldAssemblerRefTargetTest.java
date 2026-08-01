package cn.cheers.x.module.dynamicbusiness.service.capability.form;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModelCrudFormFieldAssemblerRefTargetTest {

    @Test
    void resolveRefTarget_prefersFieldProviderTargetWhenAssignmentOmitsIt() {
        FieldDO field = new FieldDO();
        field.setProviderCode("dynamic-entity:operating_area");

        ModelFieldAssignmentDO assign = new ModelFieldAssignmentDO();
        assign.setFieldId(1L);

        String resolved = ModelCrudFormFieldAssembler.resolveRefTargetEntityTypeCode(
                "equipment",
                field,
                assign,
                ModelCrudFormFieldAssembler.RefResolveContext.empty());

        assertEquals("operating_area", resolved);
    }

    @Test
    void resolveRefTarget_assignmentTargetWinsOverFieldProvider() {
        FieldDO field = new FieldDO();
        field.setProviderCode("dynamic-entity:operating_area");

        ModelFieldAssignmentDO assign = new ModelFieldAssignmentDO();
        assign.setTargetEntityType("facility");

        String resolved = ModelCrudFormFieldAssembler.resolveRefTargetEntityTypeCode(
                "equipment",
                field,
                assign,
                ModelCrudFormFieldAssembler.RefResolveContext.empty());

        assertEquals("facility", resolved);
    }

    @Test
    void buildFormRoot_assignmentOverridesBaseRefWithoutTarget() {
        String fieldCode = "region_ids";

        EntityTypeBaseFieldDO baseField = new EntityTypeBaseFieldDO();
        baseField.setFieldCode(fieldCode);
        baseField.setFieldName("所属区域");
        baseField.setDataType("REF_Multi");
        baseField.setLibraryFieldId(3672L);
        baseField.setStatus(1);
        baseField.setSortOrder(10);

        FieldDO field = new FieldDO();
        field.setId(3672L);
        field.setCode(fieldCode);
        field.setName("所属区域");
        field.setType("ENTITY_REF");
        field.setProviderCode("dynamic-entity:region");

        ModelFieldAssignmentDO assign = new ModelFieldAssignmentDO();
        assign.setFieldId(3672L);
        assign.setSort(10);

        Map<String, Object> root = ModelCrudFormFieldAssembler.buildFormRoot(
                41L,
                "equipment",
                true,
                List.of(assign),
                Map.of(3672L, field),
                Map.of(fieldCode, baseField),
                List.of(),
                ModelCrudFormFieldAssembler.RefResolveContext.empty(),
                Map.of());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> baseFieldDefs = (List<Map<String, Object>>) root.get("baseFieldDefs");
        assertNotNull(baseFieldDefs);
        Map<String, Object> regionField = baseFieldDefs.stream()
                .filter(item -> fieldCode.equals(item.get("fieldKey")) || fieldCode.equals(item.get("fieldCode")))
                .findFirst()
                .orElseThrow();

        assertEquals("region", regionField.get("targetEntityTypeCode"));
        @SuppressWarnings("unchecked")
        Map<String, Object> refTarget = (Map<String, Object>) regionField.get("refTarget");
        assertNotNull(refTarget);
        @SuppressWarnings("unchecked")
        Map<String, Object> binding = (Map<String, Object>) refTarget.get("capabilityBinding");
        assertNotNull(binding);
        assertEquals("region", binding.get("entityTypeCode"));
    }

    @Test
    void buildFormRoot_baseFieldDataTypeRef_mapsToRefPickerWithInferredRegion() {
        String fieldCode = "region_id";

        EntityTypeBaseFieldDO baseField = new EntityTypeBaseFieldDO();
        baseField.setFieldCode(fieldCode);
        baseField.setFieldName("所属区域");
        // 现网基础字段库常见缩写，此前会落到 input 从而界面显示裸 id
        baseField.setDataType("REF");
        baseField.setLibraryFieldId(3685L);
        baseField.setStatus(1);
        baseField.setSortOrder(10);

        FieldDO field = new FieldDO();
        field.setId(3685L);
        field.setCode(fieldCode);
        field.setName("所属区域");
        field.setType("ENTITY_REF");
        field.setProviderCode("dynamic-entity:region");

        ModelFieldAssignmentDO assign = new ModelFieldAssignmentDO();
        assign.setFieldId(3685L);
        assign.setSort(10);

        Map<String, Object> root = ModelCrudFormFieldAssembler.buildFormRoot(
                917L,
                "facility",
                true,
                List.of(assign),
                Map.of(3685L, field),
                Map.of(fieldCode, baseField),
                List.of(),
                ModelCrudFormFieldAssembler.RefResolveContext.empty(),
                Map.of());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> baseFieldDefs = (List<Map<String, Object>>) root.get("baseFieldDefs");
        assertNotNull(baseFieldDefs);
        Map<String, Object> regionField = baseFieldDefs.stream()
                .filter(item -> fieldCode.equals(item.get("fieldKey")) || fieldCode.equals(item.get("fieldCode")))
                .findFirst()
                .orElseThrow();

        assertEquals("ref-picker", regionField.get("renderAs"));
        assertEquals("ENTITY_REF", regionField.get("fieldType"));
        assertEquals("region", regionField.get("targetEntityTypeCode"));
    }

    @Test
    void normalizeFieldType_refAliases() {
        assertEquals("ENTITY_REF", ModelCrudFormFieldAssembler.normalizeFieldType("REF"));
        assertEquals("ENTITY_REF_MULTI", ModelCrudFormFieldAssembler.normalizeFieldType("REF_MULTI"));
        assertEquals("ENTITY_REF", ModelCrudFormFieldAssembler.normalizeFieldType("entity_ref"));
    }
}
