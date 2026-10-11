package cn.cheers.x.module.dynamicbusiness.framework.tenant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantPhysicalTableNamesTest {

    @Test
    void placeholderIsNotAnAssociationTable() {
        assertFalse(TenantPhysicalTableNames.isIsolatedAssociationBase("?"));
    }

    @Test
    void categoryRelationBaseIsIsolated() {
        assertTrue(TenantPhysicalTableNames.isIsolatedAssociationBase("dynamic_entity_category_relation"));
        assertTrue(TenantPhysicalTableNames.isIsolatedAssociationBase("dynamic_entity_category_relation_t1"));
        assertTrue(TenantPhysicalTableNames.isIsolatedAssociationBase("dynamic_model_model_relation"));
        assertTrue(TenantPhysicalTableNames.isIsolatedAssociationBase("dynamic_model_model_relation_t1"));
    }
}
