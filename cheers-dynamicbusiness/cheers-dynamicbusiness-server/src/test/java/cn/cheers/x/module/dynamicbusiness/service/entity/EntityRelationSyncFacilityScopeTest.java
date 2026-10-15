package cn.cheers.x.module.dynamicbusiness.service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityRelationSyncFacilityScopeTest {

    @Test
    void facilityScopeIsNotCopiedIntoRelationTable() {
        assertFalse(EntityRelationSyncServiceImpl.copiesRefIntoRelationTable("facility_id"));
        assertFalse(EntityRelationSyncServiceImpl.copiesRefIntoRelationTable(" facility_id "));
        assertFalse(EntityRelationSyncServiceImpl.copiesRefIntoRelationTable(" "));
        assertTrue(EntityRelationSyncServiceImpl.copiesRefIntoRelationTable("execution_record_id"));
    }
}
