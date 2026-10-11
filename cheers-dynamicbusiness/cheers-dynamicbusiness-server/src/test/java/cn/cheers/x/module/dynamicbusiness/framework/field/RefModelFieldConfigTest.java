package cn.cheers.x.module.dynamicbusiness.framework.field;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefModelFieldConfigTest {

    @Test
    void keepsCatalogModelAndNamedFields() {
        RefModelFieldConfig.Value value = RefModelFieldConfig.parse(
                "{\"entityTypeCode\":\"equipment\",\"sourceModelId\":12,\"sourceFieldIds\":[3,3,8]}");

        assertEquals("equipment", value.entityTypeCode());
        assertEquals(12L, value.sourceModelId());
        assertEquals(List.of(), value.sourceFieldIds());
    }

    @Test
    void allowsEmptyModelForTheMountedGimbal() {
        RefModelFieldConfig.Value value = RefModelFieldConfig.require(
                "{\"entityTypeCode\":\"equipment\",\"sourceModelId\":null,\"sourceFieldIds\":[5]}",
                "equipment");

        assertNull(value.sourceModelId());
        assertEquals(List.of(), value.sourceFieldIds());
        RefModelFieldConfig.Value again = RefModelFieldConfig.parse(RefModelFieldConfig.write(value));
        assertEquals(value, again);
    }

    @Test
    void keepsAConfigThatOnlyNamesTheModel() {
        RefModelFieldConfig.Value value = RefModelFieldConfig.require(
                "{\"entityTypeCode\":\"equipment\",\"sourceModelId\":1,\"sourceFieldIds\":[]}",
                "equipment");
        assertEquals(1L, value.sourceModelId());
        assertEquals(List.of(), value.sourceFieldIds());
    }

    @Test
    void rejectsAConfigWithoutACatalog() {
        ServiceException error = assertThrows(ServiceException.class, () -> RefModelFieldConfig.require(
                "{\"sourceModelId\":1}",
                null));
        assertEquals(400, error.getCode());
    }
}
